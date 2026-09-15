package com.kynsoft.share.core.infrastructure.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Unified AuditorAware implementation for all microservices.
 * Extraction strategy: SecurityContext (JWT) → Gateway header → "system".
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final String HEADER_USER_ID = "X-User-Id";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String sub = jwt.getClaimAsString("sub");
            if (sub != null && !sub.isEmpty()) {
                return Optional.of(sub);
            }
            String username = jwt.getClaimAsString("preferred_username");
            if (username != null && !username.isEmpty()) {
                return Optional.of(username);
            }
        }

        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            String userId = servletAttrs.getRequest().getHeader(HEADER_USER_ID);
            if (userId != null && !userId.isEmpty()) {
                return Optional.of(userId);
            }
        }

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getName() != null
                && !"anonymousUser".equals(authentication.getName())) {
            return Optional.of(authentication.getName());
        }

        return Optional.of("system");
    }
}
