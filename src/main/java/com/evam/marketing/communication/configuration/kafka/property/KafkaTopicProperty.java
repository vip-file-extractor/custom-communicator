package com.evam.marketing.communication.configuration.kafka.property;

import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * Kafka topic property
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Data
public class KafkaTopicProperty {
    private String name;
    private String group;
    private int partition;
    private String autoOffsetReset;
    private boolean enableAutoCommit;
    private int concurrency;
    private short replication;
    private String retentionMs;
    private List<KafkaCustomProperty> customConfigProperties = Collections.emptyList();
    private int maxPoolRecords = 1000;
}
