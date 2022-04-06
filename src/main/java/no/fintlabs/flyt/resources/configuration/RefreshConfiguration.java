package no.fintlabs.flyt.resources.configuration;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class RefreshConfiguration {
    private long intervalMs;
    private long topicRetentionTimeOffsetMs;

    public long getTopicRetentionTimeMs() {
        return intervalMs + topicRetentionTimeOffsetMs;
    }
}
