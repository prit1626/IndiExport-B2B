package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.LoginAudit;
import com.IndiExport.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LoginAuditRepository extends JpaRepository<LoginAudit, UUID> {

    /**
     * Find login audits for a specific user
     */
    Page<LoginAudit> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * Find failed login attempts for email in last N hours
     * Used for brute force detection
     */
    @Query("SELECT la FROM LoginAudit la WHERE la.email = :email AND la.status = 'FAILED' AND la.createdAt > :since ORDER BY la.createdAt DESC")
    List<LoginAudit> findFailedLoginAttemptsForEmail(@Param("email") String email, @Param("since") LocalDateTime since);

    /**
     * Find successful logins for user in time range
     * Used for detecting unusual access patterns
     */
    @Query("SELECT la FROM LoginAudit la WHERE la.user.id = :userId AND la.status = 'SUCCESS' AND la.createdAt BETWEEN :from AND :to ORDER BY la.createdAt DESC")
    List<LoginAudit> findSuccessfulLoginsInRange(@Param("userId") UUID userId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Count failed login attempts for email in last hour
     */
    @Query("SELECT COUNT(la) FROM LoginAudit la WHERE la.email = :email AND la.status = 'FAILED' AND la.createdAt > :since")
    long countFailedAttemptsInLastHour(@Param("email") String email, @Param("since") LocalDateTime since);

    /**
     * Check for login from unusual location/IP
     */
    @Query("SELECT la FROM LoginAudit la WHERE la.user.id = :userId AND la.status = 'SUCCESS' AND la.ipAddress = :ipAddress AND la.createdAt >= :since")
    List<LoginAudit> findPreviousLoginsFromIP(@Param("userId") UUID userId, @Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);
}
