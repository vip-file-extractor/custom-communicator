package com.evam.marketing.communication.service.client;

import com.evam.marketing.communication.service.event.model.CommunicationResponseEvent;
import com.evam.marketing.communication.service.event.KafkaProducerService;
import com.evam.marketing.communication.service.integration.model.request.CommunicationRequest;
import com.evam.marketing.communication.service.integration.model.response.CommunicationResponse;

/**
 * Abstract communication client
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
public abstract class AbstractCommunicationClient implements CommunicationClient {
    private final KafkaProducerService kafkaProducerService;

    protected AbstractCommunicationClient(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    protected CommunicationResponse generateSuccessCommunicationResponse(
        CommunicationRequest communicationRequest,
        String providerResponseId,
        String message
    ) {
        return CommunicationResponse.builder()
            .success(true)
            .communicationCode(communicationRequest.getCommunicationCode())
            .communicationUUID(communicationRequest.getCommunicationUUID())
            .actorId(communicationRequest.getActorId())
            .scenario(communicationRequest.getScenario())
            .provider(getProvider())
            .providerResponseId(providerResponseId)
            .message(message)
            .build();
    }

    protected CommunicationResponse generateFailCommunicationResponse(
        CommunicationRequest communicationRequest,
        String message,
        String reason
    ) {
        return CommunicationResponse.builder()
            .success(false)
            .communicationCode(communicationRequest.getCommunicationCode())
            .communicationUUID(communicationRequest.getCommunicationUUID())
            .actorId(communicationRequest.getActorId())
            .scenario(communicationRequest.getScenario())
            .provider(getProvider())
            .message(message)
            .reason(reason)
            .build();
    }

    protected void sendEvent(CommunicationResponseEvent event) {
        kafkaProducerService.sendEvent(event);
    }
}
