package common;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.RecordInterceptor;

@Slf4j
public class CustomRecordInterceptor<K, V> implements RecordInterceptor<K, V> {
    @Override
    public ConsumerRecord<K, V> intercept(ConsumerRecord<K, V> record) {
        log.trace("Recieved message: datetime: {}; Headers: {}; Request data: {}",
                record.timestamp(), record.headers(), record.value());
        return record;
    }
}
