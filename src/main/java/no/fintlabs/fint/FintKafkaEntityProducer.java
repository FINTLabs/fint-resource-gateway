package no.fintlabs.fint;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;


public abstract class FintKafkaEntityProducer<T> implements FintProducer<T> {

    protected final KafkaTemplate<String, EntityMessage<T>> kafkaTemplate;
    private final NewTopic topic;


    public FintKafkaEntityProducer(KafkaTemplate<String, EntityMessage<T>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = new NewTopic(getTopicName(), 1, (short) 1);

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
