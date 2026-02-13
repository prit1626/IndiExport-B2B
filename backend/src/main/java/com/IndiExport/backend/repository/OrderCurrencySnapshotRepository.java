package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.OrderCurrencySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderCurrencySnapshotRepository extends JpaRepository<OrderCurrencySnapshot, UUID> {

    Optional<OrderCurrencySnapshot> findByOrderId(UUID orderId);
}
