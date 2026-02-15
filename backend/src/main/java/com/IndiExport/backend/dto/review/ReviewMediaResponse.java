package com.IndiExport.backend.dto.review;

import com.IndiExport.backend.entity.ReviewMediaType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ReviewMediaResponse {
    private UUID id;
    private String url;
    private ReviewMediaType mediaType;
}
