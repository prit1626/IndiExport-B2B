package com.IndiExport.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Invoice entity representing generated PDF documents.
 * Stores immutable financial snapshots and reference to the storage URL.
 */
@Entity
@Table(name = "invoices", indexes = {
        @Index(name = "idx_invoices_order_id", columnList = "order_id"),
        @Index(name = "idx_invoices_buyer_id", columnList = "buyer_id"),
        @Index(name = "idx_invoices_seller_id", columnList = "seller_id"),
        @Index(name = "idx_invoices_number", columnList = "invoice_number")
})
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID buyerId;

    @Column(nullable = false)
    private UUID sellerId;

    @Column(nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceSide side;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceStatus status;

    /* ── Financial Snapshots ── */

    @Column(nullable = false, length = 3)
    private String currencyBase = "INR";

    @Column(nullable = false, length = 3)
    private String currencyBuyer;

    @Column(nullable = false)
    private long exchangeRateMicros; // Exchange rate * 1,000,000

    @Column(nullable = false)
    private long subtotalInrPaise;

    @Column(nullable = false)
    private long shippingInrPaise;

    @Column(nullable = false)
    private long totalInrPaise;

    @Column(nullable = false)
    private long totalBuyerMinor; // Total in buyer's currency minor units

    /* ── Document Details ── */

    @Column(columnDefinition = "TEXT")
    private String pdfUrl;

    @Column(length = 50)
    private String iecNumber;

    @Column(length = 50)
    private String gstinNumber;

    @Column(length = 20)
    private String incoterm;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Invoice() {}

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public UUID getBuyerId() { return buyerId; }
    public void setBuyerId(UUID buyerId) { this.buyerId = buyerId; }

    public UUID getSellerId() { return sellerId; }
    public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public InvoiceType getType() { return type; }
    public void setType(InvoiceType type) { this.type = type; }

    public InvoiceSide getSide() { return side; }
    public void setSide(InvoiceSide side) { this.side = side; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public String getCurrencyBase() { return currencyBase; }
    public void setCurrencyBase(String currencyBase) { this.currencyBase = currencyBase; }

    public String getCurrencyBuyer() { return currencyBuyer; }
    public void setCurrencyBuyer(String currencyBuyer) { this.currencyBuyer = currencyBuyer; }

    public long getExchangeRateMicros() { return exchangeRateMicros; }
    public void setExchangeRateMicros(long exchangeRateMicros) { this.exchangeRateMicros = exchangeRateMicros; }

    public long getSubtotalInrPaise() { return subtotalInrPaise; }
    public void setSubtotalInrPaise(long subtotalInrPaise) { this.subtotalInrPaise = subtotalInrPaise; }

    public long getShippingInrPaise() { return shippingInrPaise; }
    public void setShippingInrPaise(long shippingInrPaise) { this.shippingInrPaise = shippingInrPaise; }

    public long getTotalInrPaise() { return totalInrPaise; }
    public void setTotalInrPaise(long totalInrPaise) { this.totalInrPaise = totalInrPaise; }

    public long getTotalBuyerMinor() { return totalBuyerMinor; }
    public void setTotalBuyerMinor(long totalBuyerMinor) { this.totalBuyerMinor = totalBuyerMinor; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public String getIecNumber() { return iecNumber; }
    public void setIecNumber(String iecNumber) { this.iecNumber = iecNumber; }

    public String getGstinNumber() { return gstinNumber; }
    public void setGstinNumber(String gstinNumber) { this.gstinNumber = gstinNumber; }

    public String getIncoterm() { return incoterm; }
    public void setIncoterm(String incoterm) { this.incoterm = incoterm; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
