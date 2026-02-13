package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.Incoterm;
import com.IndiExport.backend.entity.ShippingMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyerRfqCreateRequest {

    private UUID categoryId;

    @NotBlank(message = "Title is required")
    private String title;

    private String details;

    @Min(value = 1, message = "Quantity must be greater than 0")
    private int quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotBlank(message = "Destination country is required")
    private String destinationCountry; // ISO-2

    private String destinationAddressJson;

    private ShippingMode shippingMode;

    @NotNull(message = "Incoterm is required")
    private Incoterm incoterm;

    @Min(value = 0, message = "Target price cannot be negative")
    private Long targetPriceMinor;

    private String targetCurrency;

    private List<String> mediaUrls;

    // Manual Getters/Setters
    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

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

    public String getDestinationAddressJson() { return destinationAddressJson; }
    public void setDestinationAddressJson(String destinationAddressJson) { this.destinationAddressJson = destinationAddressJson; }

    public ShippingMode getShippingMode() { return shippingMode; }
    public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }

    public Incoterm getIncoterm() { return incoterm; }
    public void setIncoterm(Incoterm incoterm) { this.incoterm = incoterm; }

    public Long getTargetPriceMinor() { return targetPriceMinor; }
    public void setTargetPriceMinor(Long targetPriceMinor) { this.targetPriceMinor = targetPriceMinor; }

    public String getTargetCurrency() { return targetCurrency; }
    public void setTargetCurrency(String targetCurrency) { this.targetCurrency = targetCurrency; }

    public List<String> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls; }
}
