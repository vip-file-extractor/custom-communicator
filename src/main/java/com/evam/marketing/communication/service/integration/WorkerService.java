package com.evam.marketing.communication.service.integration;

import com.evam.marketing.communication.configuration.CustomPusherConfig;
import com.evam.marketing.communication.service.client.AbstractCommunicationClient;
import com.evam.marketing.communication.service.event.model.CommunicationFailEvent;
import com.evam.marketing.communication.service.event.model.CommunicationSuccessEvent;
import com.evam.marketing.communication.service.event.KafkaProducerService;
import com.evam.marketing.communication.service.integration.firebase.FirebaseClient;
import com.evam.marketing.communication.service.integration.firebase.model.Message;
import com.evam.marketing.communication.service.integration.firebase.model.Notification;
import com.evam.marketing.communication.service.integration.firebase.model.PushNotificationCustomData;
import com.evam.marketing.communication.service.integration.firebase.model.PushNotificationRequest;
import com.evam.marketing.communication.service.integration.model.request.CommunicationRequest;
import com.evam.marketing.communication.service.stream.model.request.CustomBenefitRequest;
import com.evam.marketing.communication.utils.PerformanceCounter;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Worker service
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Service
@Slf4j
public class WorkerService extends AbstractCommunicationClient {
    private static final String ERROR_CODE = "2";
    private static final String ERROR_DESC = "There is no response retrieved from web service!";

    private final PersistenceService persistenceService;
    private final PerformanceCounter performanceCounter;
    private final CustomPusherConfig customPusherConfig;

    public WorkerService(
            PersistenceService persistenceService,
            PerformanceCounter performanceCounter,
            KafkaProducerService kafkaProducerService,
            CustomPusherConfig customPusherConfig
    ) {
        super(kafkaProducerService);
        this.persistenceService = persistenceService;
        this.performanceCounter = performanceCounter;
        this.customPusherConfig = customPusherConfig;
    }

    @Override
    public void send(CommunicationRequest communicationRequest) {
    }

    @Override
    public String getProvider() {
        return null;
    }

    @RateLimiter(name = "client-limiter")
    public void submit(ServiceRequest serviceRequest) throws IOException, FirebaseMessagingException {
        log.debug(serviceRequest.getRaw().getMsisdn());
        log.debug(serviceRequest.getRaw().getBody());
        log.debug(serviceRequest.getRaw().getTitle());
        log.debug(serviceRequest.getRaw().getCampaignNotification());
        PushNotificationCustomData customData = PushNotificationCustomData.PushNotificationCustomDataBuilder
                .aPushNotificationCustomData()
                .campaignNotification(Boolean.parseBoolean(serviceRequest.getRaw().getCampaignNotification()))
                .description(serviceRequest.getRaw().getDescription())
                .build();
        Notification notification = Notification.builder()
                .title(serviceRequest.getRaw().getTitle())
                .body(serviceRequest.getRaw().getBody())
                .build();
        Message message = Message.builder()
                .notification(notification)
                .token(serviceRequest.getRaw().getToken())
                .data(customData)
                .build();
        PushNotificationRequest request = PushNotificationRequest.builder().message(message).build();
        log.debug("request: {}", request);

        BatchResponse batchResponse = FirebaseClient.getInstance()
                .pushNotification(request, serviceRequest.getRaw().getOs());

        log.info("SuccessCount: {}", batchResponse.getSuccessCount());
        log.info("FailCount: {}", batchResponse.getFailureCount());
        batchResponse.getResponses()
                .forEach(sendResponse -> {
                    if (sendResponse.isSuccessful()) {
                        persistenceService.add(serviceRequest);
                        ServiceRequest.Response res = ServiceRequest.Response.builder()
                                .responseStatus("responsecode")
                                .responseDescription("responsemessage")
                                .build();
                        serviceRequest.setResponse(res);
                        sendEvent(toCommunicationSuccessEvent(
                                serviceRequest.getOriginalRequest(),
                                serviceRequest.getResponse().getResponseStatus()));
                    } else {
                        sendEvent(toCommunicationFailEvent(serviceRequest.getOriginalRequest(),
                                serviceRequest.getResponse() != null
                                        ? serviceRequest.getResponse().getResponseStatus() : ERROR_CODE,
                                serviceRequest.getResponse() != null
                                        ? serviceRequest.getResponse().getResponseDescription() : ERROR_DESC));
                    }
                    log.info("Exception: " + sendResponse.getException());
                    log.info("MessageId: " + sendResponse.getMessageId());
                });
        persistenceService.add(serviceRequest);
        ServiceRequest.Response res = ServiceRequest.Response.builder()
                .responseStatus("responsecode")
                .responseDescription("responsemessage")
                .build();
        serviceRequest.setResponse(res);
        sendEvent(toCommunicationSuccessEvent(
                serviceRequest.getOriginalRequest(),
                serviceRequest.getResponse().getResponseStatus()));
    }

    private CommunicationSuccessEvent toCommunicationSuccessEvent(
            CustomBenefitRequest customBenefitRequest, String message) {
        return CommunicationSuccessEvent.builder()
                .scenario(customBenefitRequest.getScenario())
                .actorId(customBenefitRequest.getActorId())
                .communicationCode(customBenefitRequest.getCode())
                .communicationUUID(customBenefitRequest.getUuid())
                .name(customBenefitRequest.getName())
                .message(message)
                .build();
    }

    private CommunicationFailEvent toCommunicationFailEvent(
            CustomBenefitRequest customBenefitRequest,
            String message, String reason) {
        return CommunicationFailEvent.builder()
                .scenario(customBenefitRequest.getScenario())
                .actorId(customBenefitRequest.getActorId())
                .communicationCode(customBenefitRequest.getCode())
                .communicationUUID(customBenefitRequest.getUuid())
                .name(customBenefitRequest.getName())
                .message(message)
                .reason(reason)
                .build();
    }
}
