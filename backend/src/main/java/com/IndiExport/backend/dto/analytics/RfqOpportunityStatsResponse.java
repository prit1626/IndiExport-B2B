package com.IndiExport.backend.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfqOpportunityStatsResponse {
    private Long matchingRfqs;
    private Long respondedRfqs;
    private Long wonRfqs;
}
