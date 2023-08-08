package no.fintlabs.flyt.resources.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class EntityPipeline {

    private EntityTopicNameParameters topicNameParameters;
    private String fintEndpoint;
    private String selfLinkKeyFilter;

    private SubEntityPipeline subEntityPipeline;

    public Optional<SubEntityPipeline> getSubEntityPipeline() {
        return Optional.ofNullable(subEntityPipeline);
    }
}
