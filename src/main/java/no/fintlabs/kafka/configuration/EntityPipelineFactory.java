package no.fintlabs.kafka.configuration;

import no.fintlabs.kafka.TopicService;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Service;

@Service
public class EntityPipelineFactory {

    private final TopicService topicService;

    public EntityPipelineFactory(TopicService topicService) {
        this.topicService = topicService;
    }

    public EntityPipeline create(EntityPipelineConfiguration configuration) {

        NewTopic topic = StringUtils.isNotEmpty(configuration.getKafkaTopic())
                ? this.topicService.createNewTopic(configuration.getKafkaTopic())
                : this.topicService.createEntityTopic(configuration.getResourceReference());

        String fintEndpoint = StringUtils.isNotEmpty(configuration.getFintEndpoint())
                ? configuration.getFintEndpoint()
                : "/" + configuration.getResourceReference().replace(".", "/");

        String selfLinkKeyFilter = StringUtils.isNotEmpty(configuration.getSelfLinkKeyFilter())
                ? configuration.getSelfLinkKeyFilter()
                : "systemid";

        return new EntityPipeline(topic, fintEndpoint, selfLinkKeyFilter);
    }
}
