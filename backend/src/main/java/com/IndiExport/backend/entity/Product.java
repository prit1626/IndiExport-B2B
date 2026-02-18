package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Product entity representing items listed by sellers.
 * All prices are in INR (Indian Rupees).
 * Multi-currency conversion happens at order level.
 * Status determines product visibility (ACTIVE, INACTIVE, DRAFT, BLOCKED).
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_seller_id", columnList = "seller_id"),
        @Index(name = "idx_products_status", columnList = "status"),
        @Index(name = "idx_products_name", columnList = "name"),
        @Index(name = "idx_products_sku", columnList = "sku"),
        @Index(name = "idx_products_created_at", columnList = "created_at")
})
@org.hibernate.annotations.SQLDelete(sql = "UPDATE products SET status = 'DELETED', deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("status != 'DELETED'")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerProfile seller;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 100)
    private String brand;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "SKU is required")
    @Column(nullable = false, length = 100)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'DRAFT'")
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(nullable = false)
    private long pricePaise; // Price in INR Paise (1 INR = 100 Paise)

    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    @Column(nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer minimumOrderQuantity = 1;

    @Column
    private Integer maximumOrderQuantity;

    @NotBlank(message = "Quantity unit is required")
    @Column(nullable = false, length = 50)
    private String quantityUnit; // 'PCS', 'KG', 'TON' etc.

    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    private long weightGrams;

    @Column(nullable = false)
    private int lengthMm;

    @Column(nullable = false)
    private int widthMm;

    @Column(nullable = false)
    private int heightMm;

    @Column(nullable = false, length = 100)
    private String originCountry = "INDIA";

    @Column(nullable = false, columnDefinition = "INT DEFAULT 7")
    private Integer leadTimeDays = 7;

    @Column(length = 20)
    private String hsCode; // Harmonized System Code for export

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Incoterm incoterm;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int averageRatingMilli = 0; // e.g., 4.5 is 4500

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer totalReviews = 0;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer totalOrders = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime deletedAt; // Soft delete support

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductMedia> media = new ArrayList<>();

    public Product() {}

    public enum ProductStatus {
        DRAFT,
        ACTIVE,
        INACTIVE,
        DELETED
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isAvailable() {
        return status == ProductStatus.ACTIVE && stockQuantity > 0;
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public SellerProfile getSeller() { return seller; }
    public void setSeller(SellerProfile seller) { this.seller = seller; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public long getPricePaise() { return pricePaise; }
    public void setPricePaise(long pricePaise) { this.pricePaise = pricePaise; }

    public Integer getMinimumOrderQuantity() { return minimumOrderQuantity; }
    public void setMinimumOrderQuantity(Integer minimumOrderQuantity) { this.minimumOrderQuantity = minimumOrderQuantity; }

    public Integer getMaximumOrderQuantity() { return maximumOrderQuantity; }
    public void setMaximumOrderQuantity(Integer maximumOrderQuantity) { this.maximumOrderQuantity = maximumOrderQuantity; }

    public String getQuantityUnit() { return quantityUnit; }
    public void setQuantityUnit(String quantityUnit) { this.quantityUnit = quantityUnit; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public long getWeightGrams() { return weightGrams; }
    public void setWeightGrams(long weightGrams) { this.weightGrams = weightGrams; }

    public int getLengthMm() { return lengthMm; }
    public void setLengthMm(int lengthMm) { this.lengthMm = lengthMm; }

    public int getWidthMm() { return widthMm; }
    public void setWidthMm(int widthMm) { this.widthMm = widthMm; }

    public int getHeightMm() { return heightMm; }
    public void setHeightMm(int heightMm) { this.heightMm = heightMm; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public Integer getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(Integer leadTimeDays) { this.leadTimeDays = leadTimeDays; }

    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public Incoterm getIncoterm() { return incoterm; }
    public void setIncoterm(Incoterm incoterm) { this.incoterm = incoterm; }

    public int getAverageRatingMilli() { return averageRatingMilli; }
    public void setAverageRatingMilli(int averageRatingMilli) { this.averageRatingMilli = averageRatingMilli; }

    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }

    public Set<Tag> getTags() { return tags; }
    public void setTags(Set<Tag> tags) { this.tags = tags; }

    public List<ProductMedia> getMedia() { return media; }
    public void setMedia(List<ProductMedia> media) { this.media = media; }

    // Helper methods
    public String getThumbnailUrl() {
        return (media != null && !media.isEmpty()) ? media.get(0).getMediaUrl() : null;
    }

    public String getUnit() {
        return quantityUnit;
    }
}
