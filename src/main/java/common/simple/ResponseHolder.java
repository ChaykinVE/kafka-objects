package common.simple;

import common.KafkaHeaderAccessor;
import common.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class ResponseHolder {
    private Message message;
    private Map<String, Object> headers;
    private KafkaHeaderAccessor rootHeadersForResponse;

    public ResponseHolder(Message message, Map<String, Object> headers) {
        this.message = message;
        this.headers = headers;
    }
}
