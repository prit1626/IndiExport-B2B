package com.IndiExport.backend.service.admin;

import com.IndiExport.backend.dto.admin.TermsResponse;
import com.IndiExport.backend.dto.admin.UpdateTermsRequest;
import com.IndiExport.backend.entity.TermsVersion;
import com.IndiExport.backend.exception.AdminExceptions;
import com.IndiExport.backend.repository.TermsVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final TermsVersionRepository termsRepository;

    @Transactional(readOnly = true)
    public TermsResponse getLatestPublishedTerms() {
        return termsRepository.findLatestPublished()
                .map(this::mapToResponse)
                .orElseThrow(() -> new AdminExceptions.TermsNotFoundException("No published terms found"));
    }

    @Transactional
    public TermsResponse createNewVersion(UUID adminId, UpdateTermsRequest request) {
        // Get latest version number
        int nextVersion = termsRepository.findLatestAny()
                .map(t -> t.getVersionNumber() + 1)
                .orElse(1);

        TermsVersion terms = TermsVersion.builder()
                .versionNumber(nextVersion)
                .title(request.getTitle())
                .content(request.getContent())
                .createdByAdminId(adminId)
                .isPublished(request.isPublishNow())
                .publishedAt(request.isPublishNow() ? Instant.now() : null)
                .build();

        return mapToResponse(termsRepository.save(terms));
    }

    @Transactional
    public TermsResponse publishVersion(UUID termsId) {
        TermsVersion terms = termsRepository.findById(termsId)
                .orElseThrow(() -> new AdminExceptions.TermsNotFoundException("Terms version not found"));

        if (terms.isPublished()) {
            throw new AdminExceptions.TermsAlreadyPublishedException("Terms version already published");
        }

        terms.setPublished(true);
        terms.setPublishedAt(Instant.now());
        
        return mapToResponse(termsRepository.save(terms));
    }

    private TermsResponse mapToResponse(TermsVersion terms) {
        return TermsResponse.builder()
                .id(terms.getId())
                .versionNumber(terms.getVersionNumber())
                .title(terms.getTitle())
                .content(terms.getContent())
                .isPublished(terms.isPublished())
                .publishedAt(terms.getPublishedAt())
                .createdAt(terms.getCreatedAt())
                .build();
    }
}
