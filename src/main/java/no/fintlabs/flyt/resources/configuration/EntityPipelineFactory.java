package no.fintlabs.flyt.resources.configuration;


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

        return new EntityPipeline(
                topicNameParameters,
                fintEndpoint,
                selfLinkKeyFilter,
                configuration.getSubEntityPipelineConfiguration() != null
                        ? createSubEntityPipeline(configuration.getResourceReference(), configuration.getSubEntityPipelineConfiguration())
                        : null
        );
    }

    private SubEntityPipeline createSubEntityPipeline(String resourceReference, SubEntityPipelineConfiguration subEntityPipelineConfiguration) {
        return new SubEntityPipeline(
                EntityTopicNameParameters.builder()
                        .resource(resourceReference + "-" + subEntityPipelineConfiguration.getReference())
                        .build(),
                subEntityPipelineConfiguration.getReference(),
                subEntityPipelineConfiguration.getKeySuffixFilter()
        );
    }

}
