package com.evam.marketing.communication.controller.health;

import com.evam.marketing.communication.controller.health.model.response.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Health controller
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@RestController
@RequestMapping(HealthControllerMapping.HEALTH_PATH)
public class HealthController {
    @GetMapping
    public Mono<HealthResponse> healthCheck() {
        return Mono.just(HealthResponse.builder().status("UP").build());
    }
}
