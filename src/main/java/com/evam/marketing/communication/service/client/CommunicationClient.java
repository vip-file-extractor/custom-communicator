package com.evam.marketing.communication.service.client;

import com.evam.marketing.communication.service.integration.model.request.CommunicationRequest;
import javax.validation.constraints.NotNull;

/**
 * Communiation client
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
public interface CommunicationClient {
    @NotNull
    void send(CommunicationRequest communicationRequest);

    String getProvider();
}
