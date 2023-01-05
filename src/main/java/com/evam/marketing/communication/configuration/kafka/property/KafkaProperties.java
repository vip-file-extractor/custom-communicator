package com.evam.marketing.communication.configuration.kafka.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Kafka properties
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("kafka")
@Data
public class KafkaProperties {
    private String bootstrapAddress;
    private boolean communicationUUIDCheck;
    private KafkaTopicProperty eventTopic;
    private KafkaTopicProperty integrationTopic;
    private KafkaProducerProperty producer;
}
