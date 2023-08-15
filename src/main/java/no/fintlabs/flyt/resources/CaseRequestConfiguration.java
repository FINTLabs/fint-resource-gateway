package no.fintlabs.flyt.resources;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.arkiv.noark.JournalpostResource;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

    @Bean
    public ConcurrentMessageListenerContainer<String, String> caseRequestByArchiveInstanceIdConsumer(
            RequestTopicService requestTopicService,
            FintClient fintClient,
            RequestConsumerFactoryService requestConsumerFactoryService
    ) {
        RequestTopicNameParameters topicNameParameters = RequestTopicNameParameters.builder()
                .resource("arkiv.noark.sak-with-filtered-journalposts")
                .parameterName("archive-instance-id")
                .build();

        requestTopicService.ensureTopic(topicNameParameters, 0, TopicCleanupPolicyParameters.builder().build());

        return requestConsumerFactoryService.createFactory(
                String.class,
                SakResource.class,
                (consumerRecord) -> {
                    CaseAndJournalpostIds caseAndJournalpostIds = extractCaseAndJournalpostIds(consumerRecord.value());
                    try {
                        SakResource sakResource = fintClient
                                .getResource("/arkiv/noark/sak/mappeid/" + caseAndJournalpostIds.getCaseId(), SakResource.class)
                                .block();
                        if (sakResource != null) {
                            List<JournalpostResource> filteredJournalposts = sakResource.getJournalpost()
                                    .stream()
                                    .filter(journalpostResource -> caseAndJournalpostIds.getJournalpostIds().contains(journalpostResource.getJournalPostnummer()))
                                    .toList();
                            sakResource.setJournalpost(filteredJournalposts);
                        }
                        return ReplyProducerRecord.<SakResource>builder()
                                .value(sakResource)
                                .build();
                    } catch (RuntimeException e) {
                        log.error("Could not find case with archive instance id={}", consumerRecord.value(), e);
                        return ReplyProducerRecord.<SakResource>builder()
                                .value(null)
                                .build();
                    }
                },
                new CommonLoggingErrorHandler()
        ).createContainer(topicNameParameters);
    }

    @Getter
    @Builder
    private static class CaseAndJournalpostIds {
        private final String caseId;
        private final Collection<Long> journalpostIds;
    }

    private CaseAndJournalpostIds extractCaseAndJournalpostIds(String archiveInstanceId) {
        String[] splitArchiveInstanceId = archiveInstanceId.split("-");
        String caseId = splitArchiveInstanceId[0];
        List<Long> journalpostIds = splitArchiveInstanceId.length == 1
                ? List.of()
                : Arrays.stream(
                        splitArchiveInstanceId[1]
                                .replace("[", "")
                                .replace("]", "")
                                .split(",")
                )
                .map(Long::parseLong)
                .toList();
        return CaseAndJournalpostIds
                .builder()
                .caseId(caseId)
                .journalpostIds(journalpostIds)
                .build();
    }

}
