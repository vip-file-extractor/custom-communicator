package com.evam.marketing.communication.service.integration;

import com.evam.marketing.communication.configuration.CustomPusherConfig;
import com.evam.marketing.communication.service.client.AbstractCommunicationClient;
import com.evam.marketing.communication.service.client.model.CustomCommunicationRequest;
import com.evam.marketing.communication.service.client.model.Parameter;
import com.evam.marketing.communication.service.event.model.CommunicationSuccessEvent;
import com.evam.marketing.communication.service.event.KafkaProducerService;
import com.evam.marketing.communication.service.integration.model.request.CommunicationRequest;
import com.evam.marketing.communication.service.integration.repository.JazzCustomPusherRepository;
import com.evam.marketing.communication.service.integration.repository.JazzCustomPusherRepository.OfferOnlyUuid;
import com.evam.marketing.communication.service.stream.model.request.CustomBenefitRequest;
import com.evam.marketing.communication.utils.PerformanceCounter;
import com.evam.marketing.communication.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * Custom pusher service
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Service
@Slf4j
public class CustomPusherService extends AbstractCommunicationClient {
    public static final String DATE_PATTERN = "yyyyMMdd'T'HH:mm:ssZ";
    public static final String STATUS = "-1";
    public static final String SILENT = "Silent Mode";
    public static final String SILENT_YES = "Y";
    public static final String SILENT_NO = "N";
    public static final String TIME_BOUND = "Time Constraint";
    private static final ThreadLocal<DateTimeFormatter> DF;

    private final CustomPusherConfig customPusherConfig;
    private final JazzCustomPusherRepository jazzCustomPusherRepository;
    private final PersistenceService persistenceService;
    private final WorkerService workerService;
    private final PerformanceCounter performanceCounter;

    static {
        DF = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern(DATE_PATTERN));
    }

    public CustomPusherService(
            KafkaProducerService kafkaProducerService,
            CustomPusherConfig customPusherConfig,
            JazzCustomPusherRepository jazzCustomPusherRepository,
            PersistenceService persistenceService,
            WorkerService workerService,
            PerformanceCounter performanceCounter
    ) {
        super(kafkaProducerService);
        this.customPusherConfig = customPusherConfig;
        this.jazzCustomPusherRepository = jazzCustomPusherRepository;
        this.persistenceService = persistenceService;
        this.workerService = workerService;
        this.performanceCounter = performanceCounter;
    }

    public Collection<OfferOnlyUuid> findAllByOfferUuidIn(Collection<String> offerUuids) {
        return jazzCustomPusherRepository.findAllByOfferUuidIn(offerUuids);
    }

    /**
     * It processes the <code>CommunicationRequest</code> in order to send
     * either the success event or the fail event to the engine. If the
     * request causes an error, it is believed that the request is ineligible
     * for sending events to engine. Therefore, no action on error is required.
     *
     * @param request the standard communication request
     */
    @Override
    public void send(CommunicationRequest request) {
        CustomCommunicationRequest customCommRequest = (CustomCommunicationRequest) request;
        CustomBenefitRequest customBenefitRequest = new CustomBenefitRequest();
        customBenefitRequest.setCustomBenefitParameters(customCommRequest.getParameters());

        try {
            ZonedDateTime now = ZonedDateTime.now();
            ServiceRequest serviceRequest = this.buildServiceRequest(now, customCommRequest);

            if (serviceRequest.getComputed().isSilentModeEnabled()) {
                ServiceRequest.Response response = ServiceRequest.Response.builder()
                        .responseStatus(STATUS)
                        .responseDescription(SILENT)
                        .build();
                serviceRequest.setResponse(response);
                this.persistenceService.add(serviceRequest);
                this.performanceCounter.incrementEventCountSilent();

                sendEvent(toCustomCommSuccessEvent(customBenefitRequest, customCommRequest));
            } else if (Boolean.TRUE.equals(this.customPusherConfig.getIsTimeRestricted())) {
                // If the instance does not need specific time window, set isTimeRestricted to false.

                if (!isTimeWindowOk(now.toLocalDateTime())) {
                    if (log.isDebugEnabled()) {
                        log.info("Offer[{}] could not sent. Current time is out of given window", request);
                    }
                    ServiceRequest.Response response = ServiceRequest.Response.builder()
                            .responseStatus(STATUS)
                            .responseDescription(TIME_BOUND)
                            .build();
                    serviceRequest.setResponse(response);
                    this.persistenceService.add(serviceRequest);
                    this.performanceCounter.incrementEventCountFail();
                } else {
                    this.workerService.submit(serviceRequest);
                }
            } else {
                this.workerService.submit(serviceRequest);
            }
        } catch (Exception e) {
            log.error("Execution failed so ignoring request: {}", customBenefitRequest, e);
        }
    }

    @Override
    public String getProvider() {
        return null;
    }

    /**
     * It builds the standard <code>ServiceRequest</code> based time and
     * communication request.
     *
     * @param now the date for the request
     * @param request the customized communication request
     * @return the service request
     */
    private ServiceRequest buildServiceRequest(ZonedDateTime now, CustomCommunicationRequest request) {
        ServiceRequest.ServiceRequestBuilder builder = ServiceRequest.builder();
        CustomBenefitRequest customBenefitRequest = new CustomBenefitRequest();
        customBenefitRequest.setCustomBenefitParameters(request.getParameters());

        String commUUID = request.getCommunicationUUID();
        commUUID = StringUtils.replace(commUUID, "-", "");
        builder.transactionId(commUUID);
        builder.originalRequest(customBenefitRequest);
        ServiceRequest.Base base = this.buildBase(request);
        builder.base(base);
        ServiceRequest.Raw raw = this.buildRaw(customBenefitRequest);
        builder.raw(raw);
        ServiceRequest.Computed computed = this.getComputed(now, raw);
        builder.computed(computed);
        ServiceRequest.PostRequest postRequest = this.getPostRequest(now, request, raw);
        builder.postRequest(postRequest);

        return builder.build();
    }

    /**
     * It builds the base for the standard service request from the customized
     * communication request.
     *
     * @param request the communication request
     * @return the base data for the service request
     */
    private ServiceRequest.Base buildBase(CustomCommunicationRequest request) {
        return ServiceRequest.Base.builder()
                .actorId(request.getActorId())
                .offerUUID(request.getCommunicationUUID())
                .scenarioName(request.getScenario())
                .build();
    }

    /**
     * It builds the raw parameters for the standard service request from the
     * customized benefit request.
     *
     * @param request the benefit request
     * @return the raw parameters for the service request
     */
    private ServiceRequest.Raw buildRaw(CustomBenefitRequest request) {
        ServiceRequest.Raw.RawBuilder rawBuilder = ServiceRequest.Raw.builder();
        log.debug("getting parameters {}", request);
        for (Parameter parameter : request.getCustomBenefitParameters()) {
            String name = parameter.getName();
            String value = parameter.getValue();
            switch (name) {
                case "MSISDN":
                    rawBuilder.msisdn(value);
                    log.debug(value);
                    break;
                case "TITLE":
                    rawBuilder.title(value);
                    log.debug(value);
                    break;
                case "SILENTMODE":
                    rawBuilder.silentMode(value);
                    break;
                case "BODY":
                    rawBuilder.body(value);
                    break;
                case "SEGMENTNAME":
                    rawBuilder.segmentName(value);
                    break;
                case "CAMPAIGNNOTIFICATION":
                    rawBuilder.campaignNotification(value);
                    log.debug(value);
                    break;
                case "DESCRIPTION":
                    rawBuilder.description(value);
                    break;
                case "TOKEN":
                    rawBuilder.token(value);
                    break;
                case "OS":
                    rawBuilder.os(value);
                    break;
                default:
                    break;
            }
        }
        return rawBuilder.build();
    }

    /**
     * It builds the computational parameters for the standard service request
     * from the request time and raw parameters.
     *
     * @param now the time for the request
     * @param raw the raw parameters of the service reqeust
     * @return the computational parameters
     */
    private ServiceRequest.Computed getComputed(ZonedDateTime now, ServiceRequest.Raw raw) {
        ServiceRequest.Computed.ComputedBuilder computedBuilder = ServiceRequest.Computed.builder();
        if (SILENT_YES.equalsIgnoreCase(raw.getSilentMode()) || SILENT_NO.equalsIgnoreCase(raw.getSilentMode())) {
            computedBuilder.silentModeEnabled(SILENT_YES.equalsIgnoreCase(raw.getSilentMode()));
        } else {
            computedBuilder.silentModeEnabled(true);
        }
        computedBuilder.timestamp(now);
        return computedBuilder.build();
    }

    /**
     * It builds the post request of the service request.
     *
     * @param now the time for the request
     * @param commRequest the customized communication request
     * @param raw the raw parameters for the service request
     * @return the post request for the service request
     */
    private ServiceRequest.PostRequest getPostRequest(
            ZonedDateTime now,
            CustomCommunicationRequest commRequest,
            ServiceRequest.Raw raw
    ) {
        ServiceRequest.PostRequest.PostRequestBuilder postRequestBuilder = ServiceRequest.PostRequest.builder();
        postRequestBuilder.originTransactionID(commRequest.getCommunicationUUID());
        postRequestBuilder.originTimeStamp(DF.get().format(now));
        postRequestBuilder.subscriberNumber(raw.getMsisdn());

        return postRequestBuilder.build();
    }

    /**
     * It converts the communication's request parameters to success event.
     *
     * @param customBenefitRequest the customized request
     * @param commRequest the communication request
     * @return the success event
     */
    CommunicationSuccessEvent toCustomCommSuccessEvent(
            CustomBenefitRequest customBenefitRequest, CustomCommunicationRequest commRequest) {
        return CommunicationSuccessEvent.builder()
                .scenario(commRequest.getScenario())
                .actorId(customBenefitRequest.getActorId())
                .communicationUUID(commRequest.getCommunicationUUID())
                .name(customBenefitRequest.getName())
                .message(SILENT)
                .build();
    }

    /**
     * It checks whether the sending event process is occurring during the
     * allowed time window.
     *
     * @param now the time for the request
     * @return true if the process is occurring during the allowed time window.
     */
    private boolean isTimeWindowOk(LocalDateTime now) {
        LocalTime localTime = now.toLocalTime();
        LocalTime silentModeStartTime = customPusherConfig.getSilentModeStartTime();
        boolean before = silentModeStartTime.isAfter(localTime);
        LocalTime silentModeEndTime = customPusherConfig.getSilentModeEndTime();
        boolean after = silentModeEndTime.isBefore(localTime);
        return before && after;
    }
}
