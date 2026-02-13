package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.RfqQuote;
import com.IndiExport.backend.entity.RfqQuoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RfqQuoteRepository extends JpaRepository<RfqQuote, UUID> {

    List<RfqQuote> findByRfqId(UUID rfqId);
    
    // Check if seller already quoted for this RFQ (active only)
    boolean existsByRfqIdAndSellerIdAndStatus(UUID rfqId, UUID sellerId, RfqQuoteStatus status);

    Optional<RfqQuote> findByRfqIdAndSellerId(UUID rfqId, UUID sellerId);
}
