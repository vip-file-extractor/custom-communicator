package com.evam.marketing.communication.configuration.kafka.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Kafka custom property
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KafkaCustomProperty {
    private String key;
    private String value;
}
