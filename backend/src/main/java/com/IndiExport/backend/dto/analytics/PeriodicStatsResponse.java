package com.IndiExport.backend.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodicStatsResponse {
    private Long total;
    private Long today;
    private Long thisWeek;
    private Long thisMonth;
}
