package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * SellerProfile entity storing seller-specific information.
 * Each SELLER role user has exactly one seller profile.
 * Sellers must be from India (country = 'IN').
 */
@Entity
@Table(name = "seller_profile", indexes = {
        @Index(name = "idx_seller_profile_user_id", columnList = "user_id"),
        @Index(name = "idx_seller_profile_active_products", columnList = "active_products")
})
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SellerKyc kyc;

    @NotBlank(message = "Company name is required")
    @Column(nullable = false, length = 255)
    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String companyLogoUrl;

    @Column(columnDefinition = "TEXT")
    private String website;

    @Email(message = "Invalid business email")
    @Column(length = 255)
    private String businessEmail;

    @Column(length = 20)
    private String businessPhone;

    @NotBlank(message = "Address is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;

    @NotBlank(message = "City is required")
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank(message = "State is required")
    @Column(nullable = false, length = 100)
    private String state;

    @NotBlank(message = "Postal code is required")
    @Column(nullable = false, length = 20)
    private String postalCode;

    @Column(nullable = false, length = 2, columnDefinition = "VARCHAR(2) DEFAULT 'IN'")
    private String country = "IN"; // Sellers must be from India

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "seller_export_categories",
            joinColumns = @JoinColumn(name = "seller_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> exportCategories = new HashSet<>();

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int averageRatingMilli = 0; // e.g., 4.5 is 4500

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer totalProducts = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer activeProducts = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer totalOrders = 0;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long totalSalesPaise = 0L;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime deletedAt; // Soft delete support

    @OneToOne(mappedBy = "seller", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private SellerPlan sellerPlan;

    public SellerProfile() {}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isVerified() {
        return kyc != null && kyc.getVerificationStatus() == SellerKyc.VerificationStatus.VERIFIED;
    }

    public static SellerProfileBuilder builder() {
        return new SellerProfileBuilder();
    }

    public static class SellerProfileBuilder {
        private SellerProfile sellerProfile = new SellerProfile();

        public SellerProfileBuilder id(UUID id) { sellerProfile.setId(id); return this; }
        public SellerProfileBuilder user(User user) { sellerProfile.setUser(user); return this; }
        public SellerProfileBuilder companyName(String companyName) { sellerProfile.setCompanyName(companyName); return this; }
        public SellerProfileBuilder companyLogoUrl(String companyLogoUrl) { sellerProfile.setCompanyLogoUrl(companyLogoUrl); return this; }
        public SellerProfileBuilder website(String website) { sellerProfile.setWebsite(website); return this; }
        public SellerProfileBuilder businessEmail(String businessEmail) { sellerProfile.setBusinessEmail(businessEmail); return this; }
        public SellerProfileBuilder businessPhone(String businessPhone) { sellerProfile.setBusinessPhone(businessPhone); return this; }
        public SellerProfileBuilder address(String address) { sellerProfile.setAddress(address); return this; }
        public SellerProfileBuilder city(String city) { sellerProfile.setCity(city); return this; }
        public SellerProfileBuilder state(String state) { sellerProfile.setState(state); return this; }
        public SellerProfileBuilder postalCode(String postalCode) { sellerProfile.setPostalCode(postalCode); return this; }
        public SellerProfileBuilder country(String country) { sellerProfile.setCountry(country); return this; }
        public SellerProfileBuilder totalProducts(int totalProducts) { sellerProfile.setTotalProducts(totalProducts); return this; }
        public SellerProfileBuilder activeProducts(int activeProducts) { sellerProfile.setActiveProducts(activeProducts); return this; }
        public SellerProfileBuilder totalOrders(int totalOrders) { sellerProfile.setTotalOrders(totalOrders); return this; }
        public SellerProfileBuilder totalSalesPaise(long totalSalesPaise) { sellerProfile.setTotalSalesPaise(totalSalesPaise); return this; }
        public SellerProfileBuilder averageRatingMilli(int averageRatingMilli) { sellerProfile.setAverageRatingMilli(averageRatingMilli); return this; }
        public SellerProfile build() { return sellerProfile; }
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SellerKyc getKyc() { return kyc; }
    public void setKyc(SellerKyc kyc) { this.kyc = kyc; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCompanyLogoUrl() { return companyLogoUrl; }
    public void setCompanyLogoUrl(String companyLogoUrl) { this.companyLogoUrl = companyLogoUrl; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getBusinessEmail() { return businessEmail; }
    public void setBusinessEmail(String businessEmail) { this.businessEmail = businessEmail; }

    public String getBusinessPhone() { return businessPhone; }
    public void setBusinessPhone(String businessPhone) { this.businessPhone = businessPhone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Set<Category> getExportCategories() { return exportCategories; }
    public void setExportCategories(Set<Category> exportCategories) { this.exportCategories = exportCategories; }

    public int getAverageRatingMilli() { return averageRatingMilli; }
    public void setAverageRatingMilli(int averageRatingMilli) { this.averageRatingMilli = averageRatingMilli; }

    public Integer getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }

    public Integer getActiveProducts() { return activeProducts; }
    public void setActiveProducts(Integer activeProducts) { this.activeProducts = activeProducts; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public long getTotalSalesPaise() { return totalSalesPaise; }
    public void setTotalSalesPaise(long totalSalesPaise) { this.totalSalesPaise = totalSalesPaise; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public SellerPlan getSellerPlan() { return sellerPlan; }
    public void setSellerPlan(SellerPlan sellerPlan) { this.sellerPlan = sellerPlan; }
}
