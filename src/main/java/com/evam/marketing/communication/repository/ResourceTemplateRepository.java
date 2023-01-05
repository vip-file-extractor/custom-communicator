package com.evam.marketing.communication.repository;

import com.evam.marketing.communication.repository.model.ResourceTemplate;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Resource template repository
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Repository
public interface ResourceTemplateRepository extends JpaRepository<ResourceTemplate, Long> {
    Optional<ResourceTemplate> findByCommunicationCodeAndScenarioNameAndScenarioVersion(
        String communicationCode,
        String scenarioName,
        int scenarioVersion
    );
}
