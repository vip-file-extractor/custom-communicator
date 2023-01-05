package com.evam.marketing.communication.service.stream.model.request;

import java.util.Collections;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Abstract stream request
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractStreamRequest implements StreamRequest {
    private String name;
    @NotNull
    private String code;
    @NotNull
    private String uuid;
    @NotNull
    private String scenario;
    @NotNull
    private int scenarioVersion;
    @NotNull
    private String actorId;
    private String type;
    private String messageType;

    @Override
    public Map<String, Object> getResourceVariables() {
        return Collections.emptyMap();
    }

    @Override
    public boolean hasResource() {
        return false;
    }
}
