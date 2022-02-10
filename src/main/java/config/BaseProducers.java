package config;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public abstract class BaseProducers {

    private List<Timeout> timeouts;

    @Data
    public static class Timeout {
        @NotEmpty
        private String dto;
        @Min(value = 1000L)
        private Long timeout;
    }
}
