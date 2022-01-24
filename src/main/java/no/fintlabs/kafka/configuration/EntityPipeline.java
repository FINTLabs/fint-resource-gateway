package no.fintlabs.kafka.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class EntityPipeline {

    @Getter
    private String kafkaTopic;
    @Getter
    private String fintEndpoint;
    @Getter
    private String selfLinkKeyFilter;

}
