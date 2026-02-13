package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.SellerKycDto;
import com.IndiExport.backend.entity.SellerKyc;
import com.IndiExport.backend.entity.SellerProfile;
import com.IndiExport.backend.exception.BusinessRuleViolationException;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.SellerKycRepository;
import com.IndiExport.backend.repository.SellerProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SellerKycService {

    private static final Logger log = LoggerFactory.getLogger(SellerKycService.class);

    private final SellerKycRepository sellerKycRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final FileStorageService fileStorageService;

    public SellerKycService(SellerKycRepository sellerKycRepository,
                            SellerProfileRepository sellerProfileRepository,
                            FileStorageService fileStorageService) {
        this.sellerKycRepository = sellerKycRepository;
        this.sellerProfileRepository = sellerProfileRepository;
        this.fileStorageService = fileStorageService;
    }

    public SellerKycDto.SellerKycStatusResponse getKycStatus(UUID userId) {
        SellerKyc kyc = getKycByUserId(userId);
        return mapToStatusResponse(kyc);
    }

    @Transactional
    public String uploadIec(UUID userId, String iecNumber, MultipartFile file) throws IOException {
        SellerKyc kyc = getKycByUserId(userId);
        
        fileStorageService.validateFile(file, new String[]{"application/pdf", "image/png", "image/jpeg"}, 5);
        String url = fileStorageService.uploadFile(file, "sellers/" + kyc.getSeller().getId() + "/iec");
        
        kyc.setIecNumber(iecNumber);
        kyc.setIecDocumentUrl(url);
        
        checkAndSetPending(kyc);
        sellerKycRepository.save(kyc);
        return url;
    }

    @Transactional
    public String uploadPan(UUID userId, String panNumber, MultipartFile file) throws IOException {
        SellerKyc kyc = getKycByUserId(userId);
        
        fileStorageService.validateFile(file, new String[]{"application/pdf", "image/png", "image/jpeg"}, 5);
        String url = fileStorageService.uploadFile(file, "sellers/" + kyc.getSeller().getId() + "/pan");
        
        kyc.setPanNumber(panNumber);
        kyc.setPanDocumentUrl(url);
        
        checkAndSetPending(kyc);
        sellerKycRepository.save(kyc);
        return url;
    }

    @Transactional
    public String uploadGstin(UUID userId, String gstinNumber, MultipartFile file) throws IOException {
        SellerKyc kyc = getKycByUserId(userId);
        
        fileStorageService.validateFile(file, new String[]{"application/pdf", "image/png", "image/jpeg"}, 5);
        String url = fileStorageService.uploadFile(file, "sellers/" + kyc.getSeller().getId() + "/gstin");
        
        kyc.setGstinNumber(gstinNumber);
        kyc.setGstinDocumentUrl(url);
        
        // GSTIN is optional, so it doesn't alone trigger PENDING if others are missing, 
        // but we check anyway in case it was the last thing.
        checkAndSetPending(kyc);
        sellerKycRepository.save(kyc);
        return url;
    }

    @Transactional
    public void updateBankDetails(UUID userId, SellerKycDto.UpdateBankDetailsRequest request) {
        SellerKyc kyc = getKycByUserId(userId);
        
        kyc.setBankAccountHolderName(request.getBankAccountHolderName());
        kyc.setBankIfscCode(request.getBankIfscCode());
        kyc.setBankName(request.getBankName());
        kyc.setBankBranch(request.getBankBranch());
        kyc.setPayoutMethodPreference(request.getPayoutMethodPreference());
        
        // Mask account number
        String acc = request.getBankAccountNumber();
        if (acc.length() >= 4) {
            kyc.setBankAccountNumberMasked("****" + acc.substring(acc.length() - 4));
        } else {
            kyc.setBankAccountNumberMasked("****");
        }
        
        // Payout provider reference would be set here in a real scenario after calling Stripe/Razorpay
        kyc.setPayoutProviderReferenceId("MOCK_REF_" + UUID.randomUUID().toString().substring(0, 8));
        
        checkAndSetPending(kyc);
        sellerKycRepository.save(kyc);
    }

    private void checkAndSetPending(SellerKyc kyc) {
        // If it's already VERIFIED, we don't want to revert to PENDING automatically 
        // unless they changed something critical. For MVP, re-uploading always resets to PENDING if not already VERIFIED.
        if (kyc.getVerificationStatus() == SellerKyc.VerificationStatus.VERIFIED) {
            return;
        }

        boolean hasIec = kyc.getIecNumber() != null && kyc.getIecDocumentUrl() != null;
        boolean hasPan = kyc.getPanNumber() != null && kyc.getPanDocumentUrl() != null;
        boolean hasBank = kyc.getBankAccountNumberMasked() != null && kyc.getPayoutProviderReferenceId() != null;

        if (hasIec && hasPan && hasBank) {
            kyc.setVerificationStatus(SellerKyc.VerificationStatus.PENDING);
            kyc.setSubmittedAt(LocalDateTime.now());
            kyc.setRejectionReason(null); // Clear reason on re-submission
            log.info("Seller KYC status updated to PENDING for seller: {}", kyc.getSeller().getId());
        }
    }

    private SellerKyc getKycByUserId(UUID userId) {
        SellerProfile profile = sellerProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));
        
        return sellerKycRepository.findBySellerId(profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("SellerKyc", profile.getId().toString()));
    }

    private SellerKycDto.SellerKycStatusResponse mapToStatusResponse(SellerKyc kyc) {
        SellerKycDto.SellerKycStatusResponse response = new SellerKycDto.SellerKycStatusResponse();
        response.setVerificationStatus(kyc.getVerificationStatus());
        response.setRejectionReason(kyc.getRejectionReason());
        response.setIecUploaded(kyc.getIecDocumentUrl() != null);
        response.setPanUploaded(kyc.getPanDocumentUrl() != null);
        response.setGstinUploaded(kyc.getGstinDocumentUrl() != null);
        response.setBankDetailsUploaded(kyc.getPayoutProviderReferenceId() != null);
        response.setSubmittedAt(kyc.getSubmittedAt());
        response.setVerifiedAt(kyc.getVerifiedAt());
        return response;
    }
}
