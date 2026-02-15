package com.IndiExport.backend.dto.review;

import com.IndiExport.backend.entity.ReviewStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AdminReviewResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private UUID buyerId;
    private String buyerName;
    private int rating;
    private String reviewText;
    private boolean verifiedPurchase;
    private ReviewStatus status;
    private List<ReviewMediaResponse> media;
    private int reportCount;
    private Instant createdAt;
    private Instant hiddenAt;
    private String hideReason;
}
