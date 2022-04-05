package no.fintlabs.flyt.resources.configuration;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class PullConfiguration {
    private long initialDelayMs;
    private long fixedDelayMs;
}
