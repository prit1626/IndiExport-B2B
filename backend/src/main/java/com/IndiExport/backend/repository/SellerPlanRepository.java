package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.SellerPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SellerPlanRepository extends JpaRepository<SellerPlan, UUID> {

    /**
     * Find active seller plan for a seller
     */
    Optional<SellerPlan> findBySellerIdAndIsActiveTrue(UUID sellerId);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("SELECT s FROM SellerPlan s WHERE s.seller.id = :sellerId AND s.isActive = true")
    Optional<SellerPlan> findWithLockBySellerIdAndIsActiveTrue(UUID sellerId);

    /**
     * Find plan by seller ID (may not be active)
     */
    Optional<SellerPlan> findBySellerId(UUID sellerId);

    /**
     * Check if seller has an active plan
     */
    boolean existsBySellerIdAndIsActiveTrue(UUID sellerId);
}
