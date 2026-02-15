package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Dispute;
import com.IndiExport.backend.entity.DisputeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, UUID> {

    Optional<Dispute> findByOrderId(UUID orderId);

    // Check if an active dispute exists for an order
    @Query("SELECT COUNT(d) > 0 FROM Dispute d WHERE d.order.id = :orderId AND d.status IN :statuses")
    boolean existsByOrderIdAndStatusIn(@Param("orderId") UUID orderId, @Param("statuses") Collection<DisputeStatus> statuses);

    // Buyer disputes
    Page<Dispute> findByBuyerId(UUID buyerId, Pageable pageable);
    
    // Seller disputes
    Page<Dispute> findBySellerId(UUID sellerId, Pageable pageable);

    // Admin filters
    @Query("SELECT d FROM Dispute d WHERE " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:buyerId IS NULL OR d.buyerId = :buyerId) AND " +
           "(:sellerId IS NULL OR d.sellerId = :sellerId)")
    Page<Dispute> findAllByFilters(@Param("status") DisputeStatus status,
                                   @Param("buyerId") UUID buyerId,
                                   @Param("sellerId") UUID sellerId,
                                   Pageable pageable);
}
