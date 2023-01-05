package com.evam.marketing.communication.configuration.kafka;

import com.evam.marketing.communication.configuration.kafka.property.KafkaProperties;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Kafka topic configuration
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class KafkaTopicConfig {
    private final KafkaProperties kafkaProperties;

    public KafkaTopicConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapAddress());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic eventTopic() {
        return TopicBuilder.name(kafkaProperties.getEventTopic().getName())
            .partitions(kafkaProperties.getEventTopic().getPartition())
            .replicas(kafkaProperties.getEventTopic().getReplication())
            .config(TopicConfig.RETENTION_MS_CONFIG, kafkaProperties.getEventTopic().getRetentionMs())
            .build();
    }

    @Bean
    public NewTopic integrationTopic() {
        return TopicBuilder.name(kafkaProperties.getIntegrationTopic().getName())
            .partitions(kafkaProperties.getIntegrationTopic().getPartition())
            .replicas(kafkaProperties.getIntegrationTopic().getReplication())
            .config(TopicConfig.RETENTION_MS_CONFIG, kafkaProperties.getIntegrationTopic().getRetentionMs())
            .build();
    }
}
