package no.fintlabs.kafka.configuration;


import no.fintlabs.kafka.entity.EntityTopicNameParameters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EntityPipelineFactory {

    @Value("${fint.org-id}")
    private String orgId;

    public EntityPipeline create(EntityPipelineConfiguration configuration) {
        EntityTopicNameParameters topicNameParameters =
                EntityTopicNameParameters.builder()
                        .orgId(orgId)
                        .domainContext("skjema")
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
