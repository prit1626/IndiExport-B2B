package com.IndiExport.backend.dto.dispute;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddEvidenceRequest {
    @NotBlank(message = "File URL is required")
    private String fileUrl;

    private String fileType; // IMAGE, DOCUMENT
}
