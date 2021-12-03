package no.fintlabs.kafka.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.admin.NewTopic;

@AllArgsConstructor
public class EntityPipeline {

    @Getter
    private NewTopic kafkaTopic;
    @Getter
    private String fintEndpoint;
    @Getter
    private String selfLinkKeyFilter;

}
