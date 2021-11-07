package no.fintlabs.fint;

public interface FintProducer<T> {

    String getTopicName();

    void sendMessage(String key, T object);

}
