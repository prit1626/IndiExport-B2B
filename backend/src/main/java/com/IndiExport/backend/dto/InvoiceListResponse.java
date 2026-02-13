package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.InvoiceSide;
import com.IndiExport.backend.entity.InvoiceStatus;
import com.IndiExport.backend.entity.InvoiceType;

import java.time.Instant;
import java.util.UUID;

public class InvoiceListResponse {
    private UUID id;
    private UUID orderId;
    private String invoiceNumber;
    private InvoiceType type;
    private InvoiceSide side;
    private InvoiceStatus status;
    private String currencyBuyer;
    private long totalBuyerMinor;
    private long totalInrPaise;
    private Instant createdAt;
    private String pdfUrl;

    public InvoiceListResponse() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public InvoiceType getType() { return type; }
    public void setType(InvoiceType type) { this.type = type; }

    public InvoiceSide getSide() { return side; }
    public void setSide(InvoiceSide side) { this.side = side; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }

    public String getCurrencyBuyer() { return currencyBuyer; }
    public void setCurrencyBuyer(String currencyBuyer) { this.currencyBuyer = currencyBuyer; }

    public long getTotalBuyerMinor() { return totalBuyerMinor; }
    public void setTotalBuyerMinor(long totalBuyerMinor) { this.totalBuyerMinor = totalBuyerMinor; }

    public long getTotalInrPaise() { return totalInrPaise; }
    public void setTotalInrPaise(long totalInrPaise) { this.totalInrPaise = totalInrPaise; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
}
