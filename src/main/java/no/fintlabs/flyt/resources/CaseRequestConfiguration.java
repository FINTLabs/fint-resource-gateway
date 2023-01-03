package no.fintlabs.flyt.resources;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.arkiv.noark.SakResource;
import no.fintlabs.flyt.FintClient;
import no.fintlabs.kafka.common.topic.TopicCleanupPolicyParameters;
import no.fintlabs.kafka.requestreply.ReplyProducerRecord;
import no.fintlabs.kafka.requestreply.RequestConsumerFactoryService;
import no.fintlabs.kafka.requestreply.topic.RequestTopicNameParameters;
import no.fintlabs.kafka.requestreply.topic.RequestTopicService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
@Slf4j
public class CaseRequestConfiguration {

    @Bean
    public ConcurrentMessageListenerContainer<String, String> caseRequestByMappeIdConsumer(
            RequestTopicService requestTopicService,
            FintClient fintClient,
            RequestConsumerFactoryService requestConsumerFactoryService
    ) {
        RequestTopicNameParameters topicNameParameters = RequestTopicNameParameters.builder()
                .resource("arkiv.noark.sak")
                .parameterName("mappeid")
                .build();

        requestTopicService.ensureTopic(topicNameParameters, 0, TopicCleanupPolicyParameters.builder().build());

        return requestConsumerFactoryService.createFactory(
                String.class,
                SakResource.class,
                (consumerRecord) -> {
                    try {
                        return ReplyProducerRecord.<SakResource>builder()
                                .value(fintClient
                                        .getResource("/arkiv/noark/sak/mappeid/" + consumerRecord.value(), SakResource.class)
                                        .block()
                                ).build();
                    } catch (RuntimeException e) {
                        log.error("Could not find case with id={}", consumerRecord.value(), e);
                        return ReplyProducerRecord.<SakResource>builder()
                                .value(null)
                                .build();
                    }
                },
                new CommonLoggingErrorHandler()
        ).createContainer(topicNameParameters);
    }

}
