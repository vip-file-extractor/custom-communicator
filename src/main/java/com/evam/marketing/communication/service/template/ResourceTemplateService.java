package com.evam.marketing.communication.service.template;

import com.evam.marketing.communication.repository.model.ResourceTemplate;

import java.util.Optional;

/**
 * Resource template service
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
public interface ResourceTemplateService {
    Optional<ResourceTemplate> getResourceTemplate(
            String communicationCode,
            String scenarioName,
            int scenarioVersion);
}
