package common.simple.config;

import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import common.Message;
import common.simple.SimpleKafkaTemplate;
import config.BaseConfig;
import config.DefaultKafkaConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
public class SimpleKafkaConfiguration {
    @Autowired
    private BaseConfig baseConfig;

    @Bean
    public SimpleKafkaTemplate<String, Message> simpleKafkaTemplate() {
        return new SimpleKafkaTemplate<>(simpleMessageKafkaTemplate());
    }

    @NotNull
    private KafkaTemplate<String, Message> simpleMessageKafkaTemplate() {
        return new KafkaTemplate<>(simpleMessageProducerFactory());
    }

    @NotNull
    private ProducerFactory<String, Message> simpleMessageProducerFactory() {
        return new DefaultKafkaProducerFactory<>(simpleMessageProducerConfigs());
    }

    @NotNull
    private Map<String, Object> simpleMessageProducerConfigs() {
        DefaultKafkaConfig defaultKafkaConfig = (DefaultKafkaConfig) baseConfig;
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, defaultKafkaConfig.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonValueSerializer.class);
        if (!defaultKafkaConfig.getProperties().isEmpty()) {
            props.putAll(defaultKafkaConfig.getProperties());
        }
        return props;
    }
}
