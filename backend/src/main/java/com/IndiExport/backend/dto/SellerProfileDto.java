package com.IndiExport.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO container for Seller Profile.
 */
public class SellerProfileDto {

    /**
     * DTO for responding with public/private seller profile data.
     */
    public static class SellerProfileResponse {
        private UUID id;
        private String companyName;
        private String companyLogoUrl;
        private String website;
        private String businessEmail;
        private String businessPhone;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private int averageRatingMilli;
        private Integer totalProducts;
        private Integer activeProducts;
        private long totalSalesPaise;
        private LocalDateTime createdAt;
        
        // Export Compliance
        private String iecNumber;
        private String iecStatus;
        private String gstin;
        private String panNumber;
        private LocalDateTime verificationSubmittedAt;
        
        // Payout Info
        private String payoutMethod;
        private String accountHolderName;
        private String accountNumberMasked;
        private String ifscMasked;
        
        // Plan info
        private String currentPlan;

        public SellerProfileResponse() {}

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
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
        public int getAverageRatingMilli() { return averageRatingMilli; }
        public void setAverageRatingMilli(int averageRatingMilli) { this.averageRatingMilli = averageRatingMilli; }
        public Integer getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }
        public Integer getActiveProducts() { return activeProducts; }
        public void setActiveProducts(Integer activeProducts) { this.activeProducts = activeProducts; }
        public long getTotalSalesPaise() { return totalSalesPaise; }
        public void setTotalSalesPaise(long totalSalesPaise) { this.totalSalesPaise = totalSalesPaise; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public String getIecNumber() { return iecNumber; }
        public void setIecNumber(String iecNumber) { this.iecNumber = iecNumber; }
        public String getIecStatus() { return iecStatus; }
        public void setIecStatus(String iecStatus) { this.iecStatus = iecStatus; }
        public String getGstin() { return gstin; }
        public void setGstin(String gstin) { this.gstin = gstin; }
        public String getPanNumber() { return panNumber; }
        public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
        public LocalDateTime getVerificationSubmittedAt() { return verificationSubmittedAt; }
        public void setVerificationSubmittedAt(LocalDateTime verificationSubmittedAt) { this.verificationSubmittedAt = verificationSubmittedAt; }
        public String getPayoutMethod() { return payoutMethod; }
        public void setPayoutMethod(String payoutMethod) { this.payoutMethod = payoutMethod; }
        public String getAccountHolderName() { return accountHolderName; }
        public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
        public String getAccountNumberMasked() { return accountNumberMasked; }
        public void setAccountNumberMasked(String accountNumberMasked) { this.accountNumberMasked = accountNumberMasked; }
        public String getIfscMasked() { return ifscMasked; }
        public void setIfscMasked(String ifscMasked) { this.ifscMasked = ifscMasked; }
        public String getCurrentPlan() { return currentPlan; }
        public void setCurrentPlan(String currentPlan) { this.currentPlan = currentPlan; }
    }

    /**
     * DTO for updating seller profile.
     */
    public static class UpdateSellerProfileRequest {
        @NotBlank(message = "Company name is required")
        private String companyName;
        private String website;
        @Email(message = "Invalid business email")
        private String businessEmail;
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid business phone number")
        private String businessPhone;
        @NotBlank(message = "Address is required")
        private String address;
        @NotBlank(message = "City is required")
        private String city;
        @NotBlank(message = "State is required")
        private String state;
        @NotBlank(message = "Postal code is required")
        private String postalCode;
        private Set<UUID> categoryIds;

        public UpdateSellerProfileRequest() {}

        // Getters and Setters
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
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
        public Set<UUID> getCategoryIds() { return categoryIds; }
        public void setCategoryIds(Set<UUID> categoryIds) { this.categoryIds = categoryIds; }
    }
}
