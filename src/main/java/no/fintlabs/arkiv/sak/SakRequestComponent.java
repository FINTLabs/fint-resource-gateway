package no.fintlabs.arkiv.sak;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.arkiv.noark.SakResource;
import no.fintlabs.fint.FintClient;
import no.fintlabs.kafka.topic.DomainContext;
import no.fintlabs.kafka.topic.TopicNameService;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SakRequestComponent {

    private final FintClient fintClient;

    public SakRequestComponent(FintClient fintClient) {
        this.fintClient = fintClient;
    }

    @Bean
    String sakRequestByMappeIdTopicName(TopicNameService topicNameService) {
        return topicNameService.generateRequestTopicName(
                DomainContext.SKJEMA,
                "arkiv.noark.sak",
                false,
                "mappeid"
        );
    }

    @KafkaListener(topics = "#{sakRequestByMappeIdTopicName}", containerFactory = "#{replyingKafkaListenerContainerFactory}")
    @SendTo
    public SakResource listenMappeId(String mappeId) {
        SakResource result = fintClient
                .getResource("/arkiv/noark/sak/mappeid/" + mappeId, SakResource.class)
                .block();

        log.info("Returning: " + result);
        return result;
    }

}
