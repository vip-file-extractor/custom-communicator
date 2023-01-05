package com.evam.marketing.communication.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.LocalTime;

/**
 * Custom pusher configuration
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Configuration
@ConfigurationProperties(prefix = "custom-pusher")
@Data
public class CustomPusherConfig {
    private String endpointURL;
    private String contentType;
    private String userAgent;
    private String authorization;
    private int connectionTimeout=3000;

    private String silentModeStart;
    private String silentModeEnd;
    private Boolean isTimeRestricted;

    private LocalTime silentModeStartTime;
    private LocalTime silentModeEndTime;

    private int persistJobBufferSize=10000;
    private int persistPoolSize=2;
    private String persistSql;

    @PostConstruct
    public void init() {
        silentModeStartTime = LocalTime.parse(silentModeStart);
        silentModeEndTime = LocalTime.parse(silentModeEnd);
    }

    @Data
    public static class IdentifierDetail {
        private String identifier;
        private String securityCredential;
    }
}
