package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admin_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Builder.Default
    private long advancedSellerPlanPriceInrPaise = 499900; // Default 4999.00 INR

    @Column(nullable = false)
    @Builder.Default
    private int platformCommissionBps = 250; // Default 2.5%

    @Column(nullable = false)
    @Builder.Default
    private int disputeWindowDays = 7; // Default 7 days

    @Column(nullable = false)
    @Builder.Default
    private int autoReleaseDays = 14; // Default 14 days

    @Column(nullable = false)
    @Builder.Default
    private int basicSellerMaxActiveProducts = 5;

    @Column(nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
