package com.evam.marketing.communication.service.integration.firebase;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.Collections;
import java.util.Map;

/**
 * Push notification request
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
public final class SerializationUtils {
    private static final ObjectMapper objectMapper = generateObjectMapper();

    private SerializationUtils() {
        // do nothing
    }

    public static ObjectMapper generateObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true);
        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    public static <T> String serialize(T model) {
        try {
            return objectMapper.writeValueAsString(model);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> Map<String, Object> serializeAsMap(T model) {
        try {
            return objectMapper.convertValue(model, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
