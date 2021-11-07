package no.fintlabs.arkiv.noark;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.arkiv.noark.AdministrativEnhet;
import no.fint.model.resource.arkiv.noark.AdministrativEnhetResource;
import no.fint.model.resource.arkiv.noark.AdministrativEnhetResources;
import no.fintlabs.fint.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class AdministrativEnhetEntityProducer extends FintKafkaEntityProducer<AdministrativEnhetResource> {
    private final FintClient fintClient;

    public AdministrativEnhetEntityProducer(FintClient fintClient, KafkaTemplate<String, EntityMessage<AdministrativEnhetResource>> kafkaTemplate) {
        super(kafkaTemplate);
        this.fintClient = fintClient;
    }

    @Scheduled(fixedDelay = 30000L, initialDelay = 10000L)
    public void update() {
        log.info("Start updating Administrative Enheter...");
        Objects.requireNonNull(fintClient.getResources(
                                AdministrativEnhetResources.class,
                                "/arkiv/noark/administrativenhet"
                        )
                        .block())
                .stream()
                .filter(o -> o.getSelfLinks().stream().anyMatch(link -> link.getHref().toLowerCase().contains("systemid")))
                .peek(r -> log.info("Sending entity {}", r))
                .forEach(o -> sendMessage(LinkUtilities.getSystemIdSelfLink(o), o));
        log.info("End updating Administrative Enheter");


    }

    @Override
    public String getTopicName() {
        return FintTopicUtilities.getTopicNameFromFintClassResource(AdministrativEnhet.class);
    }
}
