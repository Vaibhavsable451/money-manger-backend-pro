package com.vaibhav.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class JwtProvider {
    
    private static final long DEFAULT_EXPIRATION_MS = 86400000L; // 24 hours
    private final SecretKey key;
    private final long expirationMs;

    public JwtProvider(@Value("${jwt.secret}") String secretKey,
                      @Value("${jwt.expiration:86400000}") String expirationMsStr) {
        long expirationMs;
        try {
            // Extract numeric part if there's a comment in the value (e.g., "86400000#24hoursinmilliseconds")
            if (expirationMsStr.contains("#")) {
                expirationMsStr = expirationMsStr.substring(0, expirationMsStr.indexOf('#'));
            }
            expirationMs = Long.parseLong(expirationMsStr.trim());
        } catch (NumberFormatException e) {
            // Fallback to default if parsing fails
            expirationMs = DEFAULT_EXPIRATION_MS;
        }
        
        // Ensure the secret key is valid
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret key must not be empty");
        }
        
        try {
            this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT secret key: " + e.getMessage(), e);
        }
        
        this.expirationMs = expirationMs > 0 ? expirationMs : DEFAULT_EXPIRATION_MS;
    }

    public String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .claim("email", auth.getName())
                .claim("authorities", roles)
                .signWith(key)
                .compact();
    }
    

    public String getEmailFromJwtToken(String jwt) {
        try {
            if (jwt == null || !jwt.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Invalid JWT token");
            }
            jwt = jwt.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            return String.valueOf(claims.get("email"));
        } catch (Exception e) {
            throw new SecurityException("Failed to extract email from JWT token", e);
        }
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return "";
        }
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth != null && !auth.isEmpty())
                .collect(java.util.stream.Collectors.joining(","));
    }
}
