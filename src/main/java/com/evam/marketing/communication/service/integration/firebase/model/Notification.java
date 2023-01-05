package com.evam.marketing.communication.service.integration.firebase.model;

import lombok.Builder;
import lombok.Data;

/**
 * Notification
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Builder
@Data
public class Notification {
    public String title;
    public String body;
}
