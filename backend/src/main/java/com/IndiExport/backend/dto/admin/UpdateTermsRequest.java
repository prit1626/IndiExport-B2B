package com.IndiExport.backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTermsRequest {

    @NotBlank(message = "Markdown content is required")
    private String markdown;

    @NotBlank(message = "Version label is required")
    private String versionLabel;

    @Builder.Default
    private boolean publishNow = true; // Default for one-click publish
}
