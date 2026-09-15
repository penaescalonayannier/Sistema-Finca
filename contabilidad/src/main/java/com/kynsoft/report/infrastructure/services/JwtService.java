package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.infrastructure.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret:c2lzdGVtYS1maW5jYS1zZWNyZXQta2V5LXByb2R1Y3Rpb24tMjAyNi1tdXN0LWJlLWF0LWxlYXN0LTI1Ni1iaXRz}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Generate token for Usuario with tenant claims (fincaId, rol, trabajadorId).
     */
    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol().name());
        claims.put("usuarioId", usuario.getId().toString());

        if (usuario.getTrabajadorId() != null) {
            claims.put("trabajadorId", usuario.getTrabajadorId().toString());
        }

        // Get fincaId from trabajador
        if (usuario.getTrabajador() != null && Hibernate.isInitialized(usuario.getTrabajador())) {
            UUID fincaId = usuario.getTrabajador().getFincaId();
            if (fincaId != null) {
                claims.put("fincaId", fincaId.toString());
            }
        }

        return generateToken(claims, usuario);
    }

    // Claim extraction methods for tenant info
    public String extractFincaId(String token) {
        return extractClaim(token, claims -> claims.get("fincaId", String.class));
    }

    public String extractRol(String token) {
        return extractClaim(token, claims -> claims.get("rol", String.class));
    }

    public String extractUsuarioId(String token) {
        return extractClaim(token, claims -> claims.get("usuarioId", String.class));
    }

    public String extractTrabajadorId(String token) {
        return extractClaim(token, claims -> claims.get("trabajadorId", String.class));
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
