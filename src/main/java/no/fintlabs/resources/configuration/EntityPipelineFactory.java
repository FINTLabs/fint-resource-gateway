package no.fintlabs.resources.configuration;


import no.fintlabs.kafka.entity.topic.EntityTopicNameParameters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class EntityPipelineFactory {

    public EntityPipeline create(EntityPipelineConfiguration configuration) {
        EntityTopicNameParameters topicNameParameters =
                EntityTopicNameParameters.builder()
                        .resource(configuration.getResourceReference())
                        .build();

        String fintEndpoint = StringUtils.isNotEmpty(configuration.getFintEndpoint())
                ? configuration.getFintEndpoint()
                : "/" + configuration.getResourceReference().replace(".", "/");

        String selfLinkKeyFilter = StringUtils.isNotEmpty(configuration.getSelfLinkKeyFilter())
                ? configuration.getSelfLinkKeyFilter()
                : "systemid";

        return new EntityPipeline(topicNameParameters, fintEndpoint, selfLinkKeyFilter);
    }

}
