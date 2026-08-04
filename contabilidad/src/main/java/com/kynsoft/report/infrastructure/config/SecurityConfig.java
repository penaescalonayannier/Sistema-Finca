/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.kynsoft.report.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers(
                    "/health",
                    "/health/**",
                    "/actuator/health",
                    "/actuator/health/**",
                    "/actuator/info",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/openapi.json",
                    "/openapi/**",
                    "/api/**"  // Frontend API endpoints - public access
                ).permitAll()
                .anyRequest().authenticated();
        return http.build();
    }
}