package com.evam.marketing.communication.service.stream.model.request;

import com.evam.marketing.communication.service.client.model.Parameter;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Custom communication stream request
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
public class CustomCommunicationStreamRequest extends AbstractStreamRequest {
    private List<Parameter> parameters;
}
