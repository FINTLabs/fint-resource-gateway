package no.fintlabs.kafka.configuration;

import no.fintlabs.kafka.topic.DomainContext;
import no.fintlabs.kafka.topic.TopicService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class EntityPipelineFactory {

    private final TopicService topicService;

    public EntityPipelineFactory(TopicService topicService) {
        this.topicService = topicService;
    }

    public EntityPipeline create(EntityPipelineConfiguration configuration) {

        String topic = (
                StringUtils.isNotEmpty(configuration.getKafkaTopic())
                        ? this.topicService.getOrCreateTopic(configuration.getKafkaTopic())
                        : this.topicService.getOrCreateEntityTopic(DomainContext.SKJEMA, configuration.getResourceReference())
        ).name();

        String fintEndpoint = StringUtils.isNotEmpty(configuration.getFintEndpoint())
                ? configuration.getFintEndpoint()
                : "/" + configuration.getResourceReference().replace(".", "/");

        String selfLinkKeyFilter = StringUtils.isNotEmpty(configuration.getSelfLinkKeyFilter())
                ? configuration.getSelfLinkKeyFilter()
                : "systemid";

        return new EntityPipeline(topic, fintEndpoint, selfLinkKeyFilter);
    }
}
