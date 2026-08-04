package com.kynsoft.report.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class JasperReportsConfig {

    @Bean
    public Properties jasperReportsProperties() {
        Properties properties = new Properties();
        
        // Usar el compilador JDT de Java en lugar del compilador JavaScript predeterminado
        properties.setProperty("net.sf.jasperreports.compiler.class", 
                              "net.sf.jasperreports.engine.design.JRJdtCompiler");
        
        // Configuraciones adicionales para mejorar la compilación de reportes
        properties.setProperty("net.sf.jasperreports.compiler.xml.validation", "false");
        properties.setProperty("net.sf.jasperreports.compiler.keep.java.file", "false");
        properties.setProperty("net.sf.jasperreports.default.font.name", "DejaVu Sans");
        properties.setProperty("net.sf.jasperreports.default.pdf.encoding", "UTF-8");
        
        // Configuración para threads (optimizada para 12 cores)
        properties.setProperty("net.sf.jasperreports.compiler.max.threads.per.cpu", "8");
        properties.setProperty("net.sf.jasperreports.compiler.max.threads", "96");
        
        // Cargar las propiedades en el sistema
        System.getProperties().putAll(properties);
        
        return properties;
    }
}