package config;

import lombok.Data;

@Data
public class Producer {
    private String groupTopic;
    private String specificTopic;
}
