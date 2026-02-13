package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.TrackingDto;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.*;
import com.IndiExport.backend.repository.OrderRepository;
import com.IndiExport.backend.repository.OrderTrackingEventRepository;
import com.IndiExport.backend.repository.OrderTrackingRepository;
import com.IndiExport.backend.repository.SellerProfileRepository;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Order tracking management:
 *   - Seller creates tracking (must own the order)
 *   - Seller adds tracking events
 *   - Buyer/Seller views tracking
 */
@Service
@RequiredArgsConstructor
public class OrderTrackingService {

    private final OrderTrackingRepository trackingRepository;
    private final OrderTrackingEventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final BuyerProfileRepository buyerProfileRepository;

    // ─────────────────────────────────────────────
    // Seller: Create Tracking
    // ─────────────────────────────────────────────

    @Transactional
    public TrackingDto.TrackingResponse createTracking(UUID sellerUserId, UUID orderId,
                                                       TrackingDto.CreateRequest request) {
        SellerProfile seller = sellerProfileRepository.findByUserId(sellerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", sellerUserId.toString()));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));

        // Ownership: seller must own this order
        if (!order.getSeller().getId().equals(seller.getId())) {
            throw new TrackingAccessDeniedException();
        }

        // Prevent duplicate tracking
        if (trackingRepository.existsByOrderId(orderId)) {
            throw new ApiException("TRACKING_ALREADY_EXISTS",
                    "Tracking already exists for order " + orderId, 409);
        }

        OrderTracking tracking = new OrderTracking();
        tracking.setOrder(order);
        tracking.setCourierName(request.getCourierName());
        tracking.setTrackingNumber(request.getTrackingNumber());
        tracking.setTrackingUrl(request.getTrackingUrl());
        tracking.setCurrentStatus(TrackingStatus.SHIPPED);
        tracking.setShippedAt(request.getShippedAt());

        OrderTracking saved = trackingRepository.save(tracking);

        // Auto-create first "SHIPPED" event
        OrderTrackingEvent shippedEvent = new OrderTrackingEvent();
        shippedEvent.setTracking(saved);
        shippedEvent.setStatus(TrackingStatus.SHIPPED);
        shippedEvent.setMessage("Order shipped via " + request.getCourierName());
        shippedEvent.setEventTime(request.getShippedAt());
        eventRepository.save(shippedEvent);

        // Update order status
        order.setStatus(Order.OrderStatus.SHIPPED);
        orderRepository.save(order);

        return mapToResponse(saved, List.of(shippedEvent));
    }

    // ─────────────────────────────────────────────
    // Seller: Add Tracking Event
    // ─────────────────────────────────────────────

    @Transactional
    public TrackingDto.EventResponse addEvent(UUID sellerUserId, UUID orderId,
                                               TrackingDto.EventRequest request) {
        SellerProfile seller = sellerProfileRepository.findByUserId(sellerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", sellerUserId.toString()));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));

        if (!order.getSeller().getId().equals(seller.getId())) {
            throw new TrackingAccessDeniedException();
        }

        OrderTracking tracking = trackingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new TrackingNotFoundException(orderId.toString()));

        OrderTrackingEvent event = new OrderTrackingEvent();
        event.setTracking(tracking);
        event.setStatus(request.getStatus());
        event.setLocation(request.getLocation());
        event.setMessage(request.getMessage());
        event.setEventTime(request.getEventTime());

        OrderTrackingEvent saved = eventRepository.save(event);

        // Update current status on tracking and order
        tracking.setCurrentStatus(request.getStatus());
        if (request.getStatus() == TrackingStatus.DELIVERED) {
            tracking.setDeliveredAt(request.getEventTime());
            order.setStatus(Order.OrderStatus.DELIVERED);
            orderRepository.save(order);
        }
        trackingRepository.save(tracking);

        return mapEventToResponse(saved);
    }

    // ─────────────────────────────────────────────
    // Seller: View Tracking
    // ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public TrackingDto.TrackingResponse getTrackingForSeller(UUID sellerUserId, UUID orderId) {
        SellerProfile seller = sellerProfileRepository.findByUserId(sellerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", sellerUserId.toString()));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));

        if (!order.getSeller().getId().equals(seller.getId())) {
            throw new TrackingAccessDeniedException();
        }

        OrderTracking tracking = trackingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new TrackingNotFoundException(orderId.toString()));

        List<OrderTrackingEvent> events = eventRepository.findByTrackingIdOrderByEventTimeDesc(tracking.getId());
        return mapToResponse(tracking, events);
    }

    // ─────────────────────────────────────────────
    // Buyer: View Tracking
    // ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public TrackingDto.TrackingResponse getTrackingForBuyer(UUID buyerUserId, UUID orderId) {
        BuyerProfile buyer = buyerProfileRepository.findByUserId(buyerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", buyerUserId.toString()));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));

        // Ownership: buyer must own this order
        if (!order.getBuyer().getId().equals(buyer.getId())) {
            throw new TrackingAccessDeniedException();
        }

        OrderTracking tracking = trackingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new TrackingNotFoundException(orderId.toString()));

        List<OrderTrackingEvent> events = eventRepository.findByTrackingIdOrderByEventTimeDesc(tracking.getId());
        return mapToResponse(tracking, events);
    }

    // ─────────────────────────────────────────────
    // Mappers
    // ─────────────────────────────────────────────

    private TrackingDto.TrackingResponse mapToResponse(OrderTracking tracking, List<OrderTrackingEvent> events) {
        TrackingDto.TrackingResponse response = new TrackingDto.TrackingResponse();
        response.setId(tracking.getId());
        response.setOrderId(tracking.getOrder().getId());
        response.setOrderNumber(tracking.getOrder().getOrderNumber());
        response.setCourierName(tracking.getCourierName());
        response.setTrackingNumber(tracking.getTrackingNumber());
        response.setTrackingUrl(tracking.getTrackingUrl());
        response.setCurrentStatus(tracking.getCurrentStatus());
        response.setShippedAt(tracking.getShippedAt());
        response.setDeliveredAt(tracking.getDeliveredAt());
        response.setEvents(events.stream().map(this::mapEventToResponse).collect(Collectors.toList()));
        response.setCreatedAt(tracking.getCreatedAt());
        return response;
    }

    private TrackingDto.EventResponse mapEventToResponse(OrderTrackingEvent event) {
        TrackingDto.EventResponse response = new TrackingDto.EventResponse();
        response.setId(event.getId());
        response.setStatus(event.getStatus());
        response.setLocation(event.getLocation());
        response.setMessage(event.getMessage());
        response.setEventTime(event.getEventTime());
        return response;
    }
}
