package no.fintlabs.fint;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Objects;

@Slf4j
public abstract class FintKafkaEntityProducer {

    protected final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topicName;
    private final FintClient fintClient;
    private final String endpointUrl;

    public FintKafkaEntityProducer(KafkaTemplate<String, Object> kafkaTemplate, FintClient fintClient) {
        this.endpointUrl = this.getEndpointUrl();
        // TODO: 12/11/2021 Valider endepunkt
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = FintTopicUtilities.getEntityTopicNameFromEndpointUrl(endpointUrl);
        new NewTopic(topicName, 1, (short) 1);
        this.fintClient = fintClient;
    }

    protected abstract String getEndpointUrl();

    public abstract void pollingSchedule();

    private void sendMessage(String key, Object object) {
        kafkaTemplate
                .send(
                        this.topicName,
                        key,
                        object
                )
                .addCallback(new FintListendableFutureCallback());
    }

    protected void pollResources() {
        log.info("Polling resources from " + endpointUrl);

        Objects.requireNonNull(fintClient.getResourcesLastUpdated(endpointUrl).block())
                .stream()
                .map(r -> ((HashMap<String, ?>) r))
                //.filter(o -> o.getSelfLinks().stream().anyMatch(link -> link.getHref().toLowerCase().contains("systemid")))
                .filter(r -> r.containsKey("systemId"))
                .peek(r -> log.info("Sending aresources as byte array: length=" + r))
                .forEach(r -> this.sendMessage(((HashMap<String, String>) r.get("systemId")).get("identifikatorverdi"), r));
        log.info("Completed polling resources from " + endpointUrl);
    }

}
