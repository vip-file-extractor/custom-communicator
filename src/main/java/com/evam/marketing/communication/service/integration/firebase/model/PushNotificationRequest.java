package com.evam.marketing.communication.service.integration.firebase.model;

import lombok.Builder;
import lombok.Data;

/**
 * Push notification request
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Builder
@Data
public class PushNotificationRequest {
    private Message message;
}
