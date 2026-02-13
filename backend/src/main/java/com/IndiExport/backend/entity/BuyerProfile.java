package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * BuyerProfile entity storing buyer-specific information.
 * Each BUYER role user has exactly one buyer profile.
 */
@Entity
@Table(name = "buyer_profile", indexes = {
        @Index(name = "idx_buyer_profile_user_id", columnList = "user_id"),
        @Index(name = "idx_buyer_profile_country", columnList = "country")
})
public class BuyerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank(message = "Country code is required")
    @Column(nullable = false, length = 2)
    private String country; // ISO-2 country code (e.g., 'IN', 'US', 'DE')

    @Column(length = 255)
    private String companyName;

    @Column(length = 100)
    private String businessRegistrationNumber;

    @Column(length = 50)
    private String buyerType; // 'INDIVIDUAL', 'CORPORATE', 'GOVERNMENT'

    @Column(length = 3, columnDefinition = "VARCHAR(3) DEFAULT 'INR'")
    private String preferredCurrency = "INR";

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 20)
    private String postalCode;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public BuyerProfile() {}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static BuyerProfileBuilder builder() {
        return new BuyerProfileBuilder();
    }

    public static class BuyerProfileBuilder {
        private BuyerProfile buyerProfile = new BuyerProfile();

        public BuyerProfileBuilder id(UUID id) { buyerProfile.setId(id); return this; }
        public BuyerProfileBuilder user(User user) { buyerProfile.setUser(user); return this; }
        public BuyerProfileBuilder country(String country) { buyerProfile.setCountry(country); return this; }
        public BuyerProfileBuilder companyName(String companyName) { buyerProfile.setCompanyName(companyName); return this; }
        public BuyerProfileBuilder businessRegistrationNumber(String businessRegistrationNumber) { buyerProfile.setBusinessRegistrationNumber(businessRegistrationNumber); return this; }
        public BuyerProfileBuilder buyerType(String buyerType) { buyerProfile.setBuyerType(buyerType); return this; }
        public BuyerProfileBuilder preferredCurrency(String preferredCurrency) { buyerProfile.setPreferredCurrency(preferredCurrency); return this; }
        public BuyerProfileBuilder address(String address) { buyerProfile.setAddress(address); return this; }
        public BuyerProfileBuilder city(String city) { buyerProfile.setCity(city); return this; }
        public BuyerProfileBuilder state(String state) { buyerProfile.setState(state); return this; }
        public BuyerProfileBuilder postalCode(String postalCode) { buyerProfile.setPostalCode(postalCode); return this; }
        public BuyerProfile build() { return buyerProfile; }
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getBusinessRegistrationNumber() { return businessRegistrationNumber; }
    public void setBusinessRegistrationNumber(String businessRegistrationNumber) { this.businessRegistrationNumber = businessRegistrationNumber; }

    public String getBuyerType() { return buyerType; }
    public void setBuyerType(String buyerType) { this.buyerType = buyerType; }

    public String getPreferredCurrency() { return preferredCurrency; }
    public void setPreferredCurrency(String preferredCurrency) { this.preferredCurrency = preferredCurrency; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
