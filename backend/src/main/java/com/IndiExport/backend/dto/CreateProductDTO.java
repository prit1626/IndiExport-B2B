package com.IndiExport.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for creating a new product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductDTO {

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
    private String name;

    @Size(max = 5000, message = "Description must be maximum 5000 characters")
    private String description;

    @NotBlank(message = "SKU is required")
    @Size(min = 1, max = 100, message = "SKU must be between 1 and 100 characters")
    private String sku;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal priceInr;

    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    private Integer minimumOrderQuantity;

    @Min(value = 1, message = "Maximum order quantity must be at least 1")
    private Integer maximumOrderQuantity;

    @NotBlank(message = "Quantity unit is required (KG, PIECE, LITER, METER)")
    private String quantityUnit;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Min(value = 1, message = "Lead time must be at least 1 day")
    private Integer leadTimeDays;

    @DecimalMin(value = "0", message = "GST percentage cannot be negative")
    @DecimalMax(value = "100", message = "GST percentage cannot exceed 100")
    private BigDecimal gstPercentage;

    private String hsCode;

    @NotEmpty(message = "At least one category is required")
    private Set<UUID> categoryIds;

    private Set<UUID> tagIds;
}
