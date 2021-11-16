package no.fintlabs.arkiv.noark;

import no.fint.model.resource.arkiv.noark.AdministrativEnhetResource;
import no.fint.model.resource.arkiv.samferdsel.SoknadDrosjeloyveResource;
import no.fintlabs.fint.FintClient;
import no.fintlabs.fint.FintTopicUtilities;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class SoknadDrosjeloyveRequestConsumer {

    private final FintClient fintClient;

    public SoknadDrosjeloyveRequestConsumer(FintClient fintClient) {
        this.fintClient = fintClient;
    }

    @KafkaListener(topics = "request.arkiv.samferdsel.soknaddrosjeloyve.all")
    @SendTo
    public List<Object> listenAll() {
        return fintClient.getResources("/arkiv/samferdsel/soknaddrosjeloyve/").block();
    }

    @KafkaListener(topics = "request.arkiv.samferdsel.soknaddrosjeloyve.systemid")
    @SendTo
    public Object listenSystemId(String systemId) {
        return fintClient.getResources("/arkiv/samferdsel/soknaddrosjeloyve/")
                .map(r -> ((HashMap<String, ?>) r)) // TODO: 15/11/2021 Cast in service?
                .filter(r -> ((HashMap<String, String>) r.get("systemId")).get("identifikatorverdi").equals(systemId)) // TODO: 15/11/2021 Sentralize
                .block();
    }

}
