package utils;

import common.Headers;
import common.KafkaHelper;
import common.Message;
import config.DefaultKafkaConfig;
import config.Producer;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@UtilityClass
public class KafkaUtils {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public static ProducerRecord<String, Message> generateProducerRecord(Producer producer, Message message, boolean specific) {
        return new ProducerRecord<>(
                specific ? producer.getSpecificTopic() : producer.getGroupTopic(),
                UUID.randomUUID().toString(),
                message
        );
    }

    public static ProducerRecord<String, Message> generateProducerRecord(UUID requestId, Producer producer, Message message, boolean specific) {
        ProducerRecord<String, Message> producerRecord = new ProducerRecord<>(specific ? producer.getSpecificTopic()
                : producer.getGroupTopic(), message);
        producerRecord.headers().add(Headers.REQUEST_ID.name(), KafkaHelper.uuidToBytes(requestId));
        return producerRecord;
    }

    public static ProducerRecord<String, Message> generateProducerRecord(String topic, String destination,
                                                                         DefaultKafkaConfig config, Message message) {
        ProducerRecord<String, Message> producerRecord = new ProducerRecord<>(topic, message);
        producerRecord.headers().add(Headers.REQUEST_ID.name(), KafkaHelper.uuidToBytes(UUID.randomUUID()));
        producerRecord.headers().add(Headers.REQUEST_ID.name(), KafkaHelper.uuidToBytes(UUID.randomUUID()));
        producerRecord.headers().add(Headers.DESTINATION_INSTANCE.name(), destination.getBytes(CHARSET));
        producerRecord.headers().add(Headers.SOURCE_INSTANCE.name(),
                config.getSpecificConsumer().getGroupId().getBytes(CHARSET));
        producerRecord.headers().add(Headers.REPLY_TOPIC.name(),
                config.getSpecificConsumer().getTopic().getBytes(CHARSET));
        return producerRecord;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getResponseFromCallbackContext(common.simple.CallbackContext callbackContext) {
        return (T) callbackContext.getResponseHolder().getMessage();
    }
}
