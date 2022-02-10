package common.simple;

import common.KafkaHelper;
import common.Message;
import config.DefaultKafkaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.GenericMessage;

import java.util.Map;

public abstract class SimpleKafkaListener {
    @Value("${service.name}")
    private String serviceName = "";

    @Autowired
    private SimpleKafkaTemplate<String, Message> kafkaTemplate;
    @Autowired
    protected DefaultKafkaConfig kafkaConfig;

    public SimpleKafkaTemplate<String, Message> simpleKafkaTemplate() {
        return kafkaTemplate;
    }

    protected <T extends Message> org.springframework.messaging.Message<T> genericMessage(T message, Map<String, Object> headers) {
        return new GenericMessage<>(message, KafkaHelper.correlateHeaders(headers, kafkaConfig.getSpecificConsumer().getGroupId()));
    }
}
