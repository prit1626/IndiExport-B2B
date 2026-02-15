package com.IndiExport.backend.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsOrderSummaryDTO {
    private UUID orderId;
    private String orderNumber;
    private String status;
    private Long totalAmountPaise;
    private Instant createdAt;
}
