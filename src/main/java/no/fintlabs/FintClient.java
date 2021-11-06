package no.fintlabs;

import lombok.Data;
import no.fint.model.resource.AbstractCollectionResources;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class FintClient {
    private final WebClient webClient;

    private final Map<String, Long> sinceTimestamp = new ConcurrentHashMap<>();

    public FintClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public <S, T extends AbstractCollectionResources<S>> Mono<List<S>> getResources(Class<T> clazz, String endpoint) {

        return get(clazz, endpoint)
                .flatMapIterable(T::getContent)
                .collect(Collectors.toList());
    }

    public <T> Mono<T> get(Class<T> clazz, String endpoint) {

        return webClient.get()
                .uri(endpoint.concat("/last-updated"))
                .retrieve()
                .bodyToMono(LastUpdated.class)
                .flatMap(lastUpdated -> webClient.get()
                        .uri(endpoint, uriBuilder -> uriBuilder.queryParam("sinceTimeStamp", sinceTimestamp.getOrDefault(endpoint, 0L)).build())
                        .retrieve()
                        .bodyToMono(clazz)
                        .doOnNext(it -> sinceTimestamp.put(endpoint, lastUpdated.getLastUpdated())));
    }

    @Data
    private static class LastUpdated {
        private Long lastUpdated;
    }

    public void reset() {
        sinceTimestamp.clear();
    }
}