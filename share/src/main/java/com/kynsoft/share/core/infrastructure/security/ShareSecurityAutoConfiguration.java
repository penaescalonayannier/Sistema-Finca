package com.kynsoft.share.core.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Auto-configuration that provides a default SecurityFilterChain for
 * microservices that don't define their own.
 *
 * Security model: the Gateway handles ALL authentication and authorization.
 * Internal microservices allow all requests (anyRequest().permitAll()) but
 * keep oauth2ResourceServer configured so Spring parses the JWT when the
 * gateway forwards it. This enables:
 * - @AuthenticationPrincipal Jwt jwt in controllers
 * - SpringSecurityAuditorAware to read the user from the token
 * - Swagger UI and /v3/api-docs accessible without auth
 */
@AutoConfiguration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnMissingBean(SecurityFilterChain.class)
public class ShareSecurityAutoConfiguration {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:}")
    private String jwkSetUri;

    @Bean
    public SecurityFilterChain shareSecurityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        // Gateway handles auth. Internal services allow everything.
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(shareJwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                );

        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder shareJwtDecoder() {
        if (jwkSetUri != null && !jwkSetUri.isEmpty()) {
            return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        }
        if (issuerUri != null && !issuerUri.isEmpty()) {
            return JwtDecoders.fromIssuerLocation(issuerUri);
        }
        throw new IllegalStateException(
            "Either spring.security.oauth2.resourceserver.jwt.jwk-set-uri " +
            "or spring.security.oauth2.resourceserver.jwt.issuer-uri must be configured"
        );
    }
}
