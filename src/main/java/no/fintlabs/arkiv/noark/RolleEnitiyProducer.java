package no.fintlabs.arkiv.noark;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.arkiv.kodeverk.Rolle;
import no.fint.model.resource.arkiv.kodeverk.RolleResource;
import no.fint.model.resource.arkiv.kodeverk.RolleResources;
import no.fintlabs.fint.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class RolleEnitiyProducer extends FintKafkaEntityProducer<RolleResource> {

    private final FintClient fintClient;

    public RolleEnitiyProducer(KafkaTemplate<String, EntityMessage<RolleResource>> kafkaTemplate, FintClient fintClient) {
        super(kafkaTemplate);
        this.fintClient = fintClient;
    }

    @Scheduled(fixedDelay = 30000L, initialDelay = 10000L)
    public void update() {
        log.info("Start updating Arkiv Roller ...");
        Objects.requireNonNull(fintClient.getResources(RolleResources.class, "/arkiv/kodeverk/rolle")
                        .block())
                .stream()
                .filter(o -> o.getSelfLinks().stream().anyMatch(link -> link.getHref().toLowerCase().contains("systemid")))
                .peek(r -> log.info("Sending entity {}", r))
                .forEach(o -> sendMessage(LinkUtilities.getSystemIdSelfLink(o), o));
        log.info("End updating Arkiv Roller");

    }

    @Override
    public String getTopicName() {
        return FintTopicUtilities.getTopicNameFromFintClassResource(Rolle.class);
    }
}
