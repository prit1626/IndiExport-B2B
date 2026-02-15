package com.IndiExport.backend.repository;

import com.IndiExport.backend.entity.Review;
import com.IndiExport.backend.entity.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // For public product page (only visible reviews)
    Page<Review> findByProductIdAndStatus(UUID productId, ReviewStatus status, Pageable pageable);

    // For public product page with filters - Optimized with JOIN FETCH to prevent N+1
    @Query("SELECT r FROM Review r " +
           "JOIN FETCH r.buyer b " +
           "JOIN FETCH b.user u " +
           "LEFT JOIN FETCH r.media m " +
           "WHERE r.product.id = :productId AND r.status = 'VISIBLE' " +
           "AND (:verifiedOnly = false OR r.verifiedPurchase = true) " +
           "AND (:photosOnly = false OR size(r.media) > 0)")
    Page<Review> findPublicReviews(@Param("productId") UUID productId,
                                   @Param("verifiedOnly") boolean verifiedOnly,
                                   @Param("photosOnly") boolean photosOnly,
                                   Pageable pageable);

    // Check for existing review by buyer on product
    boolean existsByBuyerIdAndProductId(UUID buyerId, UUID productId);

    // For admin moderation (all reviews, often sorted by reports)
    @Query("SELECT r FROM Review r LEFT JOIN r.reports rep GROUP BY r.id ORDER BY COUNT(rep) DESC")
    Page<Review> findAllOrderByReportsDesc(Pageable pageable);

    // Find reported reviews
    @Query("SELECT r FROM Review r JOIN r.reports rep GROUP BY r.id")
    Page<Review> findReportedReviews(Pageable pageable);

    // Aggregation queries for Product Stats
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.status = 'VISIBLE'")
    long countVisibleByProductId(@Param("productId") UUID productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.status = 'VISIBLE'")
    Double getAverageRatingByProductId(@Param("productId") UUID productId);
}
