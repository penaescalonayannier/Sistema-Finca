package com.kynsoft.report.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableCaching
@EnableAsync
@EnableScheduling
public class ReportConfiguration {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("reportCache");
    }

    @Bean
    public ThreadPoolTaskExecutor reportTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(12);
        executor.setMaxPoolSize(24);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ReportExecutor-");
        executor.setKeepAliveSeconds(60);
        executor.initialize();
        return executor;
    }
    
    @Bean
    @ConfigurationProperties(prefix = "report.cache")
    public ReportCacheProperties reportCacheProperties() {
        return new ReportCacheProperties();
    }
    
    @Data
    public static class ReportCacheProperties {
        private boolean enabled = true;
        private long timeToLiveSeconds = 3600; // 1 hora por defecto
        private int maxEntries = 500;
    }
    
    // Programar limpieza de caché periódicamente
    @Bean
    public CacheEvictionTask cacheEvictionTask(CacheManager cacheManager, ReportCacheProperties properties) {
        return new CacheEvictionTask(cacheManager, properties);
    }
}