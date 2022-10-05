package no.fintlabs.resources.request.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties("fint.resource-gateway.resources.request")
public class RequestResourcesConfiguration {
    private List<RequestPipelineConfiguration> requestPipelines;
}
