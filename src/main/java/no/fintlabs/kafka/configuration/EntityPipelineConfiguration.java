package no.fintlabs.kafka.configuration;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class EntityPipelineConfiguration {

    private String resourceReference;
    private String kafkaTopic;
    private String fintEndpoint;

}
