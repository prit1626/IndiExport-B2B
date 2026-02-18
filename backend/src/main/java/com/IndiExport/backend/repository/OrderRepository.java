package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find orders by buyer ID with pagination
     */
    Page<Order> findByBuyerId(UUID buyerId, Pageable pageable);

    /**
     * Find orders by buyer and status
     */
    Page<Order> findByBuyerIdAndStatus(
            UUID buyerId,
            Order.OrderStatus status,
            Pageable pageable
    );

    /**
     * Find orders by seller ID with pagination
     */
    Page<Order> findBySellerIdOrderByCreatedAtDesc(UUID sellerId, Pageable pageable);

    /**
     * Find orders by buyer and status
     */
    Page<Order> findByBuyerIdAndStatusOrderByCreatedAtDesc(
            UUID buyerId,
            Order.OrderStatus status,
            Pageable pageable
    );

    /**
     * Find orders by seller and status
     */
    Page<Order> findBySellerIdAndStatusOrderByCreatedAtDesc(
            UUID sellerId,
            Order.OrderStatus status,
            Pageable pageable
    );

    /**
     * Find pending confirmations for seller
     */
    List<Order> findBySellerIdAndStatusOrderByCreatedAtAsc(
            UUID sellerId,
            Order.OrderStatus status
    );

    /**
     * Count orders for seller (total sales metric)
     */
    long countBySellerIdAndStatusNotIn(UUID sellerId, List<Order.OrderStatus> statuses);

    /**
     * Find orders within date range (for reporting)
     */
    List<Order> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    /**
     * Check if a buyer has purchased a specific product in a completed/delivered order.
     * Used for Verified Purchase badge.
     */
    boolean existsByBuyerIdAndStatusAndItems_Product_Id(UUID buyerId, Order.OrderStatus status, UUID productId);
}
