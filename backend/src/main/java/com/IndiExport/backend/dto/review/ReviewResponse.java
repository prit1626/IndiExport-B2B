package com.IndiExport.backend.dto.review;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ReviewResponse {
    private UUID id;
    private int rating;
    private String reviewText;
    private boolean verifiedPurchase;
    private String buyerName;
    private String buyerCountry;
    private List<ReviewMediaResponse> media;
    private Instant createdAt;
}
