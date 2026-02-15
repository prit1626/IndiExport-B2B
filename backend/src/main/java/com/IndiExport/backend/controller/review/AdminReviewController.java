package com.IndiExport.backend.controller.review;

import com.IndiExport.backend.dto.review.AdminReviewResponse;
import com.IndiExport.backend.entity.ReviewReport;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.repository.UserRepository;
import com.IndiExport.backend.service.review.ReviewReportService;
import com.IndiExport.backend.service.review.ReviewService;
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
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;
    private final ReviewReportService reviewReportService;
    private final UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AdminReviewResponse>> getAllReviews(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getAllReviews(pageable));
    }

    @PutMapping("/{reviewId}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminReviewResponse> hideReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID reviewId,
            @RequestParam String reason) {
        
        User admin = getUser(userDetails);
        return ResponseEntity.ok(reviewService.hideReview(reviewId, admin.getId(), reason));
    }

    @PutMapping("/{reviewId}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminReviewResponse> restoreReview(
            @PathVariable UUID reviewId) {
        return ResponseEntity.ok(reviewService.restoreReview(reviewId));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReviewReport>> getReportedReviews(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewReportService.getReports(pageable));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
