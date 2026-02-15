package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * RefreshToken entity for storing JWT refresh tokens with device tracking and revocation.
 * Supports multiple devices per user with device name, IP, and user agent.
 * Tokens are stored as hashes in DB for security.
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
        @Index(name = "idx_refresh_tokens_token_hash", columnList = "token_hash", unique = true),
        @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at"),
        @Index(name = "idx_refresh_tokens_revoked_at", columnList = "revoked_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Token hash is required")
    @Column(nullable = false, unique = true, length = 255)
    private String tokenHash; // SHA-256 hash of the actual token (for security)

    @Column(nullable = false)
    private LocalDateTime expiresAt; // Token expiration time

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime revokedAt; // NULL if active, timestamp if revoked (logout)

    // Device tracking for multiple device support
    @Column(length = 100)
    private String deviceName; // e.g., "Chrome on Windows", "Safari on iPhone"

    @Column(length = 50)
    private String ipAddress; // Client IP for security audit

    @Column(length = 500)
    private String userAgent; // Browser/Client user agent string

    // Audit
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Helper methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public boolean isActive() {
        return !isExpired() && !isRevoked();
    }

    public void revoke() {
        this.revokedAt = LocalDateTime.now();
    }
}
