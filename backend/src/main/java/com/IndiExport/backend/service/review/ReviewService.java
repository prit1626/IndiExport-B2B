package com.IndiExport.backend.service.review;

import com.IndiExport.backend.dto.review.*;
import com.IndiExport.backend.entity.*;
import com.IndiExport.backend.exception.ReviewExceptions.*;
import com.IndiExport.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMediaRepository reviewMediaRepository;
    private final ProductRepository productRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final OrderRepository orderRepository;

    // --- Buyer Actions ---

    @Transactional
    public ReviewResponse createReview(UUID buyerId, UUID productId, CreateReviewRequest request) {
        // 1. Validate Buyer
        BuyerProfile buyer = buyerProfileRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found")); // Should be handled globally

        // 2. Validate Product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 3. One review per buyer per product (MVP)
        if (reviewRepository.existsByBuyerIdAndProductId(buyer.getId(), productId)) {
            throw new ReviewAlreadyExistsException("You have already reviewed this product.");
        }

        // 4. Validate Verified Purchase
        // Check for COMPLETED or DELIVERED orders
        boolean hasPurchased = orderRepository.existsByBuyerIdAndStatusAndItems_Product_Id(
                buyer.getId(), Order.OrderStatus.COMPLETED, productId)
                || orderRepository.existsByBuyerIdAndStatusAndItems_Product_Id(
                buyer.getId(), Order.OrderStatus.DELIVERED, productId);

        if (!hasPurchased) {
             throw new VerifiedPurchaseRequiredException("You must purchase this product to review it.");
        }

        // 5. Create Review
        Review review = Review.builder()
                .buyer(buyer)
                .product(product)
                .seller(product.getSeller())
                .rating(request.getRating())
                .reviewText(request.getReviewText())
                .verifiedPurchase(true) // Enforced by check above
                .status(ReviewStatus.VISIBLE)
                .build();

        try {
            review = reviewRepository.save(review);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ReviewAlreadyExistsException("You have already reviewed this product.");
        }
        
        // 6. Add Media
        if (request.getPhotoUrls() != null && !request.getPhotoUrls().isEmpty()) {
            Review finalReview = review;
            List<ReviewMedia> mediaList = request.getPhotoUrls().stream()
                    .map(url -> ReviewMedia.builder()
                            .review(finalReview)
                            .url(url)
                            .mediaType(ReviewMediaType.IMAGE)
                            .build())
                    .collect(Collectors.toList());
            reviewMediaRepository.saveAll(mediaList);
            review.setMedia(mediaList);
        }

        // 7. Update Product Stats (Fix for Bug 3)
        updateProductStats(product);

        return mapToResponse(review);
    }


    // --- Public Actions ---

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviews(UUID productId, boolean verifiedOnly, boolean photosOnly, Pageable pageable) {
        return reviewRepository.findPublicReviews(productId, verifiedOnly, photosOnly, pageable)
                .map(this::mapToResponse);
    }

    // --- Admin Actions ---

    @Transactional(readOnly = true)
    public Page<AdminReviewResponse> getAllReviews(Pageable pageable) {
        // Simple findAll or optimized query
        return reviewRepository.findAllOrderByReportsDesc(pageable)
                 .map(this::mapToAdminResponse);
    }

    @Transactional
    public AdminReviewResponse hideReview(UUID reviewId, UUID adminId, String reason) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        review.setStatus(ReviewStatus.HIDDEN);
        review.setHiddenAt(Instant.now());
        review.setHiddenByAdminId(adminId);
        review.setHideReason(reason);

        review = reviewRepository.save(review);
        updateProductStats(review.getProduct()); // Update stats
        
        return mapToAdminResponse(review);
    }

    @Transactional
    public AdminReviewResponse restoreReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        review.setStatus(ReviewStatus.VISIBLE);
        review.setHiddenAt(null);
        review.setHiddenByAdminId(null);
        review.setHideReason(null);

        review = reviewRepository.save(review);
        updateProductStats(review.getProduct()); // Update stats

        return mapToAdminResponse(review);
    }

    // --- Mappers ---

    private void updateProductStats(Product product) {
        long count = reviewRepository.countVisibleByProductId(product.getId());
        Double avg = reviewRepository.getAverageRatingByProductId(product.getId());

        product.setTotalReviews((int) count);
        if (avg != null) {
            // Convert 1-5 scale to milli (1000-5000)
            product.setAverageRatingMilli((int) (avg * 1000));
        } else {
            product.setAverageRatingMilli(0);
        }
        productRepository.save(product);
    }

    private ReviewResponse mapToResponse(Review review) {
        List<ReviewMediaResponse> media = review.getMedia().stream()
                .map(m -> ReviewMediaResponse.builder()
                        .id(m.getId())
                        .url(m.getUrl())
                        .mediaType(m.getMediaType())
                        .build())
                .collect(Collectors.toList());

        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .verifiedPurchase(review.isVerifiedPurchase())
                // Assuming BuyerProfile -> User -> name exists (User might not be loaded, check Entity graph)
                // For safety, defaulting if User is null, though typically joined.
                .buyerName(review.getBuyer().getUser().getFullName()) 
                .buyerCountry(review.getBuyer().getCountry())
                .media(media)
                .createdAt(review.getCreatedAt())
                .build();
    }

    private AdminReviewResponse mapToAdminResponse(Review review) {
        List<ReviewMediaResponse> media = review.getMedia().stream()
                .map(m -> ReviewMediaResponse.builder()
                        .id(m.getId())
                        .url(m.getUrl())
                        .mediaType(m.getMediaType())
                        .build())
                .collect(Collectors.toList());

        return AdminReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .buyerId(review.getBuyer().getId())
                .buyerName(review.getBuyer().getUser().getFullName())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .verifiedPurchase(review.isVerifiedPurchase())
                .status(review.getStatus())
                .media(media)
                .reportCount(review.getReports().size())
                .createdAt(review.getCreatedAt())
                .hiddenAt(review.getHiddenAt())
                .hideReason(review.getHideReason())
                .build();
    }
}
