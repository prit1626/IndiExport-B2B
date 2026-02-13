package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    /**
     * Find reviews for product
     */
    Page<Review> findByProductIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID productId, Pageable pageable);

    /**
     * Find reviews by buyer
     */
    Page<Review> findByBuyerIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID buyerId, Pageable pageable);

    /**
     * Find review for specific order
     */
    java.util.Optional<Review> findByOrderId(UUID orderId);

    /**
     * Average rating for product
     */
    java.util.Optional<java.math.BigDecimal> findAverageRatingByProductIdAndDeletedAtIsNull(UUID productId);
}
