package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order entity representing buyer orders.
 * Orders can originate from RFQ negotiation or direct product purchase.
 * Stores immutable snapshot of order details and buyer information at time of creation.
 */
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_buyer_id", columnList = "buyer_id"),
        @Index(name = "idx_orders_seller_id", columnList = "seller_id"),
        @Index(name = "idx_orders_status", columnList = "status"),
        @Index(name = "idx_orders_rfq_id", columnList = "rfq_id"),
        @Index(name = "idx_orders_created_at", columnList = "created_at"),
        @Index(name = "idx_orders_seller_created", columnList = "seller_id, created_at"),
        @Index(name = "idx_orders_buyer_created", columnList = "buyer_id, created_at")
})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private BuyerProfile buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerProfile seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rfq_id")
    private RFQ rfq; // NULL if direct purchase, FK if from RFQ negotiation

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status = OrderStatus.PENDING_CONFIRMATION;

    @Column(nullable = false)
    private long totalAmountPaise; // Total in INR paise (1 INR = 100 paise)

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrderCurrencySnapshot currencySnapshot;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Payment payment;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ShippingQuote shippingQuote;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ShippingMode shippingMode;

    @Column(nullable = false, length = 3)
    private String currencyCode = "INR";

    @Column(nullable = false, length = 2)
    private String buyerCountry; // Snapshot for audit

    @Column(nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(length = 100)
    private String shippingCity;

    @Column(length = 100)
    private String shippingState;

    @Column(length = 20)
    private String shippingPostalCode;

    @Column(length = 2)
    private String shippingCountry;

    @Column(columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(columnDefinition = "DATE")
    private java.time.LocalDate estimatedDeliveryDate;

    @Column(columnDefinition = "DATE")
    private java.time.LocalDate actualDeliveryDate;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public Order() {}

    public enum OrderStatus {
        PENDING_CONFIRMATION,  // Awaiting seller confirmation
        CONFIRMED,             // Seller accepted the order
        PAID,                  // Payment captured (escrow holding)
        SHIPPED,               // Items dispatched
        IN_TRANSIT,            // In transit with logistics
        DELIVERED,             // Delivered to buyer
        COMPLETED,             // Buyer confirmed delivery, payout eligible
        CANCELLED,             // Cancelled by buyer or seller
        RETURNED               // Returned after delivery
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Manual Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public BuyerProfile getBuyer() { return buyer; }
    public void setBuyer(BuyerProfile buyer) { this.buyer = buyer; }

    public SellerProfile getSeller() { return seller; }
    public void setSeller(SellerProfile seller) { this.seller = seller; }

    public RFQ getRfq() { return rfq; }
    public void setRfq(RFQ rfq) { this.rfq = rfq; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public long getTotalAmountPaise() { return totalAmountPaise; }
    public void setTotalAmountPaise(long totalAmountPaise) { this.totalAmountPaise = totalAmountPaise; }

    public OrderCurrencySnapshot getCurrencySnapshot() { return currencySnapshot; }
    public void setCurrencySnapshot(OrderCurrencySnapshot currencySnapshot) { this.currencySnapshot = currencySnapshot; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    public ShippingQuote getShippingQuote() { return shippingQuote; }
    public void setShippingQuote(ShippingQuote shippingQuote) { this.shippingQuote = shippingQuote; }

    public ShippingMode getShippingMode() { return shippingMode; }
    public void setShippingMode(ShippingMode shippingMode) { this.shippingMode = shippingMode; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getBuyerCountry() { return buyerCountry; }
    public void setBuyerCountry(String buyerCountry) { this.buyerCountry = buyerCountry; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String shippingCity) { this.shippingCity = shippingCity; }

    public String getShippingState() { return shippingState; }
    public void setShippingState(String shippingState) { this.shippingState = shippingState; }

    public String getShippingPostalCode() { return shippingPostalCode; }
    public void setShippingPostalCode(String shippingPostalCode) { this.shippingPostalCode = shippingPostalCode; }

    public String getShippingCountry() { return shippingCountry; }
    public void setShippingCountry(String shippingCountry) { this.shippingCountry = shippingCountry; }

    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public java.time.LocalDate getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(java.time.LocalDate estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }

    public java.time.LocalDate getActualDeliveryDate() { return actualDeliveryDate; }
    public void setActualDeliveryDate(java.time.LocalDate actualDeliveryDate) { this.actualDeliveryDate = actualDeliveryDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}
