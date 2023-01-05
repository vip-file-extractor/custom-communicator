package com.evam.marketing.communication.service.integration.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Jazz custom pusher
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Entity
@Table(name = "JAZZ_CUSTOM_PUSHER", schema = "EVAM")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JazzCustomPusher implements Serializable {
    @Id
    @GeneratedValue(generator = "custom_pusher_seq",strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "TIMESTAMP")
    private LocalDateTime timestamp;
    @Column(name = "CAMPAIGN_ID")
    private String campaignId;
    @Column(name = "MSISDN")
    private String msisdn;
    @Column(name = "INSERT_TIME")
    @CreationTimestamp
    private LocalDateTime insertTime;
    @Column(name = "SILENTMODE")
    private String silentmode;
    @Column(name = "OFFER_UUID")
    private String offerUuid;
    @Column(name = "SUBMIT_DATE")
    private LocalDate submitDate;
    @Column(name = "SEGMENT_NAME")
    private String segmentName;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "BODY")
    private String body;
    @Column(name = "CAMPAIGN_NOTIFICATION")
    private String campaignNotification;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "TOKEN")
    private String token;
}
