package no.fintlabs.flyt.resources.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("fint.flyt.resource-gateway.resources")
public class ResourcesConfiguration {
    private RefreshConfiguration refresh;
    private PullConfiguration pull;
    private List<EntityPipelineConfiguration> entityPipelines;
}
