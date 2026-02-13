package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.CartItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findByBuyerIdOrderByCreatedAtDesc(UUID buyerId);

    Optional<CartItem> findByBuyerIdAndProductId(UUID buyerId, UUID productId);

    Optional<CartItem> findByIdAndBuyerId(UUID id, UUID buyerId);

    void deleteByBuyerId(UUID buyerId);

    long countByBuyerId(UUID buyerId);

    /**
     * Lock all cart items for a buyer during checkout to prevent concurrent modifications.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CartItem c JOIN FETCH c.product p JOIN FETCH p.seller s WHERE c.buyer.id = :buyerId ORDER BY c.createdAt")
    List<CartItem> findByBuyerIdWithLock(@Param("buyerId") UUID buyerId);
}
