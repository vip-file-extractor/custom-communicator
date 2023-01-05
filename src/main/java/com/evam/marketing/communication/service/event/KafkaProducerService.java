package com.evam.marketing.communication.service.event;

import com.evam.marketing.communication.configuration.kafka.property.KafkaProperties;
import com.evam.marketing.communication.service.event.model.CommunicationResponseEvent;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka producer service
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Service
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, CommunicationResponseEvent> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final KafkaCommunicationResponseEventCallback eventCallback;

    public KafkaProducerService(KafkaTemplate<String, CommunicationResponseEvent> kafkaTemplate,
        KafkaProperties kafkaProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
        this.eventCallback = new KafkaCommunicationResponseEventCallback();
    }

    public void sendEvent(CommunicationResponseEvent event) {
        String eventName = kafkaProperties.getEventTopic().getName();
        ProducerRecord<String, CommunicationResponseEvent> producerRecord =
                new ProducerRecord<>(eventName, event.getActorId(), event);

        kafkaTemplate.send(producerRecord).addCallback(eventCallback);
    }

    public void sendEvents(List<CommunicationResponseEvent> events) {
        events.forEach(this::sendEvent);
    }
}
