package no.fintlabs;

import lombok.extern.slf4j.Slf4j;
import no.fint.model.resource.arkiv.noark.AdministrativEnhetResources;
import no.fintlabs.arkiv.noark.AdministrativEnhetRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Slf4j
@Repository
public class FintRepository {
    private final FintClient fintClient;
    private final AdministrativEnhetRepository administrativEnhetRepository;

    public FintRepository(FintClient fintClient, AdministrativEnhetRepository administrativEnhetRepository) {
        this.fintClient = fintClient;
        this.administrativEnhetRepository = administrativEnhetRepository;
    }

    @PostConstruct
    public void init() {
        Objects.requireNonNull(fintClient.getResources(
                                AdministrativEnhetResources.class,
                                "/arkiv/noark/administrativenhet"
                        )
                        .block())
                        .forEach(administrativEnhetRepository::sendMessage);




    }
}
