package common.simple.error;

import common.simple.error.SimpleError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true, fluent = true)

public class SimpleErrorListenerContext {
    private UUID requestId;
    private SimpleError simpleError;
    private Map<String, Object> headers;
}
