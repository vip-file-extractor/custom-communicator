package com.evam.marketing.communication.service.client.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Parameter for campaigns
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class Parameter {
    private String name;
    private String value;
}
