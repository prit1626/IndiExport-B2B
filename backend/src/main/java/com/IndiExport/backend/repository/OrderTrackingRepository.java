package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.OrderTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderTrackingRepository extends JpaRepository<OrderTracking, UUID> {

    Optional<OrderTracking> findByOrderId(UUID orderId);

    boolean existsByOrderId(UUID orderId);
}
