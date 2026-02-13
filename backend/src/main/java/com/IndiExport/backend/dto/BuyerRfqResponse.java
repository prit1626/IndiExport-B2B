package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.Incoterm;
import com.IndiExport.backend.entity.RfqStatus;
import com.IndiExport.backend.entity.ShippingMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerRfqResponse {

    private UUID id;
    private String title;
    private String details;
    private int quantity;
    private String unit;
    private String destinationCountry;
    private ShippingMode shippingMode;
    private Incoterm incoterm;
    private Long targetPriceMinor;
    private String targetCurrency;
    private RfqStatus status;
    private Instant createdAt;
    
    private List<RfqMediaResponse> media;
    private int quoteCount;
    private List<SellerQuoteResponse> quotes; // Only visible if desired, or separate endpoint

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDestinationCountry() { return destinationCountry; }
    public void setDestinationCountry(String destinationCountry) { this.destinationCountry = destinationCountry; }

    public ShippingMode getShippingMode() { return shippingMode; }
    public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }

    public Incoterm getIncoterm() { return incoterm; }
    public void setIncoterm(Incoterm incoterm) { this.incoterm = incoterm; }

    public Long getTargetPriceMinor() { return targetPriceMinor; }
    public void setTargetPriceMinor(Long targetPriceMinor) { this.targetPriceMinor = targetPriceMinor; }

    public String getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(String targetCurrency) { this.targetCurrency = targetCurrency; }

    public RfqStatus getStatus() { return status; }
    public void setStatus(RfqStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public List<RfqMediaResponse> getMedia() { return media; }
    public void setMedia(List<RfqMediaResponse> media) { this.media = media; }
    
    public int getQuoteCount() { return quoteCount; }
    public void setQuoteCount(int quoteCount) { this.quoteCount = quoteCount; }

    public List<SellerQuoteResponse> getQuotes() { return quotes; }
    public void setQuotes(List<SellerQuoteResponse> quotes) { this.quotes = quotes; }
}
