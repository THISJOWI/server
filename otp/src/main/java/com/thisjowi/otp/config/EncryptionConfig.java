package com.thisjowi.otp.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import com.thisjowi.otp.util.EncryptionUtil;

import java.util.Map;

@Configuration
public class EncryptionConfig {

    @Bean
    @DependsOn("encryptionUtil")
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(EncryptionUtil encryptionUtil) {
        return new HibernatePropertiesCustomizer() {
            @Override
            public void customize(Map<String, Object> hibernateProperties) {
                // This forces EncryptionUtil to be initialized before Hibernate starts
            }
        };
    }
}
