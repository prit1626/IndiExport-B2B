package com.IndiExport.backend.service.payment;

import com.IndiExport.backend.dto.PaymentRequest;
import com.IndiExport.backend.dto.PaymentResponse;
import com.IndiExport.backend.dto.PaymentVerificationRequest;
import com.IndiExport.backend.dto.RazorpayOrderResponse;
import com.IndiExport.backend.entity.Order;
import com.IndiExport.backend.entity.Payment;
import com.IndiExport.backend.entity.PaymentStatus;
import com.IndiExport.backend.exception.*;
import com.IndiExport.backend.repository.OrderRepository;
import com.IndiExport.backend.repository.PaymentRepository;
import com.IndiExport.backend.service.payment.provider.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentProvider paymentProvider;

    /**
     * Create a payment for an order.
     */
    @Transactional
    public RazorpayOrderResponse createPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.getOrderId().toString()));

        // Check if order is in valid state for payment
        if (order.getStatus() != Order.OrderStatus.PENDING_CONFIRMATION && 
            order.getStatus() != Order.OrderStatus.CONFIRMED) {
            throw new BusinessRuleViolationException("Order is not in a payable state: " + order.getStatus());
        }

        log.info("Creating payment for order: {}", order.getOrderNumber());
        
        // Reuse existing payment if it's already created but not paid/failed
        java.util.Optional<Payment> existingPayment = paymentRepository.findFirstByOrderIdAndStatusNotOrderByCreatedAtDesc(order.getId(), PaymentStatus.FAILED);
        if (existingPayment.isPresent() && existingPayment.get().getStatus() == PaymentStatus.CREATED) {
            log.info("Reusing existing CREATED payment for order: {}", order.getOrderNumber());
            // We might need to update the provider intent if it changed, but usually we just return the provider response
            return paymentProvider.createPayment(order);
        }

        RazorpayOrderResponse response = paymentProvider.createPayment(order);
        PaymentResponse razorpay = response.getRazorpay();

        // Save payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setBuyerId(order.getBuyer().getId());
        payment.setSellerId(order.getSeller().getId());
        payment.setProvider(com.IndiExport.backend.entity.PaymentProvider.RAZORPAY);
        payment.setProviderPaymentIntentId(razorpay.getRazorpayOrderId());
        payment.setAmountMinor(razorpay.getAmountMinor());
        payment.setCurrency(razorpay.getCurrency());
        payment.setAmountInrPaise(order.getTotalAmountPaise());
        payment.setStatus(PaymentStatus.CREATED);
        
        paymentRepository.save(payment);

        return response;
    }

    /**
     * Verify payment and update order status.
     */
    @Transactional
    public void verifyPayment(PaymentVerificationRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.getOrderId().toString()));

        Payment payment = paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(order.getId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + order.getId()));

        boolean isValid = paymentProvider.verifyPayment(request);

        if (!isValid) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new InvalidSignatureException("Invalid payment signature");
        }

        log.info("Payment verified for order: {}", order.getOrderNumber());

        // Update Payment status
        payment.setStatus(PaymentStatus.PAID);
        payment.setProviderPaymentIntentId(request.getRazorpayPaymentId()); // Store actual payment id
        payment.setCapturedAt(Instant.now());
        paymentRepository.save(payment);

        // Update Order status
        order.setStatus(Order.OrderStatus.PAID);
        orderRepository.save(order);
        
        // Update to READY_TO_SHIP as requested
        order.setStatus(Order.OrderStatus.READY_TO_SHIP);
        orderRepository.save(order);

        log.info("Order {} updated to PAID and READY_TO_SHIP", order.getOrderNumber());
    }
    @Transactional(readOnly = true)
    public com.IndiExport.backend.entity.PaymentStatus getPaymentStatus(UUID orderId) {
        return paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(orderId)
                .map(com.IndiExport.backend.entity.Payment::getStatus)
                .orElse(null);
    }
}
