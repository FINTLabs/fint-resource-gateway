package no.fintlabs.kafka.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import no.fintlabs.kafka.entity.EntityTopicNameParameters;

@AllArgsConstructor
public class EntityPipeline {

    @Getter
    private EntityTopicNameParameters topicNameParameters;
    @Getter
    private String fintEndpoint;
    @Getter
    private String selfLinkKeyFilter;

}
