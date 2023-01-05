package com.evam.marketing.communication.service.integration.model.response;

import com.evam.marketing.communication.service.event.model.CommunicationFailEvent;
import com.evam.marketing.communication.service.event.model.CommunicationResponseEvent;
import com.evam.marketing.communication.service.event.model.CommunicationSuccessEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Communication response
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommunicationResponse {
    private boolean success;
    private String providerResponseId;
    private String reason;
    private String message;
    @Setter
    private String provider;
    @Setter
    private String communicationUUID;
    @Setter
    private String communicationCode;
    @Setter
    private String scenario;
    @Setter
    private String actorId;

    public CommunicationResponseEvent toEvent() {
        return this.success ? toSuccessEvent() : toFailEvent();
    }

    private CommunicationResponseEvent toSuccessEvent() {
        return CommunicationSuccessEvent.builder()
            .communicationCode(communicationCode)
            .communicationUUID(communicationUUID)
            .scenario(scenario)
            .actorId(actorId)
            .message(message)
            .build();
    }

    private CommunicationResponseEvent toFailEvent() {
        return CommunicationFailEvent.builder()
            .communicationCode(communicationCode)
            .communicationUUID(communicationUUID)
            .scenario(scenario)
            .actorId(actorId)
            .reason(reason)
            .message(message)
            .build();
    }
}
