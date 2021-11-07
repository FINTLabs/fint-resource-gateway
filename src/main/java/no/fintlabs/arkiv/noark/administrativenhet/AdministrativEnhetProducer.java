package no.fintlabs.arkiv.noark.administrativenhet;

import no.fint.model.arkiv.noark.AdministrativEnhet;
import no.fint.model.resource.arkiv.noark.AdministrativEnhetResource;
import no.fint.model.resource.arkiv.noark.AdministrativEnhetResources;
import no.fintlabs.fint.EntityMessage;
import no.fintlabs.fint.LinkUtilities;
import no.fintlabs.fint.FintClient;
import no.fintlabs.fint.FintKafkaProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Component
public class AdministrativEnhetProducer extends FintKafkaProducer<AdministrativEnhetResource> {
    private final FintClient fintClient;

    public AdministrativEnhetProducer(FintClient fintClient, KafkaTemplate<String, EntityMessage<AdministrativEnhetResource>> kafkaTemplate) {
        super(kafkaTemplate);
        this.fintClient = fintClient;
    }

    @PostConstruct
    public void init() {
        Objects.requireNonNull(fintClient.getResources(
                                AdministrativEnhetResources.class,
                                "/arkiv/noark/administrativenhet"
                        )
                        .block())
                .stream()
                .filter(o -> o.getSelfLinks().stream().anyMatch(link -> link.getHref().toLowerCase().contains("systemid")))
                .forEach(o -> sendMessage(LinkUtilities.getSystemIdSelfLink(o), o));


    }

    @Override
    public String getTopicName() {
        return AdministrativEnhet.class.getCanonicalName().replace("no.fint.model.", "").toLowerCase();
    }
}
