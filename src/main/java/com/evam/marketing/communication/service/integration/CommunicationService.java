package com.evam.marketing.communication.service.integration;

import com.evam.marketing.communication.service.stream.model.request.StreamRequest;
import java.util.List;

/**
 * Communication service
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
public interface CommunicationService {
    void execute(List<StreamRequest> requestList);
}
