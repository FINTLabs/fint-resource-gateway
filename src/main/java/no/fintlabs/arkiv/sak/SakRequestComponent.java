package no.fintlabs.arkiv.sak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.arkiv.noark.SakResource;
import no.fintlabs.fint.FintClient;
import no.fintlabs.kafka.topic.DomainContext;
import no.fintlabs.kafka.topic.TopicService;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SakRequestComponent {

    private final FintClient fintClient;
    private final ObjectMapper objectMapper;

    public SakRequestComponent(FintClient fintClient, ObjectMapper objectMapper) {
        this.fintClient = fintClient;
        this.objectMapper = objectMapper;
    }

    @Bean
    @Qualifier("sakRequestByMappeIdTopic")
    TopicDescription sakRequestByMappeIdTopic(TopicService topicService) {
        return topicService.getOrCreateRequestTopic(
                DomainContext.SKJEMA,
                "arkiv.noark.sak",
                false,
                "mappeid"
        );
    }

    @KafkaListener(topics = "#{sakRequestByMappeIdTopic.name()}", containerFactory = "#{replyingKafkaListenerContainerFactory}")
    @SendTo
    public String listenMappeId(String mappeId) throws JsonProcessingException {
        SakResource result = fintClient
                .getResource("/arkiv/noark/sak/mappeid/" + mappeId, SakResource.class)
                .block();

        log.info("Returning: " + result);
        return objectMapper.writeValueAsString(result);
    }

}
