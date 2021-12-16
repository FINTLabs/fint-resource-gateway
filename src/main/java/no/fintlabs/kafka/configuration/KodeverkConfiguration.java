package no.fintlabs.kafka.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("fint.kodeverk")
public class KodeverkConfiguration {

    @Getter @Setter private Resources resources;


    public static class Resources {

        @Getter @Setter private List<EntityPipelineConfiguration> entityPipelines;

    }
}
