package com.IndiExport.backend.service.rfq;

import com.IndiExport.backend.dto.RfqFinalizeResponse;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.*;
import com.IndiExport.backend.repository.*;
import com.IndiExport.backend.service.invoice.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RfqFinalizeService {

    private final RfqRepository rfqRepository;
    private final RfqQuoteRepository rfqQuoteRepository;
    private final OrderRepository orderRepository;
    private final InvoiceService invoiceService;
    private final InvoiceRepository invoiceRepository;
    private final com.IndiExport.backend.repository.ProductRepository productRepository;

    @Transactional
    public RfqFinalizeResponse finalizeRfq(UUID buyerId, UUID rfqId, UUID quoteId) {
        
        // 1. Fetch & Validate RFQ
        RFQ rfq = rfqRepository.findById(rfqId)
                .orElseThrow(() -> new RfqNotFoundException("RFQ not found"));

        if (!rfq.getBuyer().getId().equals(buyerId)) {
            throw new RfqAccessDeniedException("Not authorized to finalize this RFQ");
        }

        if (rfq.getStatus() != RfqStatus.OPEN && rfq.getStatus() != RfqStatus.UNDER_NEGOTIATION) {
            throw new InvalidRfqStateException("RFQ is not in a valid state for finalization");
        }

        // 2. Fetch & Validate Quote
        RfqQuote quote = rfqQuoteRepository.findById(quoteId)
                .orElseThrow(() -> new RfqQuoteNotFoundException("Quote not found"));

        if (!quote.getRfq().getId().equals(rfqId)) {
            throw new IllegalArgumentException("Quote does not belong to this RFQ");
        }

        if (quote.getStatus() != RfqQuoteStatus.ACTIVE) {
            throw new InvalidRfqStateException("Quote is not active");
        }
        
        if (quote.getValidityUntil() != null && quote.getValidityUntil().isBefore(Instant.now())) {
             throw new InvalidRfqStateException("Quote has expired");
        }

        // 3. Mark RFQ as FINALIZED
        rfq.setStatus(RfqStatus.FINALIZED);
        rfq.setAcceptedQuoteId(quote.getId());
        quote.setStatus(RfqQuoteStatus.ACCEPTED);
        
        rfqRepository.save(rfq);
        rfqQuoteRepository.save(quote);

        // 4. Create Order
        Order order = createOrderFromRfq(rfq, quote);
        
        // 5. Update RFQ Status to CONVERTED
        rfq.setStatus(RfqStatus.CONVERTED_TO_ORDER);
        
        rfqRepository.save(rfq);

        // 6. Generate Proforma
        invoiceService.createProformaInvoice(order.getId());
        
        UUID invoiceId = invoiceRepository.findByOrderId(order.getId(), org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .filter(inv -> inv.getType() == InvoiceType.PROFORMA)
                .findFirst()
                .map(Invoice::getId)
                .orElse(null);

        RfqFinalizeResponse response = new RfqFinalizeResponse();
        response.setOrderId(order.getId());
        response.setInvoiceId(invoiceId);
        response.setPaymentRequired(true);
        response.setMessage("RFQ finalized and Proforma Invoice generated");
        return response;
    }
    
    private Order createOrderFromRfq(RFQ rfq, RfqQuote quote) {
         long subtotal = quote.getQuotedPriceInrPaise() * rfq.getQuantity();
         long shipping = quote.getShippingEstimateInrPaise() != null ? quote.getShippingEstimateInrPaise() : 0;
         long totalOrderValue = subtotal + shipping;
         
         // Create a custom Product
         Product customProduct = new Product();
         customProduct.setSeller(quote.getSeller());
         customProduct.setName("Custom Order: " + rfq.getTitle());
         customProduct.setDescription("Generated from RFQ: " + rfq.getDetails());
         customProduct.setSku("RFQ-" + System.currentTimeMillis() + "-" + rfq.getId().toString().substring(0, 4));
         customProduct.setPricePaise(quote.getQuotedPriceInrPaise());
         customProduct.setMinimumOrderQuantity(1);
         customProduct.setQuantityUnit(rfq.getUnit());
         customProduct.setStockQuantity(rfq.getQuantity());
         customProduct.setWeightGrams(0); // Default
         customProduct.setLengthMm(0);
         customProduct.setWidthMm(0);
         customProduct.setHeightMm(0);
         customProduct.setIncoterm(rfq.getIncoterm());
         customProduct.setStatus(Product.ProductStatus.INACTIVE);
         
         customProduct = productRepository.save(customProduct);
         
         // Create Order
         Order order = new Order();
         order.setBuyer(rfq.getBuyer());
         order.setSeller(quote.getSeller());
         order.setRfq(rfq);
         order.setStatus(Order.OrderStatus.PENDING_CONFIRMATION);
         order.setTotalAmountPaise(totalOrderValue);
         order.setShippingMode(rfq.getShippingMode());
         order.setBuyerCountry(rfq.getDestinationCountry());
         order.setShippingAddress(rfq.getDestinationAddressJson() != null ? rfq.getDestinationAddressJson() : "Address from RFQ");
         order.setEstimatedDeliveryDate(java.time.LocalDate.now().plusDays(quote.getLeadTimeDays() != null ? quote.getLeadTimeDays() : 7));
         order.setItems(new ArrayList<>()); // Initialize list
         
         // Create Order Item
         OrderItem item = new OrderItem();
         item.setOrder(order);
         item.setProduct(customProduct);
         item.setProductNameSnapshot(customProduct.getName());
         item.setSkuSnapshot(customProduct.getSku());
         item.setQuantity(rfq.getQuantity());
         item.setUnitPricePaise(quote.getQuotedPriceInrPaise());
         item.setLineTotalPaise(quote.getQuotedPriceInrPaise() * rfq.getQuantity());
         
         order.getItems().add(item);
          
         order = orderRepository.save(order);

          // Create Shipping Snapshot from Quote
          if (quote.getShippingEstimateInrPaise() != null) {
              ShippingQuote shippingQuote = new ShippingQuote();
              shippingQuote.setOrder(order);
              shippingQuote.setMode(rfq.getShippingMode());
              shippingQuote.setDestinationCountry(rfq.getDestinationCountry());
              shippingQuote.setTotalWeightGrams(0);
              shippingQuote.setChargeableWeightGrams(0);
              shippingQuote.setShippingCostPaise(quote.getShippingEstimateInrPaise());
              shippingQuote.setEstimatedDeliveryDaysMin(quote.getLeadTimeDays());
              shippingQuote.setEstimatedDeliveryDaysMax(quote.getLeadTimeDays());
              shippingQuote.setQuoteSource("RFQ_NEGOTIATION");
              
              // Bidirectional set and save
              order.setShippingQuote(shippingQuote);
              orderRepository.save(order);
          }
          
          return order;
    }
}
