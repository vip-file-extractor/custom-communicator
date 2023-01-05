package com.evam.marketing.communication.service.event.model;

import lombok.Getter;

/**
 * Communication event name
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
public enum CommunicationEventName {
    SUCCESS("customCommunicationSuccess"), FAIL("customCommunicationFail");

    @Getter
    private final String eventName;

    CommunicationEventName(String eventName) {
        this.eventName = eventName;
    }
}
