package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * SellerKyc entity storing verification documents and status.
 * Linked 1:1 with SellerProfile.
 */
@Entity
@Table(name = "seller_kyc", indexes = {
        @Index(name = "idx_seller_kyc_seller_id", columnList = "seller_id"),
        @Index(name = "idx_seller_kyc_status", columnList = "verification_status")
})
public class SellerKyc {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, unique = true)
    private SellerProfile seller;

    @Column(length = 50)
    private String iecNumber;

    @Column(columnDefinition = "TEXT")
    private String iecDocumentUrl;

    @Column(length = 20)
    private String panNumber;

    @Column(columnDefinition = "TEXT")
    private String panDocumentUrl;

    @Column(length = 50)
    private String gstinNumber;

    @Column(columnDefinition = "TEXT")
    private String gstinDocumentUrl;

    @Column(length = 100)
    private String bankAccountHolderName;

    @Column(length = 50)
    private String bankAccountNumberMasked; // Last 4 digits

    @Column(length = 100)
    private String payoutProviderReferenceId; // Stripe/Razorpay/etc reference

    @Column(length = 20)
    private String bankIfscCode;

    @Column(length = 100)
    private String bankName;

    @Column(length = 100)
    private String bankBranch;

    @Column(length = 50)
    private String payoutMethodPreference; // Stripe, Razorpay, Wise, Payoneer

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VerificationStatus verificationStatus = VerificationStatus.NOT_VERIFIED;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public SellerKyc() {}

    public enum VerificationStatus {
        NOT_VERIFIED,
        PENDING,
        VERIFIED,
        REJECTED
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public static SellerKycBuilder builder() {
        return new SellerKycBuilder();
    }

    public static class SellerKycBuilder {
        private SellerKyc sellerKyc = new SellerKyc();

        public SellerKycBuilder seller(SellerProfile seller) { sellerKyc.setSeller(seller); return this; }
        public SellerKycBuilder iecNumber(String iecNumber) { sellerKyc.setIecNumber(iecNumber); return this; }
        public SellerKycBuilder panNumber(String panNumber) { sellerKyc.setPanNumber(panNumber); return this; }
        public SellerKycBuilder bankAccountNumberMasked(String bankAccountNumberMasked) { sellerKyc.setBankAccountNumberMasked(bankAccountNumberMasked); return this; }
        public SellerKycBuilder bankIfscCode(String bankIfscCode) { sellerKyc.setBankIfscCode(bankIfscCode); return this; }
        public SellerKycBuilder bankAccountHolderName(String bankAccountHolderName) { sellerKyc.setBankAccountHolderName(bankAccountHolderName); return this; }
        public SellerKycBuilder verificationStatus(VerificationStatus verificationStatus) { sellerKyc.setVerificationStatus(verificationStatus); return this; }
        public SellerKycBuilder submittedAt(LocalDateTime submittedAt) { sellerKyc.setSubmittedAt(submittedAt); return this; }
        public SellerKyc build() { return sellerKyc; }
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SellerProfile getSeller() { return seller; }
    public void setSeller(SellerProfile seller) { this.seller = seller; }
    public String getIecNumber() { return iecNumber; }
    public void setIecNumber(String iecNumber) { this.iecNumber = iecNumber; }
    public String getIecDocumentUrl() { return iecDocumentUrl; }
    public void setIecDocumentUrl(String iecDocumentUrl) { this.iecDocumentUrl = iecDocumentUrl; }
    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
    public String getPanDocumentUrl() { return panDocumentUrl; }
    public void setPanDocumentUrl(String panDocumentUrl) { this.panDocumentUrl = panDocumentUrl; }
    public String getGstinNumber() { return gstinNumber; }
    public void setGstinNumber(String gstinNumber) { this.gstinNumber = gstinNumber; }
    public String getGstinDocumentUrl() { return gstinDocumentUrl; }
    public void setGstinDocumentUrl(String gstinDocumentUrl) { this.gstinDocumentUrl = gstinDocumentUrl; }
    public String getBankAccountHolderName() { return bankAccountHolderName; }
    public void setBankAccountHolderName(String bankAccountHolderName) { this.bankAccountHolderName = bankAccountHolderName; }
    public String getBankAccountNumberMasked() { return bankAccountNumberMasked; }
    public void setBankAccountNumberMasked(String bankAccountNumberMasked) { this.bankAccountNumberMasked = bankAccountNumberMasked; }
    public String getPayoutProviderReferenceId() { return payoutProviderReferenceId; }
    public void setPayoutProviderReferenceId(String payoutProviderReferenceId) { this.payoutProviderReferenceId = payoutProviderReferenceId; }
    public String getBankIfscCode() { return bankIfscCode; }
    public void setBankIfscCode(String bankIfscCode) { this.bankIfscCode = bankIfscCode; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getBankBranch() { return bankBranch; }
    public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }
    public String getPayoutMethodPreference() { return payoutMethodPreference; }
    public void setPayoutMethodPreference(String payoutMethodPreference) { this.payoutMethodPreference = payoutMethodPreference; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    public User getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(User verifiedBy) { this.verifiedBy = verifiedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
