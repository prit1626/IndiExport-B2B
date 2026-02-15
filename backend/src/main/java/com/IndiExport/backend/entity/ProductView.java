package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * ProductView entity to track product page views.
 * Used for calculating conversion rates (views -> orders).
 */
@Entity
@Table(name = "product_views", indexes = {
        @Index(name = "idx_product_views_product_id", columnList = "product_id"),
        @Index(name = "idx_product_views_viewed_at", columnList = "viewed_at"),
        @Index(name = "idx_product_views_country", columnList = "country")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "buyer_id")
    private UUID buyerId; // Nullable, as guests can also view products

    @Column(length = 2)
    private String country; // ISO-2 country code from IP or profile

    @Column(nullable = false, name = "viewed_at")
    @Builder.Default
    private Instant viewedAt = Instant.now();
}
