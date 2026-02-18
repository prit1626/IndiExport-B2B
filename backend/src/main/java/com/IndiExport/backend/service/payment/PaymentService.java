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
    private final RazorpayPaymentProvider razorpayProvider;

    public PaymentService(PaymentRepository paymentRepository,
                          OrderRepository orderRepository,
                          BuyerProfileRepository buyerProfileRepository,
                          StripePaymentProvider stripeProvider,
                          RazorpayPaymentProvider razorpayProvider) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.buyerProfileRepository = buyerProfileRepository;
        this.stripeProvider = stripeProvider;
        this.razorpayProvider = razorpayProvider;
    }

    /**
     * Create a payment intent for an order.
     * Idempotent: if a non-failed payment already exists, returns it.
     */
    @Transactional
    public PaymentDto.RazorpayOrderResponse createRazorpayOrder(UUID buyerUserId, UUID orderId) {
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
            
            // Reconstruct logic or force new if expired? Assuming return existing.
            // Simplified for Razorpay:
            PaymentDto.RazorpayOrderResponse response = new PaymentDto.RazorpayOrderResponse();
            response.setOrderId(orderId);
            response.setProvider(PaymentProvider.RAZORPAY);

            PaymentDto.RazorpayDetails details = new PaymentDto.RazorpayDetails();
            details.setKey(razorpayProvider.getKeyId());
            details.setRazorpayOrderId(existing.getProviderPaymentIntentId());
            details.setAmountMinor(existing.getAmountMinor());
            details.setCurrency(existing.getCurrency());
            details.setBuyerName(buyer.getFullName());
            details.setBuyerEmail(buyer.getUser().getEmail());
            details.setBuyerPhone(buyer.getPhone());
            // details.setNotes(...) - retrieve from where? Reconstruct or ignore for idempotent check
            
            response.setRazorpay(details);
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

        // 5. Create Razorpay Order
        Map<String, String> notes = Map.of(
                "orderId", orderId.toString(),
                "orderNumber", order.getOrderNumber(),
                "buyerId", buyer.getId().toString()
        );

        com.razorpay.Order razorpayOrder = razorpayProvider.createOrder(amountMinor, currency, notes);
        String rzpOrderId = razorpayOrder.get("id");

        // 6. Persist payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setBuyerId(buyer.getId());
        payment.setSellerId(order.getSeller().getId());
        payment.setProvider(PaymentProvider.RAZORPAY);
        payment.setProviderPaymentIntentId(rzpOrderId); // Store Razorpay Order ID here
        payment.setAmountMinor(amountMinor);
        payment.setCurrency(currency);
        payment.setAmountInrPaise(order.getTotalAmountPaise());
        payment.setStatus(PaymentStatus.CREATED);

        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} created for order {} via RAZORPAY ({}{})",
                saved.getId(), orderId, currency, amountMinor);

        // 7. Check for existing payment to return? (Already handled by step 3)

        // 8. Build Response
        PaymentDto.RazorpayOrderResponse response = new PaymentDto.RazorpayOrderResponse();
        response.setOrderId(orderId);
        response.setProvider(PaymentProvider.RAZORPAY);

        PaymentDto.RazorpayDetails details = new PaymentDto.RazorpayDetails();
        details.setKey(razorpayProvider.getKeyId());
        details.setRazorpayOrderId(rzpOrderId);
        details.setAmountMinor(amountMinor);
        details.setCurrency(currency);
        details.setBuyerName(buyer.getFullName());
        details.setBuyerEmail(buyer.getUser().getEmail());
        details.setBuyerPhone(buyer.getPhone());
        details.setNotes(notes);
        
        response.setRazorpay(details);
        return response;
    }

    /**
     * Verify Razorpay Payment Signature
     */
    @Transactional
    public void verifyRazorpayPayment(UUID buyerUserId, PaymentDto.RazorpayVerifyRequest request) {
        // 1. Verify buyer owns the order (via platformOrderId)
        BuyerProfile buyer = buyerProfileRepository.findByUserId(buyerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", buyerUserId.toString()));
        
        // We find the Payment by orderId (which implies platformOrderId)
        Payment payment = paymentRepository.findByOrderId(request.getPlatformOrderId())
                .orElseThrow(() -> new PaymentNotFoundException(request.getPlatformOrderId().toString()));

        if (!payment.getBuyerId().equals(buyer.getId())) {
             throw new UnauthorizedPaymentAccessException();
        }

        // 2. Retrieve Razorpay Order ID from payment record
        String savedRzpOrderId = payment.getProviderPaymentIntentId();
        if (!savedRzpOrderId.equals(request.getRazorpayOrderId())) {
            throw new InvalidPaymentStateException("MISMATCH", "Razorpay Order ID mismatch");
        }

        // 3. Verify Signature
        boolean isValid = razorpayProvider.verifySignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        if (!isValid) {
            log.warn("Invalid Razorpay Signature for Order {}", request.getPlatformOrderId());
            throw new PaymentGatewayException("RAZORPAY", "Invalid Payment Signature");
        }

        // 4. Update Payment Status
        payment.setStatus(PaymentStatus.PAID);
        payment.setProviderPaymentIntentId(request.getRazorpayPaymentId()); // Optionally store pay_id
        payment.setCapturedAt(Instant.now());
        paymentRepository.save(payment);

        // 5. Update Order Status
        Order order = payment.getOrder();
        order.setStatus(Order.OrderStatus.PAID);
        // paymentService usually shouldn't update Order directly if there's an OrderStateMachine, 
        // but for simplicity here we do it or call an event publisher.
        // Assuming direct update for now as per instructions.
        orderRepository.save(order);
        
        log.info("Order {} marked as PAID via Razorpay", order.getId());
    }

    // Keep existing createPaymentIntent for legacy or switch completely?
    // User asked to "Integrate Razorpay", implying switch. 
    // I will REPLACE the existing createPaymentIntent logic but keep the method signature different if needed 
    // OR create a new method and let controller call it.
    // The previous createPaymentIntent returned PaymentDto.CreatePaymentResponse (Stripe specific).
    // I'll create `createRazorpayOrder` method.


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
