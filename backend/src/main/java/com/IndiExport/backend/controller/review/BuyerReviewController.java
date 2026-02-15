package com.IndiExport.backend.controller.review;

import com.IndiExport.backend.dto.review.CreateReviewRequest;
import com.IndiExport.backend.dto.review.ReportReviewRequest;
import com.IndiExport.backend.dto.review.ReviewResponse;
import com.IndiExport.backend.entity.BuyerProfile;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.review.ReviewReportService;
import com.IndiExport.backend.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buyer")
@RequiredArgsConstructor
public class BuyerReviewController {

    private final ReviewService reviewService;
    private final ReviewReportService reviewReportService;
    private final UserRepository userRepository;
    private final BuyerProfileRepository buyerProfileRepository;

    @PostMapping("/products/{productId}/review")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID productId,
            @RequestBody @Valid CreateReviewRequest request) {
        
        User user = getUser(userDetails);
        BuyerProfile buyer = buyerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Buyer profile not found"));

        return ResponseEntity.ok(reviewService.createReview(buyer.getId(), productId, request));
    }

    @PostMapping("/reviews/{reviewId}/report")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<Void> reportReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID reviewId,
            @RequestBody @Valid ReportReviewRequest request) {

        User user = getUser(userDetails);
        BuyerProfile buyer = buyerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Buyer profile not found"));

        reviewReportService.reportReview(reviewId, buyer.getId(), request);
        return ResponseEntity.ok().build();
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
