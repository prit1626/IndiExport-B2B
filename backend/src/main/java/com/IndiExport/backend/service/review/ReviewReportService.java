package com.IndiExport.backend.service.review;

import com.IndiExport.backend.dto.review.ReportReviewRequest;
import com.IndiExport.backend.entity.BuyerProfile;
import com.IndiExport.backend.entity.Review;
import com.IndiExport.backend.entity.ReviewReport;
import com.IndiExport.backend.exception.ReviewExceptions.*;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.ReviewReportRepository;
import com.IndiExport.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewReportService {

    private final ReviewReportRepository reviewReportRepository;
    private final ReviewRepository reviewRepository;
    private final BuyerProfileRepository buyerProfileRepository;

    @Transactional
    public void reportReview(UUID reviewId, UUID reporterId, ReportReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        if (reviewReportRepository.existsByReviewIdAndReporterId(reviewId, reporterId)) {
            throw new ReviewReportAlreadyExistsException("You have already reported this review.");
        }

        BuyerProfile reporter = buyerProfileRepository.findById(reporterId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        ReviewReport report = ReviewReport.builder()
                .review(review)
                .reporter(reporter)
                .reason(request.getReason())
                .description(request.getDescription())
                .build();

        reviewReportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public Page<ReviewReport> getReports(Pageable pageable) {
        return reviewReportRepository.findAll(pageable);
    }
}
