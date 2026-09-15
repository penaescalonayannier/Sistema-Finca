package com.kynsoft.report.infrastructure.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kynsoft.share.core.domain.request.SortTypeEnum;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            SimpleModule sortModule = new SimpleModule();
            sortModule.addDeserializer(SortTypeEnum.class, new SortTypeEnumDeserializer());
            // Use modulesToInstall to ADD modules without replacing defaults (like JavaTimeModule)
            builder.modulesToInstall(sortModule, new JavaTimeModule());
        };
    }

    public static class SortTypeEnumDeserializer extends JsonDeserializer<SortTypeEnum> {
        @Override
        public SortTypeEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            if (value == null) {
                return null;
            }
            // Map DESC to DES for compatibility
            if ("DESC".equalsIgnoreCase(value)) {
                return SortTypeEnum.DES;
            }
            // Handle standard values
            return SortTypeEnum.valueOf(value.toUpperCase());
        }
    }
}
