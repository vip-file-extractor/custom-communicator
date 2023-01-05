package com.evam.marketing.communication.service.integration.model.request;

import java.io.Serializable;

/**
 * Communication request
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
public interface CommunicationRequest extends Serializable {
    String getMessageType();

    String getActorId();

    String getCommunicationCode();

    String getCommunicationUUID();

    String getScenario();

    boolean isTransactional();
}
