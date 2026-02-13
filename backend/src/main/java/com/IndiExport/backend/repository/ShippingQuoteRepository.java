package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.ShippingQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShippingQuoteRepository extends JpaRepository<ShippingQuote, UUID> {

    Optional<ShippingQuote> findByOrderId(UUID orderId);
}
