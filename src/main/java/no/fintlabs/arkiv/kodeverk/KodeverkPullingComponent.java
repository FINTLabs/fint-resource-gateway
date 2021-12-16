package no.fintlabs.arkiv.kodeverk;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.fint.FintClient;
import no.fintlabs.kafka.configuration.EntityPipeline;
import no.fintlabs.kafka.configuration.EntityPipelineConfiguration;
import no.fintlabs.kafka.configuration.EntityPipelineFactory;
import no.fintlabs.kafka.configuration.KodeverkConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO: 03/12/2021 Rename to FintResourcePublishingComponent
@Slf4j
@Component
public class KodeverkPullingComponent {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FintClient fintClient;
    private final List<EntityPipeline> entityPipelines;

    public KodeverkPullingComponent(
            KodeverkConfiguration kodeverkConfiguration,
            EntityPipelineFactory entityPipelineFactory,
            KafkaTemplate<String, Object> kafkaTemplate,
            FintClient fintClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.fintClient = fintClient;
        this.entityPipelines = this.createEntityPipelines(
                entityPipelineFactory,
                kodeverkConfiguration.getResources().getEntityPipelines()
        );
    }

    private List<EntityPipeline> createEntityPipelines(
            EntityPipelineFactory entityPipelineFactory,
            List<EntityPipelineConfiguration> configs) {
        return configs.stream()
                .map(entityPipelineFactory::create)
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "${fint.kafka.resourceRefreshCron}")
    private void resetLastUpdatedTimestamps() {
        log.warn("Resetting last updated timestamps");
        this.fintClient.resetLastUpdatedTimestamps();
    }

    @Scheduled(
            initialDelayString = "${fint.kodeverk.resources.polling.initialDelay}",
            fixedDelayString = "${fint.kodeverk.resources.polling.fixedDelay}")
    private void pullAllUpdatedEntityResources() {
        log.info("Starting polling kodeverk resources");
        entityPipelines.forEach(this::pullUpdatedEntityResources);
        log.info("Completed polling kodeverk resources");
    }

    private void pullUpdatedEntityResources(EntityPipeline entityPipeline) {
        List<HashMap<String, Object>> resources = getUpdatedResources(entityPipeline.getFintEndpoint());
        for (HashMap<String, Object> resource : resources) {
            String key = getKey(resource, entityPipeline.getSelfLinkKeyFilter());
            kafkaTemplate.send(entityPipeline.getKafkaTopic().name(), key, resource);
        }
        log.info(resources.stream().count() + " entities sent to " + entityPipeline.getKafkaTopic().name());
    }

    private List<HashMap<String, Object>> getUpdatedResources(String endpointUrl) {
        try {
            return Objects.requireNonNull(fintClient.getResourcesLastUpdated(endpointUrl).block())
                    .stream()
                    .map(r -> ((HashMap<String, Object>) r))
                    .collect(Collectors.toList());
        } catch (WebClientException e) {
            log.error("Could not pull entities from endpoint=" + endpointUrl, e);
            return Collections.emptyList();
        }
    }

    // TODO: 19/11/2021 Handle exceptions (casting and no systemid)
    private String getKey(HashMap<String, Object> resource, String selfLinkKeyFilter) {
        HashMap<String, Object> links = (HashMap<String, Object>) resource.get("_links");
        List<HashMap<String, String>> selfLinks = (List<HashMap<String, String>>) links.get("self");
        return selfLinks.stream()
                .filter(o -> o.containsKey("href"))
                .map(o -> o.get("href"))
                .filter(o -> o.toLowerCase().contains(selfLinkKeyFilter))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("No %s to generate key for resource=%s", selfLinkKeyFilter, resource)));
    }

}
