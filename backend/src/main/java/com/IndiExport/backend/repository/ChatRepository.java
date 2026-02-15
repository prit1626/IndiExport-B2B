package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    Optional<Chat> findByBuyerIdAndSellerIdAndProductId(UUID buyerId, UUID sellerId, UUID productId);

    Optional<Chat> findByBuyerIdAndSellerIdAndRfqId(UUID buyerId, UUID sellerId, UUID rfqId);

    Page<Chat> findByBuyerId(UUID buyerId, Pageable pageable);

    Page<Chat> findBySellerId(UUID sellerId, Pageable pageable);
}
