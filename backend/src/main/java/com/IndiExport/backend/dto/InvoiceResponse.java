package com.IndiExport.backend.dto;

import com.IndiExport.backend.entity.InvoiceSide;
import com.IndiExport.backend.entity.InvoiceStatus;
import com.IndiExport.backend.entity.InvoiceType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class InvoiceResponse {
    private UUID id;
    private UUID orderId;
    private UUID buyerId;
    private UUID sellerId;
    private String invoiceNumber;
    private InvoiceType type;
    private InvoiceSide side;
    private InvoiceStatus status;
    
    // Financials
    private String currencyBase;
    private String currencyBuyer;
    private long exchangeRateMicros;
    private long subtotalInrPaise;
    private long shippingInrPaise;
    private long totalInrPaise;
    private long totalBuyerMinor;
    
    private String pdfUrl;
    private Instant createdAt;
}
