package com.evam.marketing.communication.service.integration.repository;

import com.evam.marketing.communication.service.integration.repository.model.JazzCustomPusher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * Jazz custom pusher repository
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Repository
public interface JazzCustomPusherRepository extends JpaRepository<JazzCustomPusher, String> {
    Collection<OfferOnlyUuid> findAllByOfferUuidIn(Collection<String> offerUuids);

    interface OfferOnlyUuid {
        String getOfferUuid();
    }
}
