package no.fintlabs.arkiv.kodeverk;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.fint.FintClient;
import no.fintlabs.kafka.producers.FintKafkaEntityMultiProducer;
import no.fintlabs.kafka.KodeverkConfiguration;
import no.fintlabs.kafka.EntityPipelineConfiguration;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KodeverkPollingComponent {

    private final KodeverkConfiguration kodeverkConfiguration;
    private final FintKafkaEntityMultiProducer fintKafkaEntityMultiProducer;
    private final FintClient fintClient;

    public KodeverkPollingComponent(KodeverkConfiguration kafkaAdminConfiguration, FintKafkaEntityMultiProducer fintKafkaEntityMultiProducer, FintClient fintClient) {
        this.kodeverkConfiguration = kafkaAdminConfiguration;
        this.fintKafkaEntityMultiProducer = fintKafkaEntityMultiProducer;
        this.fintClient = fintClient;
        this.initializeTopics();
    }

    private void initializeTopics() {
        log.info("Starting initializing topics");
        kodeverkConfiguration.getResources().getEntityPipelines().forEach(this::initializeTopic);
        log.info("Completed initializing topics");
    }

    private void initializeTopic(EntityPipelineConfiguration config) {
        NewTopic topic = new NewTopic(
                config.getKafkaTopic(),
                config.getTopicPartitions() > 0
                        ? config.getTopicPartitions()
                        : kodeverkConfiguration.getResources().getDefaultTopicPartitions(),
                (short) (config.getTopicReplications() > 0
                        ? config.getTopicReplications()
                        : kodeverkConfiguration.getResources().getDefaultTopicReplications())
        );
        log.info("Initialized topic: " + topic);
    }

    @Scheduled(
            initialDelayString = "${fint.kodeverk.resources.polling.initialDelay}",
            fixedDelayString = "${fint.kodeverk.resources.polling.fixedDelay}")
    private void pollingSchedule() {
        log.info("Starting polling kodeverk resources");
        kodeverkConfiguration.getResources().getEntityPipelines().forEach(this::pollEntityResources);
        log.info("Completed polling kodeverk resources");
    }

    private void pollEntityResources(EntityPipelineConfiguration config) {
        List<HashMap<String, Object>> resources = getResources(config.getFintEndpoint());
        for (HashMap<String, Object> resource : resources) {
            String key = getKey(resource);
            fintKafkaEntityMultiProducer.sendMessage(config.getKafkaTopic(), key, resource);
        }
    }

    private List<HashMap<String, Object>> getResources(String endpointUrl) {
        return Objects.requireNonNull(fintClient.getResourcesLastUpdated(endpointUrl).block())
                .stream()
                .map(r -> ((HashMap<String, Object>) r))
                .collect(Collectors.toList());
    }

    // TODO: 19/11/2021 Handle exceptions (casting and no systemid)
    private String getKey(HashMap<String, Object> resource) {
        HashMap<String, Object> links = (HashMap<String, Object>) resource.get("_links");
        List<HashMap<String, String>> selfLinks = (List<HashMap<String, String>>) links.get("self");
        return selfLinks.stream()
                .filter(o -> o.containsKey("href"))
                .map(o -> o.get("href"))
                .filter(o -> o.toLowerCase().contains("systemid"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No systemid to generate key"));
    }

}
