package com.evam.marketing.communication.service.event.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Communication fail event
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@SuperBuilder
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CommunicationFailEvent extends AbstractCommunicationResponseEvent {
    private String reason;

    @Override
    public String getName() {
        return CommunicationEventName.FAIL.getEventName();
    }

    @Override
    public CustomCommunicationEventType getType() {
        return CustomCommunicationEventType.FAIL;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
