package com.IndiExport.backend.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentActivityResponse {
    private String type; // PRODUCT_VIEW, INQUIRY, RFQ_QUOTE
    private String description;
    private Instant timestamp;
}
