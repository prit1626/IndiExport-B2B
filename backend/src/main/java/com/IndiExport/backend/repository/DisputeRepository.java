package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Dispute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, UUID> {

    /**
     * Find dispute by order ID
     */
    Optional<Dispute> findByOrderId(UUID orderId);

    /**
     * Find disputes by status
     */
    Page<Dispute> findByStatusOrderByCreatedAtDesc(Dispute.DisputeStatus status, Pageable pageable);

    /**
     * Find active disputes (not resolved) with payout frozen
     */
    List<Dispute> findByPayoutFrozenTrueAndStatusNotOrderByCreatedAtDesc(Dispute.DisputeStatus status);

    /**
     * Find disputes raised by user
     */
    Page<Dispute> findByRaisedByIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * Find disputes with payout frozen
     */
    List<Dispute> findByPayoutFrozenTrue();

    /**
     * Count disputes by reason
     */
    long countByReason(Dispute.DisputeReason reason);
}
