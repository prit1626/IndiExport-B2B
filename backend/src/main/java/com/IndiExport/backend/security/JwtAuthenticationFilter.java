package com.IndiExport.backend.security;

import com.IndiExport.backend.service.JwtService;
import com.IndiExport.backend.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * JWT Authentication Filter
 * Intercepts requests and validates JWT access token
 * Extracts claims and sets authentication context
 * 
 * Filter chain:
 * 1. Extract token from "Authorization: Bearer <token>"
 * 2. Validate token signature and expiration
 * 3. Extract claims (userId, email, roles)
 * 4. Set Spring Security Authentication
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Extract JWT token from request header
            String token = extractTokenFromRequest(request);

            if (token != null) {
                // Validate token
                if (jwtService.validateToken(token)) {
                    // Extract claims
                    Claims claims = jwtService.extractAllClaims(token);

                    // Get token type (access or refresh)
                    String tokenType = claims.get("type", String.class);

                    // Only process ACCESS tokens in this filter
                    if ("access".equals(tokenType)) {
                        // Extract user details from claims
                        String email = jwtService.extractEmail(token);
                        String userId = jwtService.extractUserId(token);
                        String rolesString = jwtService.extractRoles(token);

                        // Parse roles: "ROLE_BUYER,ROLE_SELLER,ROLE_ADMIN"
                        List<SimpleGrantedAuthority> authorities = parseAuthorities(rolesString);

                        // Create UserDetails object (using Spring Security's User)
                        org.springframework.security.core.userdetails.User principal = 
                                new org.springframework.security.core.userdetails.User(email, "", authorities);

                        // Set authentication in context
                        UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(principal, null, authorities);
                        
                        // Store userId and email as additional context
                        authentication.setDetails(new JwtAuthenticationDetails(userId, email, rolesString));
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

        } catch (Exception e) {
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     * Expected format: "Authorization: Bearer <token>"
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * Parse authorities from roles string
     * Format: "ROLE_BUYER,ROLE_SELLER,ROLE_ADMIN"
     */
    private List<SimpleGrantedAuthority> parseAuthorities(String rolesString) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        if (rolesString != null && !rolesString.isEmpty()) {
            String[] roles = rolesString.split(",");
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role.trim()));
            }
        }

        return authorities;
    }

    /**
     * Custom authentication details to store additional JWT info
     */
    public static class JwtAuthenticationDetails {
        private final String userId;
        private final String email;
        private final String roles;

        public JwtAuthenticationDetails(String userId, String email, String roles) {
            this.userId = userId;
            this.email = email;
            this.roles = roles;
        }

        public String getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }

        public String getRoles() {
            return roles;
        }
    }
}
