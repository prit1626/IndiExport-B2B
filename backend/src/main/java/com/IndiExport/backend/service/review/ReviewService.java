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

    @Transactional
    public ReviewResponse createReview(UUID buyerId, CreateReviewRequest request) {
        // 1. Validate Buyer
        BuyerProfile buyer = buyerProfileRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer profile not found"));

        // 2. Validate Order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 3. Validate Order belongs to Buyer
        if (!order.getBuyer().getId().equals(buyer.getId())) {
             throw new RuntimeException("Order does not belong to this buyer");
        }

        // 4. Validate Order Status is DELIVERED
        if (order.getStatus() != Order.OrderStatus.DELIVERED && order.getStatus() != Order.OrderStatus.COMPLETED) {
            throw new RuntimeException("Order must be delivered before reviewing");
        }

        // 5. Validate Product in Order
        boolean productInOrder = order.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(request.getProductId()));
        if (!productInOrder) {
            throw new RuntimeException("Product not found in this order");
        }

        // 6. Only one review per order
        if (reviewRepository.existsByOrderId(request.getOrderId())) {
            throw new ReviewAlreadyExistsException("This order has already been reviewed.");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 7. Create Review
        Review review = Review.builder()
                .buyer(buyer)
                .product(product)
                .seller(product.getSeller())
                .order(order)
                .rating(request.getRating())
                .reviewText(request.getReviewText())
                .verifiedPurchase(true)
                .status(ReviewStatus.VISIBLE)
                .build();

        review = reviewRepository.save(review);

        // 8. Add Media
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

        // 9. Update Product Stats
        updateProductStats(product);

        return mapToResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviews(UUID productId, boolean verifiedOnly, boolean photosOnly, Pageable pageable) {
        return reviewRepository.findPublicReviews(productId, verifiedOnly, photosOnly, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public RatingResponse getProductRatingSummary(UUID productId) {
        Double avg = reviewRepository.getAverageRatingByProductId(productId);
        long count = reviewRepository.countVisibleByProductId(productId);
        
        return RatingResponse.builder()
                .averageRating(avg != null ? avg : 0.0)
                .totalReviews(count)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<AdminReviewResponse> getAllReviews(Pageable pageable) {
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
        updateProductStats(review.getProduct());
        
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
        updateProductStats(review.getProduct());

        return mapToAdminResponse(review);
    }

    @Transactional
    public void updateProductStats(Product product) {
        long count = reviewRepository.countVisibleByProductId(product.getId());
        Double avg = reviewRepository.getAverageRatingByProductId(product.getId());

        product.setTotalReviews((int) count);
        if (avg != null) {
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
