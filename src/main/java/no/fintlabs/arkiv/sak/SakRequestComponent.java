package no.fintlabs.arkiv.sak;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.arkiv.noark.SakResource;
import no.fintlabs.fint.FintClient;
import no.fintlabs.kafka.TopicNameService;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SakRequestComponent {

    private final FintClient fintClient;

    public SakRequestComponent(
            FintClient fintClient) {
        this.fintClient = fintClient;

    }

    @Bean
    String sakRequestBySystemIdTopicName(TopicNameService topicNameService) {
        return topicNameService.generateRequestTopicName("arkiv.noark.sak","systemid");
    }

    @KafkaListener(topics = "#{sakRequestBySystemIdTopicName}")
    @SendTo
    public SakResource listenSystemId(String systemId) {
        SakResource result = fintClient
                .getResource("/arkiv/noark/sak/systemid/" + systemId, SakResource.class)
                .block();

        log.info("Returning: " + result);
        return result;
    }

    @Bean
    String sakRequestByMappeIdTopicName(TopicNameService topicNameService) {
        return topicNameService.generateRequestTopicName("arkiv.noark.sak","mappeid");
    }

    @KafkaListener(topics = "#{sakRequestByMappeIdTopicName}")
    @SendTo
    public SakResource listenMappeId(String mappeId) {
        SakResource result = fintClient
                .getResource("/arkiv/noark/sak/mappeid/" + mappeId, SakResource.class)
                .block();

        log.info("Returning: " + result);
        return result;
    }

}
