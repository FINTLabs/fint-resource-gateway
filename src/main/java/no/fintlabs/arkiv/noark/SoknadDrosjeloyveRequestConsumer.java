package no.fintlabs.arkiv.noark;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.fint.FintClient;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SoknadDrosjeloyveRequestConsumer {

    private final FintClient fintClient;

    public SoknadDrosjeloyveRequestConsumer(FintClient fintClient) {
        this.fintClient = fintClient;
    }

    @KafkaListener(topics = "request.arkiv.samferdsel.soknaddrosjeloyve.all")
    @SendTo
    public List<Object> listenAll() {
        return fintClient.getResourcesLastUpdated("/arkiv/samferdsel/soknaddrosjeloyve/").block();
    }

    @KafkaListener(topics = "request.arkiv.samferdsel.soknaddrosjeloyve.systemid")
    @SendTo
    public Object listenSystemId(String systemId) {
        Object result = fintClient.getResource("/arkiv/samferdsel/soknaddrosjeloyve/systemid/" + systemId)
                //.map(r -> ((HashMap<String, ?>) r)) // TODO: 15/11/2021 Cast in service?
                //.filter(r -> ((HashMap<String, String>) r.get("systemId")).get("identifikatorverdi").equals(systemId)) // TODO: 15/11/2021 Sentralize
                .block();

        log.info("Returning: " + result);
        return result;
    }

    // Default Consumer Factory
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<String, String>(consumerConfigs(), new StringDeserializer(), new StringDeserializer());
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    // Concurrent Listner container factory
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        // NOTE - set up of reply template
        factory.setReplyTemplate(kafkaTemplate());
        return factory;
    }

    // Standard KafkaTemplate
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private DefaultKafkaProducerFactory<String, Object> producerFactory() {

        ObjectMapper mapper = new ObjectMapper();
        JavaType requestValueType = mapper.getTypeFactory().constructType(Object.class);

        return new DefaultKafkaProducerFactory<String, Object>(producerConfigs(), new StringSerializer(), new JsonSerializer<>(requestValueType, mapper));
    }

    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:29092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

}
