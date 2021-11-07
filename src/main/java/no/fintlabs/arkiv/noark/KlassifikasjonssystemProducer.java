package no.fintlabs.arkiv.noark;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.arkiv.noark.Klassifikasjonssystem;
import no.fint.model.resource.arkiv.kodeverk.KlassifikasjonstypeResource;
import no.fint.model.resource.arkiv.kodeverk.KlassifikasjonstypeResources;
import no.fintlabs.fint.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class KlassifikasjonssystemProducer extends FintKafkaEntityProducer<KlassifikasjonstypeResource> {
    private final FintClient fintClient;

    public KlassifikasjonssystemProducer(KafkaTemplate<String, EntityMessage<KlassifikasjonstypeResource>> kafkaTemplate, FintClient fintClient) {
        super(kafkaTemplate);
        this.fintClient = fintClient;
    }

    @Scheduled(fixedDelay = 30000L, initialDelay = 10000L)
    public void update() {
        log.info("Start updating Arkiv Klassifikasjonssystem ...");
        Objects.requireNonNull(fintClient.getResources(KlassifikasjonstypeResources.class, "/arkiv/noark/klassifikasjonssystem")
                        .block())
                .stream()
                .filter(o -> o.getSelfLinks().stream().anyMatch(link -> link.getHref().toLowerCase().contains("systemid")))
                .peek(r -> log.info("Sending entity {}", r))
                .forEach(o -> sendMessage(LinkUtilities.getSystemIdSelfLink(o), o));
        log.info("End updating Arkiv Klassifikasjonssystem");

    }

    @Override
    public String getTopicName() {
        return FintTopicUtilities.getTopicNameFromFintClassResource(Klassifikasjonssystem.class);
    }
}
