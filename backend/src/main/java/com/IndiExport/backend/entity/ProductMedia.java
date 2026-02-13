package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ProductMedia entity storing product images, videos, and documents.
 * Multiple media files can be attached to a single product.
 */
@Entity
@Table(name = "product_media", indexes = {
        @Index(name = "idx_product_media_product_id", columnList = "product_id"),
        @Index(name = "idx_product_media_media_type", columnList = "media_type")
})
public class ProductMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MediaType mediaType; // 'IMAGE', 'VIDEO', 'DOCUMENT'

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer displayOrder = 0;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    public ProductMedia() {}

    public enum MediaType {
        IMAGE,
        VIDEO,
        DOCUMENT
    }

    public static ProductMediaBuilder builder() {
        return new ProductMediaBuilder();
    }

    public static class ProductMediaBuilder {
        private ProductMedia productMedia = new ProductMedia();

        public ProductMediaBuilder product(Product product) { productMedia.setProduct(product); return this; }
        public ProductMediaBuilder mediaUrl(String mediaUrl) { productMedia.setMediaUrl(mediaUrl); return this; }
        public ProductMediaBuilder mediaType(MediaType mediaType) { productMedia.setMediaType(mediaType); return this; }
        public ProductMediaBuilder displayOrder(Integer displayOrder) { productMedia.setDisplayOrder(displayOrder); return this; }
        public ProductMediaBuilder uploadedBy(User uploadedBy) { productMedia.setUploadedBy(uploadedBy); return this; }
        public ProductMedia build() { return productMedia; }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
}
