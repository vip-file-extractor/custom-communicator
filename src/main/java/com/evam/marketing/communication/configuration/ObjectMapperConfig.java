package com.evam.marketing.communication.configuration;

import com.evam.marketing.communication.utils.SerializationUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Object mapper configuration
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Configuration
public class ObjectMapperConfig {
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return SerializationUtils.generateObjectMapper();
    }
}
