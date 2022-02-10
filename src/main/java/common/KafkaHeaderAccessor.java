package common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.kafka.common.header.Header;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static common.Headers.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true, fluent = true)
public class KafkaHeaderAccessor {
    private String sourceInstance;
    private String destinationInstance;
    private String replyTopic;
    private UUID messageId;
    private UUID requestId;
    private Long chainAwaitTimeout;

    public static KafkaHeaderAccessor ofMap(Map<String, Object> headers) {
        return new KafkaHeaderAccessor()
                .requestId(KafkaHelper.bytesToUuid((byte[]) headers.getOrDefault(REQUEST_ID.name(), null)))
                .messageId(KafkaHelper.bytesToUuid((byte[]) headers.getOrDefault(MESSAGE_ID.name(), null)))
                .replyTopic(KafkaHelper.bytesArrayToString((byte[]) headers.getOrDefault(REPLY_TOPIC.name(), null)))
                .sourceInstance(KafkaHelper.bytesArrayToString((byte[]) headers.getOrDefault(SOURCE_INSTANCE.name(), null)))
                .chainAwaitTimeout(KafkaHelper.bytesToLong((byte[]) headers.getOrDefault(CHAIN_AWAIT_TIMEOUT.name(), null)))
                .destinationInstance(KafkaHelper.bytesArrayToString((byte[]) headers.getOrDefault(DESTINATION_INSTANCE.name(), null)));

    }

    public Map<String, Object> toMap() {
        Map<String, Object> headerMap = new HashMap<>();
        if (sourceInstance != null) headerMap.put(SOURCE_INSTANCE.name(), sourceInstance);
        if (destinationInstance != null) headerMap.put(DESTINATION_INSTANCE.name(), destinationInstance);
        if (replyTopic != null) headerMap.put(REPLY_TOPIC.name(), replyTopic);
        if (messageId != null) headerMap.put(MESSAGE_ID.name(), messageId);
        if (requestId != null) headerMap.put(REQUEST_ID.name(), requestId);
        if (chainAwaitTimeout != null) headerMap.put(CHAIN_AWAIT_TIMEOUT.name(), chainAwaitTimeout);
        return headerMap;
    }

    public static KafkaHeaderAccessor ofHeaders(org.apache.kafka.common.header.Headers headers) {
        Map<String, Object> headerMap = new HashMap<>();
        for (Header header : headers.toArray()) headerMap.put(header.key(), header.value());
        return ofMap(headerMap);
    }
}
