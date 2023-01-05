package com.evam.marketing.communication.service.event.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Communication success event
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CommunicationSuccessEvent extends AbstractCommunicationResponseEvent {

    @Override
    public String getName() {
        return CommunicationEventName.SUCCESS.getEventName();
    }

    @Override
    public CustomCommunicationEventType getType() {
        return CustomCommunicationEventType.SUCCESS;
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}
