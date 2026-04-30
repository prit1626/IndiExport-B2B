package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.Incoterm;
import com.IndiExport.backend.entity.RfqStatus;
import com.IndiExport.backend.entity.ShippingMode;
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
public class SellerRfqListResponse {
    private UUID id;
    private String title;
    private int quantity;
    private String unit;
    private String destinationCountry;
    private ShippingMode shippingMode;
    private Incoterm incoterm;
    private RfqStatus status;
    private Long targetPriceMinor;
    private String targetCurrency;
    private Long targetPriceINRPaise;
    private Instant createdAt;
    private String categoryName;
    private int quoteCount; // Optional: shows competition

    // Manual Getters/Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public ShippingMode getShippingMode() {
        return shippingMode;
    }

    public void setShippingMode(ShippingMode shippingMode) {
        this.shippingMode = shippingMode;
    }

    public Incoterm getIncoterm() {
        return incoterm;
    }

    public void setIncoterm(Incoterm incoterm) {
        this.incoterm = incoterm;
    }

    public RfqStatus getStatus() {
        return status;
    }

    public void setStatus(RfqStatus status) {
        this.status = status;
    }

    public Long getTargetPriceMinor() {
        return targetPriceMinor;
    }

    public void setTargetPriceMinor(Long targetPriceMinor) {
        this.targetPriceMinor = targetPriceMinor;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public Long getTargetPriceINRPaise() {
        return targetPriceINRPaise;
    }

    public void setTargetPriceINRPaise(Long targetPriceINRPaise) {
        this.targetPriceINRPaise = targetPriceINRPaise;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getQuoteCount() {
        return quoteCount;
    }

    public void setQuoteCount(int quoteCount) {
        this.quoteCount = quoteCount;
    }
}
