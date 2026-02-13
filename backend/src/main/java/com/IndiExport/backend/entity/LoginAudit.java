package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * LoginAudit entity for security logging of all login attempts (successful and failed).
 * Used for detecting suspicious activity, geographic anomalies, and security analysis.
 */
@Entity
@Table(name = "login_audits", indexes = {
        @Index(name = "idx_login_audits_user_id", columnList = "user_id"),
        @Index(name = "idx_login_audits_status", columnList = "status"),
        @Index(name = "idx_login_audits_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // NULL for failed login attempts with invalid email

    @Column(nullable = false, length = 100)
    private String email; // The email used in login attempt

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'SUCCESS'")
    @Builder.Default
    private LoginStatus status = LoginStatus.SUCCESS; // SUCCESS or FAILED

    @Column(length = 200)
    private String failureReason; // e.g., "Invalid password", "User not found", "Account suspended"

    // Client tracking
    @Column(nullable = false, length = 50)
    private String ipAddress;

    @Column(nullable = false, length = 500)
    private String userAgent;

    @Column(length = 50)
    private String country; // Geo-location (from IP)

    @Column(length = 50)
    private String city; // Geo-location (from IP)

    // Timestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum LoginStatus {
        SUCCESS, FAILED
    }
}
