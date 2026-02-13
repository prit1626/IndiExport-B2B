package com.IndiExport.backend.service.payment;

import com.IndiExport.backend.entity.PaymentStatus;
import com.IndiExport.backend.exception.InvalidPaymentStateException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * Validates payment status transitions.
 * All state changes MUST go through this service.
 *
 * Valid transitions:
 *   CREATED  → CAPTURED, FAILED
 *   CAPTURED → HOLDING, FAILED
 *   HOLDING  → RELEASED, REFUNDED, FAILED
 *   RELEASED → (terminal)
 *   REFUNDED → (terminal)
 *   FAILED   → CREATED (retry)
 */
@Service
public class PaymentStateMachineService {

    private static final Map<PaymentStatus, Set<PaymentStatus>> VALID_TRANSITIONS = Map.of(
            PaymentStatus.CREATED,  Set.of(PaymentStatus.CAPTURED, PaymentStatus.FAILED),
            PaymentStatus.CAPTURED, Set.of(PaymentStatus.HOLDING, PaymentStatus.FAILED),
            PaymentStatus.HOLDING,  Set.of(PaymentStatus.RELEASED, PaymentStatus.REFUNDED, PaymentStatus.FAILED),
            PaymentStatus.RELEASED, Set.of(), // terminal
            PaymentStatus.REFUNDED, Set.of(), // terminal
            PaymentStatus.FAILED,   Set.of(PaymentStatus.CREATED) // allow retry
    );

    /**
     * Validate and return if the transition is allowed.
     * @throws InvalidPaymentStateException if the transition is invalid
     */
    public void validateTransition(PaymentStatus from, PaymentStatus to) {
        Set<PaymentStatus> allowed = VALID_TRANSITIONS.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new InvalidPaymentStateException(from.name(), to.name());
        }
    }

    public boolean isTerminal(PaymentStatus status) {
        return status == PaymentStatus.RELEASED || status == PaymentStatus.REFUNDED;
    }
}
