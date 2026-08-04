package com.kynsoft.report.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;

public class CacheEvictionTask {
    private static final Logger logger = LoggerFactory.getLogger(CacheEvictionTask.class);
    
    private final CacheManager cacheManager;
    private final ReportConfiguration.ReportCacheProperties properties;
    public CacheEvictionTask(CacheManager cacheManager, ReportConfiguration.ReportCacheProperties properties) {
        this.cacheManager = cacheManager;
        this.properties = properties;
    }
    
    @Scheduled(fixedRateString = "${report.cache.eviction-interval-ms:3600000}")
    public void evictExpiredCaches() {
        if (!properties.isEnabled()) {
            return;
        }
        
        logger.debug("Ejecutando limpieza programada de caché de reportes");
        cacheManager.getCache("reportCache").clear();
        logger.debug("Limpieza de caché completada");
    }
}