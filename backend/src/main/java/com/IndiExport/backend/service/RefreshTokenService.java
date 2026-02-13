package com.IndiExport.backend.service;

import com.IndiExport.backend.entity.RefreshToken;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * RefreshTokenService manages JWT refresh tokens in database.
 * - Store refresh tokens as SHA-256 hashes for security
 * - Support multiple devices per user
 * - Revoke tokens for logout
 * - Rotate tokens for security
 * - Cleanup expired tokens
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration:604800000}") // 7 days
    private long refreshTokenExpiration;

    private static final int MAX_ACTIVE_TOKENS_PER_USER = 5; // Max devices per user
    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * Create and store a new refresh token for a user with device info
     * Automatically revokes oldest token if user exceeds device limit
     */
    @Transactional
    public RefreshToken createRefreshToken(User user, String tokenValue, String deviceName, String ipAddress, String userAgent) {

        // Check active token limit
        long activeTokenCount = refreshTokenRepository.countActiveTokens(user.getId(), LocalDateTime.now());
        if (activeTokenCount >= MAX_ACTIVE_TOKENS_PER_USER) {
            revokeOldestTokenForUser(user.getId());
        }

        // Hash the token for storage (never store raw tokens in DB)
        String tokenHash = hashToken(tokenValue);

        // Calculate expiration
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);

        // Create and save refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Verify refresh token exists, is not revoked, and not expired
     */
    @Transactional(readOnly = true)
    public boolean verifyRefreshToken(String tokenValue) {
        String tokenHash = hashToken(tokenValue);
        Optional<RefreshToken> token = refreshTokenRepository.findByTokenHash(tokenHash);

        if (token.isEmpty()) {
            return false;
        }

        RefreshToken rt = token.get();
        if (rt.isRevoked()) {
            return false;
        }

        if (rt.isExpired()) {
            return false;
        }

        return true;
    }

    /**
     * Get refresh token by token value (hash lookup)
     */
    @Transactional(readOnly = true)
    public RefreshToken getRefreshToken(String tokenValue) {
        String tokenHash = hashToken(tokenValue);
        return refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", tokenHash));
    }

    /**
     * Revoke a specific refresh token (for logout specific device)
     */
    @Transactional
    public void revokeRefreshToken(String tokenValue) {
        String tokenHash = hashToken(tokenValue);
        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", tokenHash));

        token.revoke();
        refreshTokenRepository.save(token);
    }

    /**
     * Revoke all refresh tokens for a user (logout all devices)
     */
    @Transactional
    public void revokeAllTokensForUser(UUID userId) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUserId(userId);
        tokens.stream()
                .filter(RefreshToken::isActive)
                .forEach(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token);
                });
    }

    /**
     * Get all active refresh tokens for a user (device list)
     */
    @Transactional(readOnly = true)
    public List<RefreshToken> getActiveTokensForUser(UUID userId) {
        return refreshTokenRepository.findActiveTokensByUserId(userId, LocalDateTime.now());
    }

    /**
     * Rotate refresh token (revoke old, issue new)
     * Useful for security - refresh tokens only valid for single use
     */
    @Transactional
    public RefreshToken rotateRefreshToken(String oldTokenValue, String newTokenValue, String deviceName, String ipAddress, String userAgent) {
        RefreshToken oldToken = getRefreshToken(oldTokenValue);
        
        if (oldToken.isRevoked() || oldToken.isExpired()) {
            throw new IllegalArgumentException("Cannot rotate revoked or expired token");
        }

        // Revoke old token
        oldToken.revoke();
        refreshTokenRepository.save(oldToken);

        // Create new token
        return createRefreshToken(oldToken.getUser(), newTokenValue, deviceName, ipAddress, userAgent);
    }

    /**
     * Cleanup expired tokens (periodic job)
     * Should be run by scheduler
     */
    @Transactional
    public void cleanupExpiredTokens() {
        List<RefreshToken> expiredTokens = refreshTokenRepository.findExpiredTokens(LocalDateTime.now());
        refreshTokenRepository.deleteAll(expiredTokens);
    }

    /**
     * Hash token using SHA-256
     * Tokens should never be stored as plaintext in DB
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Revoke oldest active token for user when limit exceeded
     */
    private void revokeOldestTokenForUser(UUID userId) {
        List<RefreshToken> tokens = refreshTokenRepository.findActiveTokensByUserId(userId, LocalDateTime.now());
        if (!tokens.isEmpty()) {
            RefreshToken oldestToken = tokens.get(tokens.size() - 1); // Last in desc order = oldest
            oldestToken.revoke();
            refreshTokenRepository.save(oldestToken);
        }
    }
}
