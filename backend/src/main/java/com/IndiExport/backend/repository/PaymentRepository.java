package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Payment;
import com.IndiExport.backend.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByOrderId(UUID orderId);

    /** Find non-failed payment for an order (for idempotent intent creation). */
    Optional<Payment> findByOrderIdAndStatusNot(UUID orderId, PaymentStatus status);

    Optional<Payment> findByProviderPaymentIntentId(String providerPaymentIntentId);

    /** Pessimistic lock for webhook processing — prevents double updates. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.providerPaymentIntentId = :piId")
    Optional<Payment> findByProviderPaymentIntentIdForUpdate(@Param("piId") String piId);

    /** Pessimistic lock for payout release. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Optional<Payment> findByIdForUpdate(@Param("id") UUID id);

    List<Payment> findByStatus(PaymentStatus status);

    /** Find payments eligible for auto-release:
     *  - status=HOLDING, not dispute-locked
     *  - holding started before cutoff */
    @Query("""
        SELECT p FROM Payment p
        WHERE p.status = 'HOLDING'
          AND p.disputeLocked = false
          AND p.holdingStartedAt < :cutoff
    """)
    List<Payment> findEligibleForAutoRelease(@Param("cutoff") Instant cutoff);

    @Query("SELECT p FROM Payment p WHERE p.sellerId = :sellerId ORDER BY p.createdAt DESC")
    List<Payment> findBySellerId(@Param("sellerId") UUID sellerId);

    @Query("SELECT p FROM Payment p WHERE p.buyerId = :buyerId ORDER BY p.createdAt DESC")
    List<Payment> findByBuyerId(@Param("buyerId") UUID buyerId);
}
