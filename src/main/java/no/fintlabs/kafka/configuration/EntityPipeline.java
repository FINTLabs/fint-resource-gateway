package no.fintlabs.kafka.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.admin.NewTopic;

@AllArgsConstructor
public class EntityPipeline {

    @Getter
    NewTopic kafkaTopic;
    @Getter
    String fintEndpoint;

}
