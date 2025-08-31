package com.vaibhav.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

// Remove @Component since we're not using this class anymore
// @Component
public class JwtTokenValidator extends OncePerRequestFilter {
    
    private final SecretKey key;
    
    public JwtTokenValidator(String secretKey) {
        this.key = Keys.hmacShaKeyFor(decodeBase64(secretKey));
    }

    private byte[] decodeBase64(String base64) {
        try {
            return Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decode JWT secret", e);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(this.key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String email = claims.get("email", String.class);
                String authorities = claims.get("authorities", String.class);

                if (email != null && authorities != null) {
                    List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                    Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auth);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new RuntimeException("Missing required claims in JWT");
                }

            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
                return;
            } catch (io.jsonwebtoken. SignatureException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature");
                return;
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed JWT token");
                return;
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while validating JWT token");
                return;
            }
        }

        // Continue the filter chain if no JWT or if processing was successful
        filterChain.doFilter(request, response);
    }
}
