package com.evam.marketing.communication.service.stream.model.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Map;

/**
 * Stream request
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = CustomCommunicationStreamRequest.class)
public interface StreamRequest {
    String getName();

    String getCode();

    String getUuid();

    String getScenario();

    int getScenarioVersion();

    String getActorId();

    String getType();

    String getMessageType();

    Map<String, Object> getResourceVariables();

    boolean hasResource();
}
