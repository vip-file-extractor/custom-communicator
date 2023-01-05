package com.evam.marketing.communication.service.integration;

import com.evam.marketing.communication.repository.model.ResourceTemplate;
import com.evam.marketing.communication.service.integration.model.request.CommunicationRequest;
import com.evam.marketing.communication.service.integration.repository.JazzCustomPusherRepository;
import com.evam.marketing.communication.service.stream.model.request.StreamRequest;
import com.evam.marketing.communication.service.template.ResourceTemplateService;
import com.evam.marketing.communication.utils.CommunicationConversionUtils;
import com.evam.marketing.communication.utils.PerformanceCounter;
import com.evam.marketing.communication.utils.ResourceTemplateUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Communication service implementation
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Service
@Slf4j
public class CommunicationServiceImpl implements CommunicationService {
    private final ResourceTemplateService resourceTemplateService;
    private final PerformanceCounter performanceCounter;
    private final CustomPusherService customPusherService;

    @Value("${kafka.communication-uuid-check:false}")
    private boolean communicationUUIDCheck;

    public CommunicationServiceImpl(
        ResourceTemplateService resourceTemplateService,
        PerformanceCounter performanceCounter,
        CustomPusherService customPusherService
    ) {
        this.resourceTemplateService = resourceTemplateService;
        this.performanceCounter = performanceCounter;
        this.customPusherService = customPusherService;
    }

    @Override
    public void execute(List<StreamRequest> requestList) {
        Set<String> set = requestList.stream()
                .map(StreamRequest::getUuid)
                .collect(Collectors.toSet());
        Set<String> duplicates = customPusherService.findAllByOfferUuidIn(set)
                .stream()
                .map(JazzCustomPusherRepository.OfferOnlyUuid::getOfferUuid)
                .collect(Collectors.toSet());

        if (!duplicates.isEmpty()) {
            String processedUUIDsStr = String.join(",", set);
            log.warn("I [thread: {} | Passing already processed UUIDs [{}]",
                    Thread.currentThread().getName(),
                    processedUUIDsStr);
            requestList.removeIf(request -> duplicates.contains(request.getUuid()));
        }

        for (StreamRequest streamRequest : requestList) {
            customPusherService.send(generateCommunicationRequest(streamRequest));
        }
    }

    private CommunicationRequest generateCommunicationRequest(StreamRequest streamRequest) {
        Optional<ResourceTemplate> resourceTemplateOptional = Optional.empty();
        if (streamRequest.hasResource()) {
            resourceTemplateOptional = resourceTemplateService.getResourceTemplate(
                streamRequest.getCode(),
                streamRequest.getScenario(), streamRequest.getScenarioVersion());
        }

        String body = null;
        if (resourceTemplateOptional.isPresent() && !Objects.isNull(
            resourceTemplateOptional.get().getContent())) {
            ResourceTemplate resourceTemplate = resourceTemplateOptional.get();
            body = ResourceTemplateUtils.enrichContent(streamRequest,
                resourceTemplate.getContent());
        }

        return CommunicationConversionUtils.streamRequestToCommunicationRequest(streamRequest, body);
    }
}
