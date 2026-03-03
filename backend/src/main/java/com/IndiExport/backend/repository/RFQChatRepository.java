package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.RFQChat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RFQChatRepository extends JpaRepository<RFQChat, UUID> {

    Optional<RFQChat> findByRfqIdAndSellerId(UUID rfqId, UUID sellerId);

    @Query("""
        SELECT c FROM RFQChat c
        WHERE c.buyer.id = :buyerId
        ORDER BY c.createdAt DESC
    """)
    Page<RFQChat> findByBuyerId(@Param("buyerId") UUID buyerId, Pageable pageable);

    @Query("""
        SELECT c FROM RFQChat c
        WHERE c.seller.id = :sellerId
        ORDER BY c.createdAt DESC
    """)
    Page<RFQChat> findBySellerId(@Param("sellerId") UUID sellerId, Pageable pageable);
}
