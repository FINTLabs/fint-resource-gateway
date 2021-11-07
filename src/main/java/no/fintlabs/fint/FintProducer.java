package no.fintlabs.fint;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;

public interface FintProducer<T> {

    @Bean
    default NewTopic topic() {
        return new NewTopic(getTopicName(), 1, (short) 1);
    };

    String getTopicName();

    void sendMessage(String key, T object);


}
