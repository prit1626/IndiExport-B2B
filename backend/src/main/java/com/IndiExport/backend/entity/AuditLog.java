package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * AuditLog entity for tracking all user actions in the system.
 * Stores before/after state for critical operations (entity changes).
 * Used for compliance, debugging, and history tracking.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_logs_user_id", columnList = "user_id"),
        @Index(name = "idx_audit_logs_entity_type", columnList = "entity_type"),
        @Index(name = "idx_audit_logs_action", columnList = "action"),
        @Index(name = "idx_audit_logs_created_at", columnList = "created_at")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // NULL if system-initiated action

    @Column(nullable = false, length = 50)
    private String entityType; // 'ORDER', 'PRODUCT', 'PAYMENT', 'DISPUTE'

    @Column(nullable = false)
    private UUID entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuditAction action; // CREATE, UPDATE, DELETE, SOFT_DELETE

    @Column(columnDefinition = "JSONB")
    private String beforeState; // Previous state (for UPDATE/DELETE)

    @Column(columnDefinition = "JSONB")
    private String afterState; // New state (for CREATE/UPDATE)

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AuditLog() {}

    public enum AuditAction {
        CREATE,
        UPDATE,
        DELETE,
        SOFT_DELETE,
        VIEW,
        LOGIN,
        LOGOUT
    }

    public static AuditLogBuilder builder() {
        return new AuditLogBuilder();
    }

    public static class AuditLogBuilder {
        private AuditLog auditLog = new AuditLog();

        public AuditLogBuilder user(User user) { auditLog.setUser(user); return this; }
        public AuditLogBuilder entityType(String entityType) { auditLog.setEntityType(entityType); return this; }
        public AuditLogBuilder entityId(UUID entityId) { auditLog.setEntityId(entityId); return this; }
        public AuditLogBuilder action(AuditAction action) { auditLog.setAction(action); return this; }
        public AuditLogBuilder beforeState(String beforeState) { auditLog.setBeforeState(beforeState); return this; }
        public AuditLogBuilder afterState(String afterState) { auditLog.setAfterState(afterState); return this; }
        public AuditLogBuilder description(String description) { auditLog.setDescription(description); return this; }
        public AuditLogBuilder ipAddress(String ipAddress) { auditLog.setIpAddress(ipAddress); return this; }
        public AuditLogBuilder createdAt(LocalDateTime createdAt) { auditLog.setCreatedAt(createdAt); return this; }
        public AuditLog build() { return auditLog; }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public UUID getEntityId() { return entityId; }
    public void setEntityId(UUID entityId) { this.entityId = entityId; }
    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }
    public String getBeforeState() { return beforeState; }
    public void setBeforeState(String beforeState) { this.beforeState = beforeState; }
    public String getAfterState() { return afterState; }
    public void setAfterState(String afterState) { this.afterState = afterState; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
