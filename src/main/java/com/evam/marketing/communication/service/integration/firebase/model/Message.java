package com.evam.marketing.communication.service.integration.firebase.model;

import lombok.Builder;
import lombok.Data;

/**
 * Message
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Builder
@Data
public class Message {
    public Notification notification;
    public PushNotificationCustomData data;
    public String token;
}
