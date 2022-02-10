package common.simple.error;

import common.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true, fluent = true)

public class SimpleError implements Message{
    private String exception;
    private String exceptionMessage;

    public SimpleError(Exception exception) {
        this.exceptionMessage = exception.getMessage();
        this.exception = getStackTrace(exception);
    }
}
