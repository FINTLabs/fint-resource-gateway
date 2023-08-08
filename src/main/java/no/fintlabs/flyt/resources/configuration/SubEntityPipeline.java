package no.fintlabs.flyt.resources.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;

@Getter
@AllArgsConstructor
public class SubEntityPipeline {
    private EntityTopicNameParameters topicNameParameters;
    private String subEntityName;
    private String keySuffixFilter;
}
