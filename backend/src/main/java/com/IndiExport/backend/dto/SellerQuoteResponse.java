package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.RfqQuoteStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerQuoteResponse {
    private UUID id;
    private UUID sellerId;
    private String sellerName; // Optional: mask if needed, but usually visible
    private boolean isVerifiedSeller;
    private long quotedPriceInrPaise;
    private Long shippingEstimateInrPaise;
    private Integer leadTimeDays;
    private String notes;
    private Instant validityUntil;
    private RfqQuoteStatus status;
    private Instant createdAt;

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getSellerId() { return sellerId; }
    public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public boolean isVerifiedSeller() { return isVerifiedSeller; }
    public void setVerifiedSeller(boolean verifiedSeller) { isVerifiedSeller = verifiedSeller; }

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
}
