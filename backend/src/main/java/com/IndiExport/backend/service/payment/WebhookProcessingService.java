package com.IndiExport.backend.service.payment;

import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.repository.OrderRepository;
import com.IndiExport.backend.repository.PaymentRepository;
import com.IndiExport.backend.repository.PaymentWebhookEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Processes webhook events from payment providers.
 *
 * Key guarantees:
 *   - Idempotent: duplicate events are detected and ignored
 *   - State transitions are validated via PaymentStateMachineService
 *   - Raw payloads are stored for audit in payment_webhook_events
 *   - Payment is locked (PESSIMISTIC_WRITE) during processing
 */
@Service
public class WebhookProcessingService {

    private static final Logger log = LoggerFactory.getLogger(WebhookProcessingService.class);

    private final PaymentWebhookEventRepository webhookEventRepository;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentStateMachineService stateMachine;
    private final ObjectMapper objectMapper;

    public WebhookProcessingService(PaymentWebhookEventRepository webhookEventRepository,
                                    PaymentRepository paymentRepository,
                                    OrderRepository orderRepository,
                                    PaymentStateMachineService stateMachine,
                                    ObjectMapper objectMapper) {
        this.webhookEventRepository = webhookEventRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.stateMachine = stateMachine;
        this.objectMapper = objectMapper;
    }

    /**
     * Process a Stripe webhook event.
     */
    @Transactional
    public void processStripeEvent(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventId = root.path("id").asText();
            String eventType = root.path("type").asText();

            // 1. Idempotency: skip if already processed
            if (webhookEventRepository.existsByProviderAndEventId(PaymentProvider.STRIPE, eventId)) {
                log.info("Duplicate Stripe webhook event, skipping: {}", eventId);
                return;
            }

            // 2. Store raw event
            PaymentWebhookEvent webhookEvent = new PaymentWebhookEvent();
            webhookEvent.setProvider(PaymentProvider.STRIPE);
            webhookEvent.setEventId(eventId);
            webhookEvent.setEventType(eventType);
            webhookEvent.setPayload(payload);

            try {
                // 3. Process based on event type
                switch (eventType) {
                    case "payment_intent.succeeded" -> handlePaymentSucceeded(root);
                    case "payment_intent.payment_failed" -> handlePaymentFailed(root);
                    default -> log.debug("Unhandled Stripe event type: {}", eventType);
                }
                webhookEvent.setProcessed(true);
            } catch (Exception e) {
                log.error("Error processing Stripe event {}: {}", eventId, e.getMessage(), e);
                webhookEvent.setProcessingError(e.getMessage());
            }

            webhookEventRepository.save(webhookEvent);

        } catch (Exception e) {
            log.error("Failed to parse Stripe webhook payload: {}", e.getMessage(), e);
        }
    }

    /**
     * Stripe: payment_intent.succeeded → CREATED → CAPTURED → HOLDING
     */
    private void handlePaymentSucceeded(JsonNode root) {
        String piId = root.path("data").path("object").path("id").asText();

        // Lock payment row
        Payment payment = paymentRepository.findByProviderPaymentIntentIdForUpdate(piId)
                .orElse(null);

        if (payment == null) {
            log.warn("No payment found for Stripe PI: {}", piId);
            return;
        }

        // Skip if already processed (idempotent)
        if (payment.getStatus() == PaymentStatus.HOLDING
                || payment.getStatus() == PaymentStatus.RELEASED) {
            log.info("Payment {} already in state {}, skipping", payment.getId(), payment.getStatus());
            return;
        }

        // CREATED → CAPTURED
        stateMachine.validateTransition(payment.getStatus(), PaymentStatus.CAPTURED);
        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setCapturedAt(Instant.now());

        // CAPTURED → HOLDING (immediate)
        stateMachine.validateTransition(PaymentStatus.CAPTURED, PaymentStatus.HOLDING);
        payment.setStatus(PaymentStatus.HOLDING);
        payment.setHoldingStartedAt(Instant.now());
        payment.setLastWebhookPayload(root.toString());

        paymentRepository.save(payment);

        // Update order status to PAID
        Order order = payment.getOrder();
        order.setStatus(Order.OrderStatus.PAID);
        orderRepository.save(order);

        log.info("Payment {} captured and moved to HOLDING for order {}",
                payment.getId(), order.getId());
    }

    /**
     * Stripe: payment_intent.payment_failed → FAILED
     */
    private void handlePaymentFailed(JsonNode root) {
        String piId = root.path("data").path("object").path("id").asText();

        Payment payment = paymentRepository.findByProviderPaymentIntentIdForUpdate(piId)
                .orElse(null);

        if (payment == null) {
            log.warn("No payment found for Stripe PI: {}", piId);
            return;
        }

        if (stateMachine.isTerminal(payment.getStatus())) {
            log.info("Payment {} in terminal state {}, skipping failure handling",
                    payment.getId(), payment.getStatus());
            return;
        }

        stateMachine.validateTransition(payment.getStatus(), PaymentStatus.FAILED);
        payment.setStatus(PaymentStatus.FAILED);
        payment.setLastWebhookPayload(root.toString());
        paymentRepository.save(payment);

        log.info("Payment {} marked FAILED for order {}", payment.getId(), payment.getOrder().getId());
    }

    /**
     * Process a RazorpayX webhook event (for payout status updates).
     */
    @Transactional
    public void processRazorpayEvent(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventId = root.path("event").asText() + "_" + root.path("payload")
                    .path("payout").path("entity").path("id").asText();
            String eventType = root.path("event").asText();

            if (webhookEventRepository.existsByProviderAndEventId(PaymentProvider.RAZORPAYX, eventId)) {
                log.info("Duplicate RazorpayX webhook event, skipping: {}", eventId);
                return;
            }

            PaymentWebhookEvent webhookEvent = new PaymentWebhookEvent();
            webhookEvent.setProvider(PaymentProvider.RAZORPAYX);
            webhookEvent.setEventId(eventId);
            webhookEvent.setEventType(eventType);
            webhookEvent.setPayload(payload);
            webhookEvent.setProcessed(true);

            // RazorpayX payout events are logged for audit
            // Payout status updates can be handled via PayoutService if needed
            log.info("RazorpayX webhook event processed: {} ({})", eventId, eventType);

            webhookEventRepository.save(webhookEvent);
        } catch (Exception e) {
            log.error("Failed to process RazorpayX webhook: {}", e.getMessage(), e);
        }
    }
}
