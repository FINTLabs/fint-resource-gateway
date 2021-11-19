package no.fintlabs.kafka;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class EntityPipelineConfiguration {

    private String kafkaTopic;
    private String fintEndpoint;
    private int topicPartitions;
    private int topicReplications;

}
