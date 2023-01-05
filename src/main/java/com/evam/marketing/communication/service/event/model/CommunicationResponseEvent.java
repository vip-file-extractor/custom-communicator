package com.evam.marketing.communication.service.event.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Communication response event
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
public interface CommunicationResponseEvent extends Serializable {
    String getName();

    String getScenario();

    String getActorId();

    String getCommunicationCode();

    String getCommunicationUUID();

    CustomCommunicationEventType getType();

    boolean isSuccess();

    void addCustomParameter(String parameterKey, String parameterValue);

    void addCustomParameter(String parameterKey, BigDecimal parameterValue);

    Map<String, Object> getCustomParameters();
}
