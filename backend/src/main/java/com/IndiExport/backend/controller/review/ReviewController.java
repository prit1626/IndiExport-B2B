package com.IndiExport.backend.controller.review;

import com.IndiExport.backend.dto.review.CreateReviewRequest;
import com.IndiExport.backend.dto.review.RatingResponse;
import com.IndiExport.backend.dto.review.ReviewResponse;
import com.IndiExport.backend.entity.BuyerProfile;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private final BuyerProfileRepository buyerProfileRepository;

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CreateReviewRequest request) {
        
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        BuyerProfile buyer = buyerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Buyer profile not found"));

        return ResponseEntity.ok(reviewService.createReview(buyer.getId(), request));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "false") boolean verifiedOnly,
            @RequestParam(defaultValue = "false") boolean photosOnly,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(reviewService.getProductReviews(productId, verifiedOnly, photosOnly, pageable));
    }

    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<RatingResponse> getProductRatingSummary(@PathVariable UUID productId) {
        return ResponseEntity.ok(reviewService.getProductRatingSummary(productId));
    }
}
