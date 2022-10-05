package no.fintlabs.resources.request.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import no.fintlabs.kafka.requestreply.topic.RequestTopicNameParameters;

@Getter
@AllArgsConstructor
public class RequestPipeline {

    private RequestTopicNameParameters topicNameParameters;
    private String fintEndpointFormat;
    private int expectedNumberOfPathParams;
}
