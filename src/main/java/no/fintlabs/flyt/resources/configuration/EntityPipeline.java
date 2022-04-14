package no.fintlabs.flyt.resources.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;

@AllArgsConstructor
public class EntityPipeline {

    @Getter
    private EntityTopicNameParameters topicNameParameters;
    @Getter
    private String fintEndpoint;
    @Getter
    private String selfLinkKeyFilter;

}
