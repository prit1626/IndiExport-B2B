package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.CheckoutDto;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.OrderRepository;
import com.IndiExport.backend.service.currency.CurrencyConversionService;
import com.IndiExport.backend.service.currency.CurrencyMetadata;
import com.IndiExport.backend.service.invoice.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final com.IndiExport.backend.repository.SellerProfileRepository sellerProfileRepository;
    private final InvoiceService invoiceService;

    /**
     * Get paginated orders for a buyer
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getBuyerOrders(UUID userId, int page, int size, String status, String sortStr) {
        BuyerProfile buyer = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", userId.toString()));

        // Sort parsing
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sortStr != null && !sortStr.isEmpty()) {
            String[] parts = sortStr.split(",");
            if (parts.length >= 2) {
                sort = Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
            } else {
                sort = Sort.by(parts[0]);
            }
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orderPage;

        if (status != null && !status.isEmpty()) {
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
                orderPage = orderRepository.findByBuyerIdAndStatus(buyer.getId(), orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                 // Invalid status, return empty or all? Returning empty for safety
                 orderPage = Page.empty();
            }
        } else {
            orderPage = orderRepository.findByBuyerId(buyer.getId(), pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("items", orderPage.getContent().stream().map(this::mapToOrderSummary).collect(Collectors.toList()));
        response.put("page", orderPage.getNumber());
        response.put("size", orderPage.getSize());
        response.put("totalItems", orderPage.getTotalElements());
        response.put("totalPages", orderPage.getTotalPages());

        return response;
    }

    /**
     * Get paginated orders for a seller
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSellerOrders(UUID userId, int page, int size, String status, String sortStr) {
        SellerProfile seller = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));

        // Sort parsing
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sortStr != null && !sortStr.isEmpty()) {
            String[] parts = sortStr.split(",");
            if (parts.length >= 2) {
                sort = Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
            } else {
                sort = Sort.by(parts[0]);
            }
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orderPage;

        if (status != null && !status.isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            try {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
                orderPage = orderRepository.findBySellerIdAndStatusOrderByCreatedAtDesc(seller.getId(), orderStatus, pageable);
            } catch (IllegalArgumentException e) {
                 orderPage = Page.empty();
            }
        } else {
            orderPage = orderRepository.findBySellerIdOrderByCreatedAtDesc(seller.getId(), pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("items", orderPage.getContent().stream().map(this::mapToOrderSummary).collect(Collectors.toList()));
        response.put("page", orderPage.getNumber());
        response.put("size", orderPage.getSize());
        response.put("totalItems", orderPage.getTotalElements());
        response.put("totalPages", orderPage.getTotalPages());

        return response;
    }

    /**
     * Get single order details for a buyer
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getBuyerOrderDetails(UUID userId, UUID orderId) {
        BuyerProfile buyer = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", userId.toString()));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));

        if (!order.getBuyer().getId().equals(buyer.getId())) {
             throw new ResourceNotFoundException("Order", orderId.toString()); // Hide unauthorized access
        }

        return mapToOrderDetails(order);
    }

    /**
     * Get single order details for a seller
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSellerOrderDetails(UUID userId, UUID orderId) {
        SellerProfile seller = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));

        if (!order.getSeller().getId().equals(seller.getId())) {
             throw new ResourceNotFoundException("Order", orderId.toString());
        }

        return mapToOrderDetails(order);
    }

    /**
     * Update order status for a seller
     */
    @Transactional
    public Map<String, Object> updateSellerOrderStatus(UUID userId, UUID orderId, String newStatus) {
        SellerProfile seller = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));

        if (!order.getSeller().getId().equals(seller.getId())) {
             throw new ResourceNotFoundException("Order", orderId.toString());
        }

        try {
            Order.OrderStatus status = Order.OrderStatus.valueOf(newStatus);
            order.setStatus(status);
            orderRepository.save(order);
            
            // Generate invoices when order is confirmed delivered
            if (status == Order.OrderStatus.DELIVERED) {
                try {
                    invoiceService.createFinalInvoices(orderId);
                } catch (Exception e) {
                    // Log but don't fail the status update if invoice generation fails
                    System.err.println("Failed to generate final invoices for order " + orderId + ": " + e.getMessage());
                }
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + newStatus);
        }

        return mapToOrderDetails(order);
    }

    /**
     * Get order tracking info
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getBuyerOrderTracking(UUID userId, UUID orderId) {
        BuyerProfile buyer = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", userId.toString()));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));
        
        if (!order.getBuyer().getId().equals(buyer.getId())) {
             throw new ResourceNotFoundException("Order", orderId.toString());
        }

        Map<String, Object> tracking = new HashMap<>();
        tracking.put("orderId", order.getId());
        tracking.put("trackingNumber", order.getTrackingNumber()); 
        tracking.put("courier", order.getShippingCourier()); 
        tracking.put("status", order.getStatus().name());
        tracking.put("events", new ArrayList<>()); 

        return tracking;
    }

    private Map<String, Object> mapToOrderSummary(Order order) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", order.getId());
        summary.put("orderNumber", order.getOrderNumber());
        summary.put("status", order.getStatus());
        summary.put("createdAt", order.getCreatedAt() != null ? order.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant() : null);
        summary.put("shippingMode", order.getShippingMode());
        summary.put("itemCount", order.getItems().size());
        summary.put("shippingCountry", order.getShippingCountry());
        summary.put("buyerName", order.getBuyer().getFullName());
        
        // Totals
        summary.put("grandTotalINRPaise", order.getTotalAmountPaise());
        
        OrderCurrencySnapshot snapshot = order.getCurrencySnapshot();
        if (snapshot != null) {
            summary.put("grandTotalConvertedMinor", snapshot.getConvertedTotalMinor());
            summary.put("currency", snapshot.getBuyerCurrency());
        } else {
             summary.put("currency", "INR");
        }
        
        return summary;
    }

    private Map<String, Object> mapToOrderDetails(Order order) {
        Map<String, Object> details = mapToOrderSummary(order);
        
        // Expanded details
        Map<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("fullName", order.getBuyer().getFullName());
        shippingAddress.put("streetAddress", order.getShippingAddress());
        shippingAddress.put("city", order.getShippingCity());
        shippingAddress.put("state", order.getShippingState());
        shippingAddress.put("postalCode", order.getShippingPostalCode());
        shippingAddress.put("country", order.getShippingCountry());
        shippingAddress.put("phone", order.getBuyer().getPhone());
        shippingAddress.put("email", order.getBuyer().getUser().getEmail());
        details.put("shippingAddress", shippingAddress);
        
        Map<String, Object> buyerInfo = new HashMap<>();
        buyerInfo.put("fullName", order.getBuyer().getFullName());
        buyerInfo.put("email", order.getBuyer().getUser().getEmail());
        buyerInfo.put("phone", order.getBuyer().getPhone());
        details.put("buyer", buyerInfo);
        
        Map<String, Object> shippingInfo = new HashMap<>();
        shippingInfo.put("shippingMode", order.getShippingMode());
        shippingInfo.put("courier", order.getShippingCourier() != null ? order.getShippingCourier() : "");
        shippingInfo.put("trackingNumber", order.getTrackingNumber() != null ? order.getTrackingNumber() : "");
        details.put("shipping", shippingInfo);

        List<Map<String, Object>> items = order.getItems().stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            try {
                itemMap.put("id", item.getId());
                itemMap.put("title", item.getProductNameSnapshot());
                itemMap.put("sku", item.getSkuSnapshot());
                itemMap.put("qty", item.getQuantity());
                
                if (item.getProduct() != null) {
                    itemMap.put("thumbnailUrl", item.getProduct().getThumbnailUrl());
                    itemMap.put("unit", item.getProduct().getUnit());
                } else {
                    itemMap.put("thumbnailUrl", null);
                    itemMap.put("unit", "PCS");
                }
                
                itemMap.put("basePriceINRPaise", item.getUnitPricePaise());
                
                // Conversions if snapshot exists
                OrderCurrencySnapshot currencySnap = order.getCurrencySnapshot();
                if (currencySnap != null) {
                    double rate = (double) currencySnap.getExchangeRateMicros() / 1_000_000;
                    long convertedPrice = (long) (item.getUnitPricePaise() * rate); 
                    itemMap.put("convertedPriceMinor", convertedPrice);
                }
            } catch (Exception e) {
                // Log and provide fallback to avoid breaking the whole list
                itemMap.put("title", item.getProductNameSnapshot() != null ? item.getProductNameSnapshot() : "Unknown Product");
                itemMap.put("qty", item.getQuantity());
            }
            return itemMap;
        }).collect(Collectors.toList());
        details.put("items", items);

        // Snapshot
        OrderCurrencySnapshot snapshot = order.getCurrencySnapshot();
        if(snapshot != null) {
            details.put("currencySnapshot", snapshot);
        }
        
        // Totals breakdown
        Map<String, Object> totals = new HashMap<>();
        long totalAmountPaise = order.getTotalAmountPaise();
        long shippingPaise = (order.getShippingQuote() != null) ? order.getShippingQuote().getShippingCostPaise() : 0L;
        long subtotalPaise = totalAmountPaise - shippingPaise;

        totals.put("grandTotalINRPaise", totalAmountPaise);
        totals.put("subtotalINRPaise", subtotalPaise);
        totals.put("shippingINRPaise", shippingPaise);
        totals.put("taxINRPaise", 0L); // GST/Tax not explicitly stored separately on order yet

        if (snapshot != null) {
            double rate = (double) snapshot.getExchangeRateMicros() / 1_000_000;
            totals.put("grandTotalConvertedMinor", snapshot.getConvertedTotalMinor());
            totals.put("subtotalConvertedMinor", (long) (subtotalPaise * rate));
            totals.put("shippingConvertedMinor", (long) (shippingPaise * rate));
            totals.put("taxConvertedMinor", 0L);
        }
        details.put("totals", totals);

        return details;
    }
}
