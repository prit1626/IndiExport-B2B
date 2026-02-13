package com.IndiExport.backend.service.payment;

import com.IndiExport.backend.dto.PaymentDto;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.*;
import com.IndiExport.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Core payment service — handles intent creation and payment status queries.
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final StripePaymentProvider stripeProvider;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          BuyerProfileRepository buyerProfileRepository,
                          StripePaymentProvider stripeProvider) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.stripeProvider = stripeProvider;
    }

    /**
     * Create a payment intent for an order.
     * Idempotent: if a non-failed payment already exists, returns it.
     */
    @Transactional
    public PaymentDto.CreatePaymentResponse createPaymentIntent(UUID buyerUserId, UUID orderId) {
        // 1. Verify buyer owns the order
        BuyerProfile buyer = buyerProfileRepository.findByUserId(buyerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", buyerUserId.toString()));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId.toString()));

        if (!order.getBuyer().getId().equals(buyer.getId())) {
            throw new UnauthorizedPaymentAccessException();
        }

        // 2. Order must be in PENDING_CONFIRMATION or CONFIRMED status
        if (order.getStatus() != Order.OrderStatus.PENDING_CONFIRMATION
                && order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new InvalidPaymentStateException(
                    "ORDER_" + order.getStatus().name(), "Payment can only be created for pending/confirmed orders");
        }

        // 3. Idempotent: return existing non-failed payment
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId)
                .filter(p -> p.getStatus() != PaymentStatus.FAILED && p.getStatus() != PaymentStatus.REFUNDED);

        if (existingPayment.isPresent()) {
            Payment existing = existingPayment.get();
            log.info("Returning existing payment {} for order {}", existing.getId(), orderId);
            PaymentDto.CreatePaymentResponse response = new PaymentDto.CreatePaymentResponse();
            response.setPaymentId(existing.getId());
            response.setProvider(existing.getProvider());
            response.setProviderPaymentIntentId(existing.getProviderPaymentIntentId());
            response.setClientSecret(existing.getProviderClientSecret());
            response.setAmountMinor(existing.getAmountMinor());
            response.setCurrency(existing.getCurrency());
            response.setStatus(existing.getStatus());
            return response;
        }

        // 4. Resolve amount and currency from OrderCurrencySnapshot
        OrderCurrencySnapshot snapshot = order.getCurrencySnapshot();
        long amountMinor;
        String currency;

        if (snapshot != null && !"INR".equals(snapshot.getBuyerCurrency())) {
            // Use buyer's currency (converted amount from checkout)
            amountMinor = snapshot.getConvertedTotalMinor();
            currency = snapshot.getBuyerCurrency();
        } else {
            // Domestic buyer pays in INR
            amountMinor = order.getTotalAmountPaise();
            currency = "INR";
        }

        // 5. Create Stripe PaymentIntent
        Map<String, String> metadata = Map.of(
                "orderId", orderId.toString(),
                "orderNumber", order.getOrderNumber(),
                "buyerId", buyer.getId().toString()
        );

        Map<String, String> stripeResult = stripeProvider.createPaymentIntent(amountMinor, currency, metadata);

        // 6. Persist payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setBuyerId(buyer.getId());
        payment.setSellerId(order.getSeller().getId());
        payment.setProvider(PaymentProvider.STRIPE);
        payment.setProviderPaymentIntentId(stripeResult.get("paymentIntentId"));
        payment.setProviderClientSecret(stripeResult.get("clientSecret"));
        payment.setAmountMinor(amountMinor);
        payment.setCurrency(currency);
        payment.setAmountInrPaise(order.getTotalAmountPaise());
        payment.setStatus(PaymentStatus.CREATED);

        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} created for order {} via STRIPE ({}{})",
                saved.getId(), orderId, currency, amountMinor);

        PaymentDto.CreatePaymentResponse response = new PaymentDto.CreatePaymentResponse();
        response.setPaymentId(saved.getId());
        response.setProvider(PaymentProvider.STRIPE);
        response.setProviderPaymentIntentId(saved.getProviderPaymentIntentId());
        response.setClientSecret(saved.getProviderClientSecret());
        response.setAmountMinor(amountMinor);
        response.setCurrency(currency);
        response.setStatus(PaymentStatus.CREATED);
        return response;
    }

    /**
     * Get payment status (buyer-facing).
     */
    @Transactional(readOnly = true)
    public PaymentDto.PaymentStatusResponse getPaymentStatus(UUID buyerUserId, UUID orderId) {
        BuyerProfile buyer = buyerProfileRepository.findByUserId(buyerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", buyerUserId.toString()));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId.toString()));

        if (!payment.getBuyerId().equals(buyer.getId())) {
            throw new UnauthorizedPaymentAccessException();
        }

        return mapToStatusResponse(payment);
    }

    /**
     * Get payment status for admin (no ownership check).
     */
    @Transactional(readOnly = true)
    public PaymentDto.AdminPaymentResponse getAdminPaymentDetail(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId.toString()));

        return mapToAdminResponse(payment);
    }

    public PaymentDto.PaymentStatusResponse mapToStatusResponse(Payment p) {
        PaymentDto.PaymentStatusResponse response = new PaymentDto.PaymentStatusResponse();
        response.setPaymentId(p.getId());
        response.setOrderId(p.getOrder().getId());
        response.setProvider(p.getProvider());
        response.setStatus(p.getStatus());
        response.setAmountMinor(p.getAmountMinor());
        response.setCurrency(p.getCurrency());
        response.setAmountInrPaise(p.getAmountInrPaise());
        response.setDisputeLocked(p.isDisputeLocked());
        response.setCreatedAt(p.getCreatedAt());
        response.setCapturedAt(p.getCapturedAt());
        response.setReleasedAt(p.getReleasedAt());
        return response;
    }

    public PaymentDto.AdminPaymentResponse mapToAdminResponse(Payment p) {
        PaymentDto.AdminPaymentResponse response = new PaymentDto.AdminPaymentResponse();
        response.setPaymentId(p.getId());
        response.setOrderId(p.getOrder().getId());
        response.setOrderNumber(p.getOrder().getOrderNumber());
        response.setBuyerId(p.getBuyerId());
        response.setSellerId(p.getSellerId());
        response.setProvider(p.getProvider());
        response.setProviderPaymentIntentId(p.getProviderPaymentIntentId());
        response.setStatus(p.getStatus());
        response.setAmountMinor(p.getAmountMinor());
        response.setCurrency(p.getCurrency());
        response.setAmountInrPaise(p.getAmountInrPaise());
        response.setDisputeLocked(p.isDisputeLocked());
        response.setCreatedAt(p.getCreatedAt());
        response.setCapturedAt(p.getCapturedAt());
        response.setHoldingStartedAt(p.getHoldingStartedAt());
        response.setReleasedAt(p.getReleasedAt());
        response.setRefundedAt(p.getRefundedAt());
        return response;
    }
}
