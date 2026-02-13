package com.IndiExport.backend.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerQuoteCreateRequest {

    @Min(value = 0, message = "Price must be positive")
    private long quotedPriceInrPaise;

    private Long shippingEstimateInrPaise;

    @Min(value = 1, message = "Lead time must be at least 1 day")
    private Integer leadTimeDays;

    private String notes;

    @NotNull(message = "Validity date is required")
    @Future(message = "Validity must be in the future")
    private Instant validityUntil;

    // Manual Getters/Setters
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
}
