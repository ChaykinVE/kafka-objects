package config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
public class DefaultKafkaConfig implements BaseConfig {
    private Map<String, Object> properties;
    //@Value("${kafka.bootstrapServers}")
    private String bootstrapServers;
    //@Value("${kafka.errorTopic}")
    private String errorTopic;
    //@Value("${kafka.replyOnError}")
    private Boolean replyOnError = true;
    //@Value("${kafka.groupConsumer}")
    private Consumer groupConsumer;
    //@Value("${kafka.specificConsumer}")
    private Consumer specificConsumer;

    public DefaultKafkaConfig() {this.properties = new HashMap<>();}

    @Override
    public BaseProducers producers() {
        return null;
    }

    @Data

    public static class Consumer {
        //@Value("${groupId}")
        private String groupId;
        private Long pollTimeOut;
        private Integer concurrency;
        private String topic;

        private Map<String, Object> properties = new HashMap<>();
    }
}
