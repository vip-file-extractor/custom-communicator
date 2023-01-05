package com.evam.marketing.communication.service.stream.model.request;

import com.evam.marketing.communication.service.client.model.Parameter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Custom benefit request
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CustomBenefitRequest extends AbstractStreamRequest {
    @ToString.Exclude
    private List<Parameter> customBenefitParameters;
    private String customBenefitTemplate;

    public List<Parameter> getCustomBenefitParameters() {
        if (Objects.isNull(customBenefitParameters)) {
            return Collections.emptyList();
        }

        return customBenefitParameters;
    }
}
