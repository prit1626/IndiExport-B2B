package com.IndiExport.backend.service.dispute;

import com.IndiExport.backend.entity.Payment;
import com.IndiExport.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisputeEscrowLockService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public void lockFunds(UUID orderId) {
        java.util.Optional<Payment> paymentOpt = paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(orderId);
        
        if (paymentOpt.isEmpty()) {
            log.warn("Payment not found for order: {}. Cannot lock escrow.", orderId);
            return;
        }

        Payment payment = paymentOpt.get();
        if (!payment.isDisputeLocked()) {
            payment.setDisputeLocked(true);
            paymentRepository.save(payment);
            log.info("Escrow locked for order: {}", orderId);
        }
    }

    @Transactional
    public void unlockFunds(UUID orderId) {
        Payment payment = paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));

        if (payment.isDisputeLocked()) {
            payment.setDisputeLocked(false);
            paymentRepository.save(payment);
            log.info("Escrow unlocked for order: {}", orderId);
        }
    }
}
