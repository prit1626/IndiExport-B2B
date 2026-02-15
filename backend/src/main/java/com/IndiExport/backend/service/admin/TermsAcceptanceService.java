package com.IndiExport.backend.service.admin;

import com.IndiExport.backend.dto.admin.AcceptanceResponse;
import com.IndiExport.backend.entity.TermsVersion;
import com.IndiExport.backend.entity.UserTermsAcceptance;
import com.IndiExport.backend.exception.AdminExceptions;
import com.IndiExport.backend.repository.TermsVersionRepository;
import com.IndiExport.backend.repository.UserTermsAcceptanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TermsAcceptanceService {

    private final UserTermsAcceptanceRepository acceptanceRepository;
    private final TermsVersionRepository termsRepository;

    @Transactional
    public AcceptanceResponse acceptTerms(UUID userId, UUID termsVersionId, String ipAddress, String userAgent) {
        TermsVersion terms = termsRepository.findById(termsVersionId)
                .orElseThrow(() -> new AdminExceptions.TermsNotFoundException("Terms version not found"));

        if (!terms.isPublished()) {
             throw new AdminExceptions.TermsNotFoundException("Cannot accept unpublished terms");
        }

        if (acceptanceRepository.existsByUserIdAndTermsVersionId(userId, termsVersionId)) {
            // Already accepted, just return existing (idempotent)
            UserTermsAcceptance existing = acceptanceRepository.findByUserIdAndTermsVersionId(userId, termsVersionId).get();
            return mapToResponse(existing);
        }

        UserTermsAcceptance acceptance = UserTermsAcceptance.builder()
                .userId(userId)
                .termsVersion(terms)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        return mapToResponse(acceptanceRepository.save(acceptance));
    }

    @Transactional(readOnly = true)
    public boolean hasAcceptedLatest(UUID userId) {
        return termsRepository.findLatestPublished()
                .map(latest -> acceptanceRepository.existsByUserIdAndTermsVersionId(userId, latest.getId()))
                .orElse(true); // If no terms exist, assume accepted (or false depending on biz logic)
    }

    private AcceptanceResponse mapToResponse(UserTermsAcceptance acceptance) {
        return AcceptanceResponse.builder()
                .id(acceptance.getId())
                .userId(acceptance.getUserId())
                .termsVersionId(acceptance.getTermsVersion().getId())
                .termsVersionNumber(acceptance.getTermsVersion().getVersionNumber())
                .acceptedAt(acceptance.getAcceptedAt())
                .build();
    }
}
