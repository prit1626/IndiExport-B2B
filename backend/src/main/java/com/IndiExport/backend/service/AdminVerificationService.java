package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.SellerKycDto;
import com.IndiExport.backend.entity.AuditLog;
import com.IndiExport.backend.entity.SellerKyc;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.BusinessRuleViolationException;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.AuditLogRepository;
import com.IndiExport.backend.repository.SellerKycRepository;
import com.IndiExport.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminVerificationService {

    private final SellerKycRepository sellerKycRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public List<SellerKycDto.AdminSellerKycResponse> getPendingSellers() {
        return sellerKycRepository.findPendingVerifications().stream()
                .map(this::mapToAdminResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SellerKycDto.AdminSellerKycResponse getSellerKyc(UUID sellerId) {
        SellerKyc kyc = sellerKycRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerKyc", sellerId.toString()));
        return mapToAdminResponse(kyc);
    }

    @Transactional
    public void approveSeller(UUID adminId, UUID sellerId) {
        SellerKyc kyc = sellerKycRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerKyc", sellerId.toString()));
        
        if (kyc.getVerificationStatus() != SellerKyc.VerificationStatus.PENDING) {
            throw new BusinessRuleViolationException("KYC is not in PENDING status");
        }

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User", adminId.toString()));

        String beforeState = kyc.getVerificationStatus().toString();
        
        kyc.setVerificationStatus(SellerKyc.VerificationStatus.VERIFIED);
        kyc.setVerifiedAt(LocalDateTime.now());
        kyc.setVerifiedBy(admin);
        sellerKycRepository.save(kyc);

        logAudit(admin, "SELLER_KYC", kyc.getId(), AuditLog.AuditAction.UPDATE, 
                beforeState, kyc.getVerificationStatus().toString(), "Seller verified by admin");
    }

    @Transactional
    public void rejectSeller(UUID adminUserId, UUID sellerId, SellerKycDto.AdminRejectSellerRequest request) {
        SellerKyc kyc = sellerKycRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerKyc", sellerId.toString()));

        if (kyc.getVerificationStatus() != SellerKyc.VerificationStatus.PENDING) {
            throw new BusinessRuleViolationException("Seller is not in PENDING status");
        }

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", adminUserId.toString()));

        String beforeState = kyc.getVerificationStatus().toString();

        kyc.setVerificationStatus(SellerKyc.VerificationStatus.REJECTED);
        kyc.setRejectionReason(request.getReason());
        sellerKycRepository.save(kyc);

        logAudit(admin, "SELLER_KYC", kyc.getId(), AuditLog.AuditAction.UPDATE, 
                beforeState, kyc.getVerificationStatus().toString(), "Seller rejected: " + request.getReason());
    }

    private void logAudit(User admin, String entityType, UUID entityId, AuditLog.AuditAction action, 
                          String before, String after, String description) {
        AuditLog audit = AuditLog.builder()
                .user(admin)
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .beforeState(before)
                .afterState(after)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(audit);
    }

    private SellerKycDto.AdminSellerKycResponse mapToAdminResponse(SellerKyc kyc) {
        return SellerKycDto.AdminSellerKycResponse.builder()
                .sellerId(kyc.getSeller().getId())
                .companyName(kyc.getSeller().getCompanyName())
                .iecNumber(kyc.getIecNumber())
                .iecDocumentUrl(kyc.getIecDocumentUrl())
                .panNumber(kyc.getPanNumber())
                .panDocumentUrl(kyc.getPanDocumentUrl())
                .gstinNumber(kyc.getGstinNumber())
                .gstinDocumentUrl(kyc.getGstinDocumentUrl())
                .bankAccountHolderName(kyc.getBankAccountHolderName())
                .bankAccountNumberMasked(kyc.getBankAccountNumberMasked())
                .bankIfscCode(kyc.getBankIfscCode())
                .bankName(kyc.getBankName())
                .bankBranch(kyc.getBankBranch())
                .payoutMethodPreference(kyc.getPayoutMethodPreference())
                .verificationStatus(kyc.getVerificationStatus())
                .submittedAt(kyc.getSubmittedAt())
                .build();
    }
}
