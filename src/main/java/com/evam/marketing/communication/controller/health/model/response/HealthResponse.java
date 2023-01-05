package com.evam.marketing.communication.controller.health.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Health response
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class HealthResponse {
    private String status;
}
