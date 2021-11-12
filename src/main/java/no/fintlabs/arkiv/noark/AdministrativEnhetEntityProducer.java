package no.fintlabs.arkiv.noark;

import no.fintlabs.fint.FintClient;
import no.fintlabs.fint.FintKafkaEntityProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AdministrativEnhetEntityProducer extends FintKafkaEntityProducer {

    public AdministrativEnhetEntityProducer(KafkaTemplate<String, byte[]> kafkaTemplate, FintClient fintClient) {
        super(kafkaTemplate, fintClient);
    }

    @Override
    protected String getEndpointUrl() {
        return "/arkiv/noark/administrativenhet";
    }

    @Scheduled(fixedDelay = 30000L, initialDelay = 10000L)
    public void pollingSchedule() {
        this.pollResources();
    }
}
