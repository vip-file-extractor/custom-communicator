package com.evam.marketing.communication.service.stream;

import com.evam.marketing.communication.service.integration.CommunicationService;
import com.evam.marketing.communication.service.stream.model.request.StreamRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Integration kafka consumer
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Component
@Slf4j
public class IntegrationKafkaConsumer {
    public static final String LISTENER_ID = "INTEGRATION_LISTENER";

    private final CommunicationService communicationService;

    public IntegrationKafkaConsumer(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @KafkaListener(id = LISTENER_ID,
        topics = {"${kafka.integration-topic.name}"},
        groupId = "${kafka.integration-topic.group}",
        containerFactory = "integrationKafkaFactory"
    )
    public void integrationListener(List<StreamRequest> requestList, Acknowledgment ack) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Received communication request records [{}]. {}", requestList.size(), requestList);
            } else {
                log.info("Received communication request records [{}]", requestList.size());
            }

            communicationService.execute(requestList);
        } finally {
            ack.acknowledge();
        }
    }
}
