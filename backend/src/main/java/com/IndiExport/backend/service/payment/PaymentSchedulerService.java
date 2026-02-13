package com.IndiExport.backend.service.payment;

import com.IndiExport.backend.entity.PaymentStatus;
import com.IndiExport.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Scheduled service for automatic payout release.
 *
 * Runs every hour, finds payments in HOLDING state that are eligible:
 *   - holdingStartedAt is older than N days (configurable)
 *   - not dispute-locked
 * Attempts to release payout for each.
 */
@Service
@RequiredArgsConstructor
public class PaymentSchedulerService {

    private final PaymentRepository paymentRepository;
    private final PayoutService payoutService;

    @Value("${payment.escrow.auto-release-days:7}")
    private int autoReleaseDays;

    /**
     * Auto-release eligible payouts every hour.
     */
    @Scheduled(fixedDelayString = "3600000") // 1 hour
    public void autoRelease() {
        Instant cutoff = Instant.now().minus(autoReleaseDays, ChronoUnit.DAYS);
        var eligiblePayments = paymentRepository.findEligibleForAutoRelease(cutoff);

        for (var payment : eligiblePayments) {
            try {
                payoutService.releasePayout(payment.getId(), false);
            } catch (Exception e) {
                // Auto-release failed, continue with next payment
            }
        }
    }
}
