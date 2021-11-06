package no.fintlabs.arkiv.noark;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.fint.model.arkiv.noark.AdministrativEnhet;
import no.fint.model.resource.arkiv.noark.AdministrativEnhetResource;
import no.fintlabs.NoKeyFoundException;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Repository;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Locale;

@Slf4j
@Repository
public class AdministrativEnhetRepository {

    private final KafkaTemplate<String, AdministrativEnhetResource> kafkaTemplate;
    private final static String TOPIC_NAME = AdministrativEnhet.class.getCanonicalName().replace("no.fint.model.", "").toLowerCase(Locale.ROOT);

    public AdministrativEnhetRepository(KafkaTemplate<String, AdministrativEnhetResource> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @SneakyThrows
    public void sendMessage(AdministrativEnhetResource object) {

        ListenableFuture<SendResult<String, AdministrativEnhetResource>> future =
                kafkaTemplate.send(
                        TOPIC_NAME,
                        object.getSelfLinks().stream().filter(link -> link.getHref().contains("systemid")).findAny().orElseThrow(NoKeyFoundException::new).getHref(),
                        object);

        future.addCallback(new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(SendResult<String, AdministrativEnhetResource> result) {
                log.trace("Sent message=[" + object +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.trace("Unable to send message=["
                        + object + "] due to : " + ex.getMessage());
            }
        });
    }


    @Bean
    public NewTopic topic() {
        return new NewTopic(TOPIC_NAME, 1, (short) 1);
    }
}
