package com.evam.marketing.communication.service.integration;

import com.evam.marketing.communication.service.stream.model.request.CustomBenefitRequest;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Service request. It defines the parameters as per evam designer
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Data
@Builder
public class ServiceRequest {
    private CustomBenefitRequest originalRequest;
    private String transactionId;
    private Base base;
    private Raw raw;
    private Computed computed;
    private PostRequest postRequest;
    private Response response;

    @Data
    @Builder
    static class Base {
        private String actorId;
        private String offerUUID;
        private String scenarioName;
        private String offerCode;
    }

    @Data
    @Builder
    static class Raw {
        private String msisdn;
        private String silentMode;
        private String title;
        private String body;
        private String segmentName;
        private String campaignNotification;
        private String description;
        private String token;
        private String os;
    }

    @Data
    @Builder
    static class Computed {
        private boolean silentModeEnabled;
        private ZonedDateTime timestamp;
    }

    @Data
    @Builder
    static class PostRequest {
        private String originTransactionID;
        private String originTimeStamp;
        private String subscriberNumber;
    }

    @Data
    @Builder
    static class Response {
        private String responseStatus;
        private String responseDescription;
    }
}
