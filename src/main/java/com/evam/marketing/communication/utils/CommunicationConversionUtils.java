package com.evam.marketing.communication.utils;

import com.evam.marketing.communication.service.client.model.CustomCommunicationRequest;
import com.evam.marketing.communication.service.integration.model.request.CommunicationRequest;
import com.evam.marketing.communication.service.stream.model.request.CustomCommunicationStreamRequest;
import com.evam.marketing.communication.service.stream.model.request.StreamRequest;

/**
 * Communication conversion utils
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
public final class CommunicationConversionUtils {
    private CommunicationConversionUtils() {
    }

    public static CommunicationRequest streamRequestToCommunicationRequest(StreamRequest request, String content) {
        CustomCommunicationStreamRequest streamRequest = (CustomCommunicationStreamRequest) request;

        return CustomCommunicationRequest.builder()
            .messageType(streamRequest.getMessageType())
            .actorId(streamRequest.getActorId())
            .communicationCode(streamRequest.getCode())
            .communicationUUID(streamRequest.getUuid())
            .scenario(streamRequest.getScenario())
            .scenarioVersion(streamRequest.getScenarioVersion())
            .parameters(streamRequest.getParameters())
            .build();
    }
}
