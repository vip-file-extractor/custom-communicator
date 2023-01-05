package com.evam.marketing.communication.service.integration.model.request;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Abstract communication request
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class AbstractCommunicationRequest implements CommunicationRequest {
    private static final String TRANSACTIONAL = "Transactional";

    private String messageType;
    private String actorId;
    private String communicationCode;
    private String communicationUUID;
    private String scenario;
    private int scenarioVersion;

    public boolean isTransactional() {
        return TRANSACTIONAL.equalsIgnoreCase(this.messageType);
    }
}
