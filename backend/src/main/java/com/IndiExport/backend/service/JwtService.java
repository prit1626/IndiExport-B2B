package com.IndiExport.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JwtService for generating, validating, and extracting claims from JWT tokens.
 * Uses HS512 (HMAC with SHA-512) for signing.
 * 
 * Access Token: Short-lived (default 15 minutes), contains user id, email, roles
 * Refresh Token: Long-lived (default 7 days), used to get new access token
 */
@Service
public class JwtService {

    @Value("${app.jwt.secret:your-secret-key-change-in-production-at-least-256-bits-long}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:900000}") // 15 minutes in milliseconds
    private long accessTokenExpiration;

    @Value("${app.jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private long refreshTokenExpiration;

    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    /**
     * Generate JWT access token from Authentication
     * Contains: userId, email, roles
     */
    public String generateAccessToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TOKEN_TYPE_ACCESS);
        
        // Extract user details from authentication principal
        String email = authentication.getName();
        String userId = extractUserIdFromAuthentication(authentication);
        String roles = authentication.getAuthorities().toString();
        
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("roles", roles);

        return buildToken(claims, email, accessTokenExpiration);
    }

    /**
     * Generate JWT access token with userId and email
     * Useful for refresh token flow
     */
    public String generateAccessToken(String userId, String email, String roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TOKEN_TYPE_ACCESS);
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("roles", roles);

        return buildToken(claims, email, accessTokenExpiration);
    }

    /**
     * Generate JWT refresh token from Authentication
     * Contains: userId, email
     * Refresh tokens should be stored in DB for revocation
     */
    public String generateRefreshToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TOKEN_TYPE_REFRESH);
        
        String email = authentication.getName();
        String userId = extractUserIdFromAuthentication(authentication);
        
        claims.put("userId", userId);
        claims.put("email", email);

        return buildToken(claims, email, refreshTokenExpiration);
    }

    /**
     * Generate JWT refresh token with userId and email
     */
    public String generateRefreshToken(String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", TOKEN_TYPE_REFRESH);
        claims.put("userId", userId);
        claims.put("email", email);

        return buildToken(claims, email, refreshTokenExpiration);
    }

    /**
     * Build JWT token with claims, subject, and expiration
     */
    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // Email
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validate JWT token (checks signature and expiration)
     * Returns true if valid, false if expired or invalid signature
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract all claims from JWT token
     */
    public Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extract email (subject) from token
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract userId from token claims
     */
    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    /**
     * Extract token type (access or refresh)
     */
    public String extractTokenType(String token) {
        return extractAllClaims(token).get("type", String.class);
    }

    /**
     * Extract roles from token claims
     */
    public String extractRoles(String token) {
        return extractAllClaims(token).get("roles", String.class);
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Extract userId from Authentication object
     * Used in JWT generation during login
     */
    private String extractUserIdFromAuthentication(Authentication authentication) {
        // TODO: Implement based on your UserDetails implementation
        // For now, return a placeholder - should extract from principal
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            // If your UserDetails has userId, extract it
            return UUID.randomUUID().toString(); // Replace with actual userId extraction
        }
        return UUID.randomUUID().toString();
    }

    /**
     * Get access token expiration time in seconds
     */
    public long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000;
    }

    /**
     * Get refresh token expiration time in seconds
     */
    public long getRefreshTokenExpirationInSeconds() {
        return refreshTokenExpiration / 1000;
    }
}
