package no.fintlabs.fint;

import org.springframework.kafka.core.KafkaTemplate;


public abstract class FintKafkaProducer<T> implements FintProducer<T> {

    protected final KafkaTemplate<String, EntityMessage<T>> kafkaTemplate;

    public FintKafkaProducer(KafkaTemplate<String, EntityMessage<T>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String key, T object) {
        kafkaTemplate
                .send(
                        getTopicName(),
                        key,
                        new EntityMessage<>(object)
                )
                .addCallback(new FintListendableFutureCallback<>());
    }
}
