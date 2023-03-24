package no.fintlabs.flyt.resources.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;

@AllArgsConstructor
public class SubEntityPipeline {
    @Getter
    private EntityTopicNameParameters topicNameParameters;
    @Getter
    private String subEntityName;
    @Getter
    private String keySuffixFilter;
}
