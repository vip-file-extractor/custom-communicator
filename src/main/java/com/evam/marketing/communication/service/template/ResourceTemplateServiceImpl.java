package com.evam.marketing.communication.service.template;

import com.evam.marketing.communication.repository.ResourceTemplateRepository;
import com.evam.marketing.communication.repository.model.ResourceTemplate;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resource template service implementation
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Service
@Slf4j
public class ResourceTemplateServiceImpl implements ResourceTemplateService {
    public static final String RESOURCE_TEMPLATE_CACHE_KEY = "resource-template-cache";

    private final ResourceTemplateRepository resourceTemplateRepository;

    public ResourceTemplateServiceImpl(ResourceTemplateRepository resourceTemplateRepository) {
        this.resourceTemplateRepository = resourceTemplateRepository;
    }

    @Cacheable(value = ResourceTemplateServiceImpl.RESOURCE_TEMPLATE_CACHE_KEY, unless = "#result == null or #result == \"\"")
    @Transactional
    @Override
    public Optional<ResourceTemplate> getResourceTemplate(
            String communicationCode,
            String scenarioName,
            int scenarioVersion
    ) {
        return resourceTemplateRepository.findByCommunicationCodeAndScenarioNameAndScenarioVersion(
            communicationCode, scenarioName, scenarioVersion);
    }
}
