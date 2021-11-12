package no.fintlabs.fint;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Objects;

@Slf4j
public abstract class FintKafkaEntityProducer {

    protected final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final String topicName;
    private final FintClient fintClient;
    private final String endpointUrl;

    public FintKafkaEntityProducer(KafkaTemplate<String, byte[]> kafkaTemplate, FintClient fintClient) {
        this.endpointUrl = this.getEndpointUrl();
        // TODO: 12/11/2021 Valider endepunkt
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = FintTopicUtilities.getTopicNameFromEndpointUrl(endpointUrl);
        new NewTopic(topicName, 1, (short) 1);
        this.fintClient = fintClient;
    }

    protected abstract String getEndpointUrl();

    public abstract void pollingSchedule();

    private void sendMessage(byte[] object) {
        kafkaTemplate
                .send(
                        this.topicName,
                        object
                )
                .addCallback(new FintListendableFutureCallback());
    }

    protected void pollResources() {
        log.info("Polling resources from " + endpointUrl);
        Objects.requireNonNull(fintClient.getResources(endpointUrl).block())
                .stream()
                //.filter(o -> o.getSelfLinks().stream().anyMatch(link -> link.getHref().toLowerCase().contains("systemid")))
                .peek(r -> log.info("Sending resources as byte array: length=" + r.length))
                .forEach(this::sendMessage);
        log.info("Completed polling resources from " + endpointUrl);
    }

}
