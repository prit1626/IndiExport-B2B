package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.PaymentPayout;
import com.IndiExport.backend.entity.PayoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentPayoutRepository extends JpaRepository<PaymentPayout, UUID> {

    Optional<PaymentPayout> findByPaymentId(UUID paymentId);

    /** Prevent double payout: check if a non-failed payout exists. */
    boolean existsByPaymentIdAndStatusNot(UUID paymentId, PayoutStatus status);

    List<PaymentPayout> findBySellerId(UUID sellerId);
}
