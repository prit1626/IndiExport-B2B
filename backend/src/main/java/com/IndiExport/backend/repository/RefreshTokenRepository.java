package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.RefreshToken;
import com.IndiExport.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Find refresh token by token hash (unique)
     * Used during token validation and refresh
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Find all active (non-revoked, non-expired) tokens for a user
     * Used for device management
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user.id = :userId AND rt.revokedAt IS NULL AND rt.expiresAt > :now ORDER BY rt.createdAt DESC")
    List<RefreshToken> findActiveTokensByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    /**
     * Find all refresh tokens for a user (including revoked and expired)
     * Used for logout all devices
     */
    List<RefreshToken> findByUserId(UUID userId);

    /**
     * Find expired tokens for cleanup job
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.expiresAt < :cutoffDate AND rt.revokedAt IS NULL")
    List<RefreshToken> findExpiredTokens(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Count active refresh tokens for a user
     * Useful for device limit enforcement
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user.id = :userId AND rt.revokedAt IS NULL AND rt.expiresAt > :now")
    long countActiveTokens(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    /**
     * Check if a specific token is revoked
     */
    @Query("SELECT CASE WHEN rt.revokedAt IS NOT NULL THEN true ELSE false END FROM RefreshToken rt WHERE rt.tokenHash = :tokenHash")
    boolean isTokenRevoked(@Param("tokenHash") String tokenHash);
}
