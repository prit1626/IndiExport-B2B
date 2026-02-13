package com.IndiExport.backend.service.payment;

import com.IndiExport.backend.dto.PaymentDto;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.*;
import com.IndiExport.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Handles seller payout release:
 *   1. Verifies all release conditions (delivery confirmed, no dispute, account verified)
 *   2. Calculates INR amount (using locked exchange rate, minus commission)
 *   3. Initiates payout via RazorpayX
 *   4. Updates payment status to RELEASED
 */
@Service
@RequiredArgsConstructor
public class PayoutService {

    private final PaymentRepository paymentRepository;
    private final PaymentPayoutRepository payoutRepository;
    private final SellerPayoutAccountRepository payoutAccountRepository;
    private final DisputeRepository disputeRepository;
    private final OrderRepository orderRepository;
    private final PaymentStateMachineService stateMachine;
    private final RazorpayXPayoutProvider razorpayXProvider;

    /** Platform commission in basis points (e.g. 250 = 2.5%). */
    @Value("${payment.platform.commission-basis-points:250}")
    private int commissionBasisPoints;

    /**
     * Release payout for a payment.
     * Can be called by the auto-release scheduler or admin force-release.
     *
     * @param paymentId  the payment to release
     * @param forceAdmin if true, skip delivery confirmation check (admin override)
     */
    @Transactional
    public PaymentDto.PayoutResponse releasePayout(UUID paymentId, boolean forceAdmin) {
        // 1. Lock payment row
        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId.toString()));

        // 2. Idempotent: if already released, return existing payout
        if (payment.getStatus() == PaymentStatus.RELEASED) {
            return payoutRepository.findByPaymentId(paymentId)
                    .map(this::mapToPayoutResponse)
                    .orElse(null);
        }

        // 3. Validate state transition
        stateMachine.validateTransition(payment.getStatus(), PaymentStatus.RELEASED);

        // 4. Check for active disputes
        if (payment.isDisputeLocked()) {
            throw new InvalidPaymentStateException("HOLDING", "Cannot release: dispute is active");
        }

        var dispute = disputeRepository.findByOrderId(payment.getOrder().getId());
        if (dispute.isPresent() && dispute.get().isUnderDispute()) {
            payment.setDisputeLocked(true);
            paymentRepository.save(payment);
            throw new InvalidPaymentStateException("HOLDING",
                    "Cannot release: active dispute found on order");
        }

        // 5. Check order delivery status (unless admin force)
        if (!forceAdmin) {
            Order order = payment.getOrder();
            if (order.getStatus() != Order.OrderStatus.DELIVERED
                    && order.getStatus() != Order.OrderStatus.COMPLETED) {
                throw new InvalidPaymentStateException("HOLDING",
                        "Cannot release: order not yet delivered/completed");
            }
        }

        // 6. Prevent double payout
        if (payoutRepository.existsByPaymentIdAndStatusNot(paymentId, PayoutStatus.FAILED)) {
            throw new InvalidPaymentStateException("HOLDING", "Payout already in progress");
        }

        // 7. Verify seller payout account
        SellerPayoutAccount account = payoutAccountRepository.findBySellerId(payment.getSellerId())
                .orElseThrow(() -> new PayoutAccountNotVerifiedException(payment.getSellerId().toString()));

        if (account.getStatus() != SellerPayoutAccount.AccountStatus.VERIFIED) {
            throw new PayoutAccountNotVerifiedException(payment.getSellerId().toString());
        }

        // 8. Calculate payout: INR paise minus commission
        long grossInrPaise = payment.getAmountInrPaise();
        long commissionPaise = (grossInrPaise * commissionBasisPoints) / 10_000;
        long netPayoutPaise = grossInrPaise - commissionPaise;

        // 9. Get exchange rate from OrderCurrencySnapshot (for audit)
        Order order = payment.getOrder();
        long exchangeRateMicros = 1_000_000L; // default 1:1 for INR
        if (order.getCurrencySnapshot() != null) {
            exchangeRateMicros = order.getCurrencySnapshot().getExchangeRateMicros();
        }

        // 10. Create payout record (CREATED state)
        PaymentPayout payout = PaymentPayout.builder()
                .payment(payment)
                .sellerId(payment.getSellerId())
                .provider(PaymentProvider.RAZORPAYX)
                .amountInrPaise(netPayoutPaise)
                .commissionPaise(commissionPaise)
                .exchangeRateMicros(exchangeRateMicros)
                .status(PayoutStatus.CREATED)
                .build();

        payout = payoutRepository.save(payout);

        // 11. Initiate RazorpayX payout
        try {
            String providerPayoutId = razorpayXProvider.createPayout(
                    account.getRazorpayFundAccountId(),
                    netPayoutPaise,
                    payout.getId().toString()
            );

            payout.setProviderPayoutId(providerPayoutId);
            payout.setStatus(PayoutStatus.PROCESSING);
            payoutRepository.save(payout);

            // 12. Update payment status to RELEASED
            payment.setStatus(PaymentStatus.RELEASED);
            payment.setReleasedAt(Instant.now());
            paymentRepository.save(payment);

            // Update order status
            order.setStatus(Order.OrderStatus.COMPLETED);
            orderRepository.save(order);

            return mapToPayoutResponse(payout);

        } catch (PayoutFailedException e) {
            payout.setStatus(PayoutStatus.FAILED);
            payout.setFailureReason(e.getMessage());
            payoutRepository.save(payout);
            throw e;
        }
    }

    /**
     * Admin: force hold a payment (prevent auto-release).
     */
    @Transactional
    public void adminHoldPayment(UUID paymentId) {
        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId.toString()));

        if (payment.getStatus() != PaymentStatus.HOLDING) {
            throw new InvalidPaymentStateException(payment.getStatus().name(),
                    "Only HOLDING payments can be manually held");
        }

        payment.setDisputeLocked(true);
        paymentRepository.save(payment);
    }

    /**
     * Admin: approve refund for a payment.
     */
    @Transactional
    public void adminRefundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId.toString()));

        stateMachine.validateTransition(payment.getStatus(), PaymentStatus.REFUNDED);

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(Instant.now());
        paymentRepository.save(payment);

        // TODO: integrate Stripe refund API call
    }

    private PaymentDto.PayoutResponse mapToPayoutResponse(PaymentPayout p) {
        return PaymentDto.PayoutResponse.builder()
                .payoutId(p.getId())
                .paymentId(p.getPayment().getId())
                .sellerId(p.getSellerId())
                .amountInrPaise(p.getAmountInrPaise())
                .commissionPaise(p.getCommissionPaise())
                .exchangeRateMicros(p.getExchangeRateMicros())
                .status(p.getStatus())
                .providerPayoutId(p.getProviderPayoutId())
                .createdAt(p.getCreatedAt())
                .completedAt(p.getCompletedAt())
                .build();
    }
}
