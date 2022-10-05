package no.fintlabs.resources.entity.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("fint.resource-gateway.resources.entity")
public class EntityResourcesConfiguration {
    private RefreshConfiguration refresh;
    private PullConfiguration pull;
    private List<EntityPipelineConfiguration> entityPipelines;
}
