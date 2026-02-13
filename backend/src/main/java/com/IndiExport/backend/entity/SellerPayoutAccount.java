package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Seller's verified payout account for RazorpayX funds transfer.
 * Must be VERIFIED before any payout can be initiated.
 */
@Entity
@Table(name = "seller_payout_accounts", indexes = {
        @Index(name = "idx_spa_seller_id", columnList = "seller_id")
})
public class SellerPayoutAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, unique = true)
    private SellerProfile seller;

    /** RazorpayX contact ID for this seller. */
    @Column(length = 200)
    private String razorpayContactId;

    /** RazorpayX fund account ID linked to seller's bank. */
    @Column(length = 200)
    private String razorpayFundAccountId;

    /** Masked bank account for display (e.g. "XXXX1234"). */
    @Column(length = 50)
    private String bankAccountMasked;

    /** IFSC code for Indian bank transfer. */
    @Column(length = 11)
    private String ifscCode;

    /** Account holder name. */
    @Column(length = 200)
    private String accountHolderName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.PENDING;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public SellerPayoutAccount() {}

    public enum AccountStatus {
        PENDING,
        VERIFIED,
        REJECTED
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SellerProfile getSeller() { return seller; }
    public void setSeller(SellerProfile seller) { this.seller = seller; }
    public String getRazorpayContactId() { return razorpayContactId; }
    public void setRazorpayContactId(String razorpayContactId) { this.razorpayContactId = razorpayContactId; }
    public String getRazorpayFundAccountId() { return razorpayFundAccountId; }
    public void setRazorpayFundAccountId(String razorpayFundAccountId) { this.razorpayFundAccountId = razorpayFundAccountId; }
    public String getBankAccountMasked() { return bankAccountMasked; }
    public void setBankAccountMasked(String bankAccountMasked) { this.bankAccountMasked = bankAccountMasked; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public String getAccountHolderName() { return accountHolderName; }
    public void setAccountHolderName(String accountHolderName) { this.accountHolderName = accountHolderName; }
    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
