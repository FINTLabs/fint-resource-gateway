package no.fintlabs.resources.request.configuration;

import no.fintlabs.kafka.requestreply.topic.RequestTopicNameParameters;
import org.springframework.stereotype.Service;

@Service
public class RequestPipelineFactory {

    public RequestPipeline create(RequestPipelineConfiguration configuration) {
        RequestTopicNameParameters requestTopicNameParameters = RequestTopicNameParameters
                .builder()
                .resource(configuration.getResourceReference())
                .parameterName(String.join("-and-", configuration.getRequestParameterNames()))
                .build();

        return new RequestPipeline(
                requestTopicNameParameters,
                configuration.getFintEndpointFormat(),
                configuration.getRequestParameterNames().size()
        );
    }
}
