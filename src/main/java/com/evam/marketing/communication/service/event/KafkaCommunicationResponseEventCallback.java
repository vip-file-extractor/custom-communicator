package com.evam.marketing.communication.service.event;

import com.evam.marketing.communication.service.event.model.CommunicationResponseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Kafka communication response event callback
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Slf4j
public class KafkaCommunicationResponseEventCallback implements
        ListenableFutureCallback<SendResult<String, CommunicationResponseEvent>> {

    @Override
    public void onFailure(Throwable e) {
        log.warn("Kafka message send fail!", e);
    }

    @Override
    public void onSuccess(SendResult<String, CommunicationResponseEvent> result) {
        log.debug("Kafka message successfully sent. {}", result);
    }
}
