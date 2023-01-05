package com.evam.marketing.communication.configuration.kafka;

import com.evam.marketing.communication.service.event.model.CommunicationResponseEvent;
import com.evam.marketing.communication.configuration.kafka.property.KafkaProperties;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Kafka producer configuration
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Configuration
public class KafkaProducerConfig {
    private final KafkaProperties kafkaProperties;

    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public KafkaTemplate<String, CommunicationResponseEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    private ProducerFactory<String, CommunicationResponseEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(getCommonConfigProperties());
    }

    private Map<String, Object> getCommonConfigProperties() {
        Map<String, Object> configProps = new HashMap<>();
        kafkaProperties.getProducer()
            .getCustomConfigProperties()
            .forEach(kafkaConfigProperty -> configProps.put(kafkaConfigProperty.getKey(),
                kafkaConfigProperty.getValue()));

        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            kafkaProperties.getBootstrapAddress());
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG,
            kafkaProperties.getProducer().getBufferMemory());
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG,
            kafkaProperties.getProducer().getBatchSize());
        configProps.put(ProducerConfig.LINGER_MS_CONFIG,
            kafkaProperties.getProducer().getLingerMs());
        configProps.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducer().getAcks());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return configProps;
    }
}
