package com.IndiExport.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OrderItem entity representing line items in an order.
 * Stores immutable snapshot of product price at order time.
 * All money values are in INR paise (long). NO BigDecimal.
 */
@Entity
@Table(name = "order_items", indexes = {
        @Index(name = "idx_order_items_order_id", columnList = "order_id"),
        @Index(name = "idx_order_items_product_id", columnList = "product_id")
})
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Snapshot of product name at order time (immutable audit). */
    @Column(nullable = false, length = 255)
    private String productNameSnapshot;

    /** Snapshot of product SKU at order time. */
    @Column(nullable = false, length = 100)
    private String skuSnapshot;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private int quantity;

    /** Locked-in unit price in INR paise at order time. */
    @Column(nullable = false)
    private long unitPricePaise;

    /** Discount in basis points. e.g. 1800 = 18.00%. 0 = no discount. */
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int discountBasisPoints = 0;

    /** GST in basis points. e.g. 1800 = 18.00%. */
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int gstBasisPoints = 0;

    /** Final line total in INR paise: qty * unitPrice - discount + gst. */
    @Column(nullable = false)
    private long lineTotalPaise;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    public OrderItem() {}

    /**
     * Calculate and set line total using integer math:
     * subtotal = quantity * unitPricePaise
     * discount = subtotal * discountBasisPoints / 10000
     * afterDiscount = subtotal - discount
     * gst = afterDiscount * gstBasisPoints / 10000
     * lineTotal = afterDiscount + gst
     */
    public void calculateLineTotal() {
        long subtotal = (long) quantity * unitPricePaise;

        long discountAmount = 0;
        if (discountBasisPoints > 0) {
            discountAmount = (subtotal * discountBasisPoints) / 10_000L;
        }
        long afterDiscount = subtotal - discountAmount;

        long gstAmount = 0;
        if (gstBasisPoints > 0) {
            gstAmount = (afterDiscount * gstBasisPoints) / 10_000L;
        }

        this.lineTotalPaise = afterDiscount + gstAmount;
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getProductNameSnapshot() { return productNameSnapshot; }
    public void setProductNameSnapshot(String productNameSnapshot) { this.productNameSnapshot = productNameSnapshot; }

    public String getSkuSnapshot() { return skuSnapshot; }
    public void setSkuSnapshot(String skuSnapshot) { this.skuSnapshot = skuSnapshot; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public long getUnitPricePaise() { return unitPricePaise; }
    public void setUnitPricePaise(long unitPricePaise) { this.unitPricePaise = unitPricePaise; }

    public int getDiscountBasisPoints() { return discountBasisPoints; }
    public void setDiscountBasisPoints(int discountBasisPoints) { this.discountBasisPoints = discountBasisPoints; }

    public int getGstBasisPoints() { return gstBasisPoints; }
    public void setGstBasisPoints(int gstBasisPoints) { this.gstBasisPoints = gstBasisPoints; }

    public long getLineTotalPaise() { return lineTotalPaise; }
    public void setLineTotalPaise(long lineTotalPaise) { this.lineTotalPaise = lineTotalPaise; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
