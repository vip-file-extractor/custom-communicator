package com.evam.marketing.communication.configuration;

import com.evam.encryptor.utility.CryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.annotation.Configuration;

/**
 * Kafka consumer configuration
 *
 * @author Abdul Wadood
 * @since 7.2.0
 */
@Configuration
@Slf4j
public class EvamEncryptor implements StringEncryptor {

    public String encrypt(String data) {
        return CryptUtils.encrypt(data);
    }

    public String decrypt(String encryptedData) {
        return CryptUtils.decrypt(encryptedData);
    }
}
