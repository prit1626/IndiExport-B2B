package com.IndiExport.backend.dto.review;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportReviewRequest {
    @NotBlank(message = "Reason is required")
    private String reason;

    private String description;
}
