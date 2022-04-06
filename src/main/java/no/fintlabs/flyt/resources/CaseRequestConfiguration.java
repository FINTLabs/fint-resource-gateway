package no.fintlabs.flyt.resources;

import no.fint.model.resource.arkiv.noark.SakResource;
import no.fintlabs.flyt.FintClient;
import no.fintlabs.kafka.TopicCleanupPolicyParameters;
import no.fintlabs.kafka.requestreply.FintKafkaRequestConsumerFactory;
import no.fintlabs.kafka.requestreply.RequestTopicNameParameters;
import no.fintlabs.kafka.requestreply.RequestTopicService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
public class CaseRequestConfiguration {

    @Value("${fint.org-id}")
    private String orgId;

    @Bean
    public ConcurrentMessageListenerContainer<String, String> caseRequestByMappeIdConsumer(
            RequestTopicService requestTopicService,
            FintClient fintClient,
            FintKafkaRequestConsumerFactory fintKafkaRequestConsumerFactory
    ) {
        RequestTopicNameParameters topicNameParameters = RequestTopicNameParameters.builder()
                .orgId(orgId)
                .domainContext("skjema")
                .resource("arkiv.noark.sak")
                .parameterName("mappeid")
                .build();

        requestTopicService.ensureTopic(topicNameParameters, 0, TopicCleanupPolicyParameters.builder().build());

        return fintKafkaRequestConsumerFactory.createConsumer(
                topicNameParameters,
                String.class,
                SakResource.class,
                (consumerRecord) -> fintClient
                        .getResource("/arkiv/noark/sak/mappeid/" + consumerRecord.value(), SakResource.class)
                        .block(),
                new CommonLoggingErrorHandler()
        );
    }

}
