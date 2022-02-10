package common.simple;

import common.Headers;
import common.KafkaHelper;
import common.Message;
import common.simple.error.SimpleErrorListenerContext;
import common.simple.error.SimpleErrorListenerContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class SimpleKafkaTemplate<K, V> {
    private KafkaTemplate<K, V> kafkaTemplate;
    private ConcurrentMap<UUID, AwaitingForResponseRequest> uuidToAwaitingForResponseRequestMap = new ConcurrentHashMap<>();

    @Value("${simple.threshold:60000}")
    private Long THRESHOLD_MS;

    public SimpleKafkaTemplate(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public KafkaTemplate<K, V> defaultKafkaTemplate() {
        return kafkaTemplate;
    }

    public CompletableFuture<CallbackContext> sendMessage (ProducerRecord<K, V> producerRecord) {
        CompletableFuture<CallbackContext> future = new CompletableFuture<>();
        this.sendMessage(producerRecord, future::complete, simpleErrorListenerContext ->
                future.completeExceptionally(new SimpleErrorListenerContextHolder(simpleErrorListenerContext)),
                future::completeExceptionally);
        return future;
    }

    private void sendMessage(ProducerRecord<K,V> producerRecord, Consumer<CallbackContext> successCallback,
                            Consumer<SimpleErrorListenerContext> simpleErrorListener, Consumer<Throwable> timeoutCallback) {
        UUID requestId;
        Iterator<Header> requestIdIterator = producerRecord.headers().headers(Headers.REQUEST_ID.name()).iterator();
        if (requestIdIterator.hasNext()) {
            requestId = KafkaHelper.bytesToUuid(requestIdIterator.next().value());
        } else {
            requestId = UUID.randomUUID();
        }
        kafkaTemplate.send(producerRecord).completable()
                .thenAccept(x -> createAwaitingForResponseRequest(producerRecord, requestId, successCallback,
                        simpleErrorListener, timeoutCallback));
    }

    private void createAwaitingForResponseRequest(ProducerRecord<K,V> producerRecord, UUID requestId,
                                                 Consumer<CallbackContext> successCallback,
                                                 Consumer<SimpleErrorListenerContext> simpleErrorListener,
                                                 Consumer<Throwable> timeoutCallback) {
        AwaitingForResponseRequest request = new AwaitingForResponseRequest();
        request.setSuccessCallback(successCallback);
        request.setErrorCallback(simpleErrorListener);
        Timer timer = new Timer(requestId.toString(), true);
        Long timeout;
        Iterator<Header> timeoutIterator = producerRecord.headers().headers(Headers.AWAIT_TIMEOUT.name()).iterator();
        if (timeoutIterator.hasNext()) {
            timeout = KafkaHelper.bytesToLong(timeoutIterator.next().value());
        } else {
            timeout = THRESHOLD_MS;
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                uuidToAwaitingForResponseRequestMap.remove(requestId);
                timeoutCallback.accept(new TimeoutException(String.format(
                        "response from %s was not received in %s ms", producerRecord.topic(), timeout
                )));
            }
        }, timeout);
        request.setTimer(timer);
        this.uuidToAwaitingForResponseRequestMap.put(requestId, request);
    }

    public void updateRequestsResponses(UUID requestId, Message message, Map<String, Object> headers) {
        log.debug("Process response for requestId {}", requestId);
        AwaitingForResponseRequest awaitingForResponseRequest = uuidToAwaitingForResponseRequestMap.remove(requestId);
        if (awaitingForResponseRequest != null) {
            awaitingForResponseRequest.getTimer().cancel();
            ResponseHolder responseHolder = new ResponseHolder().setHeaders(headers).setMessage(message);
            awaitingForResponseRequest.getSuccessCallback().accept(new CallbackContext().setResponseHolder(responseHolder));
        }
        else {
            log.error("Requests awaiting for response were not found for requestId {}", requestId);
        }
    }
}
