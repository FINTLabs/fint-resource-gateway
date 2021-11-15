package no.fintlabs.arkiv.noark;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.fint.FintClient;
import no.fintlabs.fint.FintKafkaEntityProducer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RolleEnitiyProducer extends FintKafkaEntityProducer {
    public RolleEnitiyProducer(KafkaTemplate<String, Object> kafkaTemplate, FintClient fintClient) {
        super(kafkaTemplate, fintClient);
    }

    @Override
    protected String getEndpointUrl() {
        return "/arkiv/kodeverk/rolle";
    }

    @Scheduled(fixedDelay = 30000L, initialDelay = 10000L)
    public void pollingSchedule() {
        this.pollResources();
    }
}
