package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * SellerPlan entity representing the active plan for each seller.
 * BASIC_SELLER: max 5 active products
 * ADVANCED_SELLER: unlimited active products
 * One-to-one relationship with SellerProfile.
 */
@Entity
@Table(name = "seller_plan", indexes = {
        @Index(name = "idx_seller_plan_seller_id", columnList = "seller_id"),
        @Index(name = "idx_seller_plan_active", columnList = "is_active")
})
public class SellerPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, unique = true)
    private SellerProfile seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'BASIC_SELLER'")
    private PlanType planType = PlanType.BASIC_SELLER;

    @Column(nullable = false)
    private Integer maxActiveProducts;

    @Column(precision = 15, scale = 2)
    private java.math.BigDecimal maxMonthlyRevenue; // NULL if unlimited

    @Column(nullable = false)
    private LocalDateTime activeSince = LocalDateTime.now();

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime validUntil;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public SellerPlan() {}

    public enum PlanType {
        BASIC_SELLER,      // Max 5 active products
        ADVANCED_SELLER    // Unlimited active products
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static SellerPlan createBasicPlan(SellerProfile seller) {
        SellerPlan plan = new SellerPlan();
        plan.setSeller(seller);
        plan.setPlanType(PlanType.BASIC_SELLER);
        plan.setMaxActiveProducts(5);
        plan.setIsActive(true);
        return plan;
    }

    public static SellerPlan createAdvancedPlan(SellerProfile seller) {
        SellerPlan plan = new SellerPlan();
        plan.setSeller(seller);
        plan.setPlanType(PlanType.ADVANCED_SELLER);
        plan.setMaxActiveProducts(999999); // Unlimited (very high number)
        plan.setIsActive(true);
        return plan;
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SellerProfile getSeller() { return seller; }
    public void setSeller(SellerProfile seller) { this.seller = seller; }
    public PlanType getPlanType() { return planType; }
    public void setPlanType(PlanType planType) { this.planType = planType; }
    public Integer getMaxActiveProducts() { return maxActiveProducts; }
    public void setMaxActiveProducts(Integer maxActiveProducts) { this.maxActiveProducts = maxActiveProducts; }
    public java.math.BigDecimal getMaxMonthlyRevenue() { return maxMonthlyRevenue; }
    public void setMaxMonthlyRevenue(java.math.BigDecimal maxMonthlyRevenue) { this.maxMonthlyRevenue = maxMonthlyRevenue; }
    public LocalDateTime getActiveSince() { return activeSince; }
    public void setActiveSince(LocalDateTime activeSince) { this.activeSince = activeSince; }
    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
