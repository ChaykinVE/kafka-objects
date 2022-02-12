package dto.requestservice;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import common.Message;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Accessors(fluent = true, chain = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DeleteRequestResponseDto implements Message {
    @NotNull
    UUID requestId;

    @NotNull
    Boolean success;

    Throwable error;
}
