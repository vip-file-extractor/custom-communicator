package com.evam.marketing.communication.configuration.kafka.property;

import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * Kafka producer property
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Data
public class KafkaProducerProperty {
    private long bufferMemory = 33554432;
    private int batchSize = 16384;
    private long lingerMs;
    private String acks = "1";
    private List<KafkaCustomProperty> customConfigProperties = Collections.emptyList();
}
