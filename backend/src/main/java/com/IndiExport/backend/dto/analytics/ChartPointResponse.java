package com.IndiExport.backend.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartPointResponse {
    private String period; // e.g., "2026-01" or "2026-01-01"
    private Long value;    // e.g., revenue in paise or count
}
