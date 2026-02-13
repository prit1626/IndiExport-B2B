package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rfq_quotes", indexes = {
        @Index(name = "idx_rfq_quotes_rfq_id", columnList = "rfq_id"),
        @Index(name = "idx_rfq_quotes_seller_id", columnList = "seller_id")
})
public class RfqQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id", nullable = false)
    private RFQ rfq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerProfile seller;

    @Column(nullable = false)
    private long quotedPriceInrPaise;

    @Column
    private Long shippingEstimateInrPaise;

    @Column
    private Integer leadTimeDays;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column
    private Instant validityUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RfqQuoteStatus status = RfqQuoteStatus.ACTIVE;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public RfqQuote() {}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public RFQ getRfq() { return rfq; }
    public void setRfq(RFQ rfq) { this.rfq = rfq; }

    public SellerProfile getSeller() { return seller; }
    public void setSeller(SellerProfile seller) { this.seller = seller; }

    public long getQuotedPriceInrPaise() { return quotedPriceInrPaise; }
    public void setQuotedPriceInrPaise(long quotedPriceInrPaise) { this.quotedPriceInrPaise = quotedPriceInrPaise; }

    public Long getShippingEstimateInrPaise() { return shippingEstimateInrPaise; }
    public void setShippingEstimateInrPaise(Long shippingEstimateInrPaise) { this.shippingEstimateInrPaise = shippingEstimateInrPaise; }

    public Integer getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(Integer leadTimeDays) { this.leadTimeDays = leadTimeDays; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Instant getValidityUntil() { return validityUntil; }
    public void setValidityUntil(Instant validityUntil) { this.validityUntil = validityUntil; }

    public RfqQuoteStatus getStatus() { return status; }
    public void setStatus(RfqQuoteStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
