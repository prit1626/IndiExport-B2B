package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.SellerProfileDto;
import com.IndiExport.backend.entity.Category;
import com.IndiExport.backend.entity.SellerProfile;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.CategoryRepository;
import com.IndiExport.backend.repository.SellerProfileRepository;
import com.IndiExport.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class SellerProfileService {

    private static final Logger log = LoggerFactory.getLogger(SellerProfileService.class);

    private final SellerProfileRepository sellerProfileRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public SellerProfileService(SellerProfileRepository sellerProfileRepository,
                                 CategoryRepository categoryRepository,
                                 UserRepository userRepository,
                                 FileStorageService fileStorageService) {
        this.sellerProfileRepository = sellerProfileRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public SellerProfileDto.SellerProfileResponse getProfile(UUID userId) {
        SellerProfile profile = sellerProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));
        
        return mapToResponse(profile);
    }

    @Transactional(readOnly = true)
    public SellerProfileDto.SellerProfileResponse getPublicProfile(UUID sellerProfileId) {
        SellerProfile profile = sellerProfileRepository.findById(sellerProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", sellerProfileId.toString()));
        
        return mapToResponse(profile);
    }

    @Transactional
    public SellerProfileDto.SellerProfileResponse updateProfile(UUID userId, SellerProfileDto.UpdateSellerProfileRequest request) {
        SellerProfile profile = sellerProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));

        profile.setCompanyName(request.getCompanyName());
        profile.setWebsite(request.getWebsite());
        profile.setBusinessEmail(request.getBusinessEmail());
        profile.setBusinessPhone(request.getBusinessPhone());
        profile.setAddress(request.getAddress());
        profile.setCity(request.getCity());
        profile.setState(request.getState());
        profile.setPostalCode(request.getPostalCode());

        // Update User info if email/name allowed (User.java fields)
        User user = profile.getUser();
        // user.setEmail(request.getBusinessEmail()); // Usually email shouldn't change without verification
        userRepository.save(user);

        if (request.getCategoryIds() != null) {
            List<Category> categories = categoryRepository.findAllById(request.getCategoryIds());
            profile.setExportCategories(new HashSet<>(categories));
        }

        return mapToResponse(sellerProfileRepository.save(profile));
    }

    @Transactional
    public String uploadLogo(UUID userId, MultipartFile logo) throws IOException {
        SellerProfile profile = sellerProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));

        fileStorageService.validateFile(logo, new String[]{"image/png", "image/jpeg", "image/jpg"}, 2);
        
        String logoUrl = fileStorageService.uploadFile(logo, "sellers/" + profile.getId() + "/logo");
        profile.setCompanyLogoUrl(logoUrl);
        sellerProfileRepository.save(profile);
        
        return logoUrl;
    }

    private SellerProfileDto.SellerProfileResponse mapToResponse(SellerProfile profile) {
        SellerProfileDto.SellerProfileResponse response = new SellerProfileDto.SellerProfileResponse();
        response.setId(profile.getId());
        response.setCompanyName(profile.getCompanyName());
        response.setCompanyLogoUrl(profile.getCompanyLogoUrl());
        response.setWebsite(profile.getWebsite());
        response.setBusinessEmail(profile.getBusinessEmail());
        response.setBusinessPhone(profile.getBusinessPhone());
        response.setAddress(profile.getAddress());
        response.setCity(profile.getCity());
        response.setState(profile.getState());
        response.setCountry(profile.getCountry());
        response.setPostalCode(profile.getPostalCode());
        response.setAverageRatingMilli(profile.getAverageRatingMilli());
        response.setTotalProducts(profile.getTotalProducts());
        response.setActiveProducts(profile.getActiveProducts());
        response.setTotalSalesPaise(profile.getTotalSalesPaise());
        response.setCreatedAt(profile.getCreatedAt());

        // Attach KYC status if available
        if (profile.getKyc() != null) {
            response.setIecNumber(profile.getKyc().getIecNumber());
            response.setIecStatus(profile.getKyc().getVerificationStatus().name());
            response.setGstin(profile.getKyc().getGstinNumber());
            response.setPanNumber(profile.getKyc().getPanNumber());
            response.setVerificationSubmittedAt(profile.getKyc().getSubmittedAt());
            
            // Masked account info
            response.setPayoutMethod(profile.getKyc().getPayoutMethodPreference());
            response.setAccountHolderName(profile.getKyc().getBankAccountHolderName());
            response.setAccountNumberMasked(profile.getKyc().getBankAccountNumberMasked());
            response.setIfscMasked(profile.getKyc().getBankIfscCode() != null ? "****" + profile.getKyc().getBankIfscCode().substring(Math.max(0, profile.getKyc().getBankIfscCode().length() - 4)) : null);
        }

        // Attach Plan info
        if (profile.getSellerPlan() != null) {
            response.setCurrentPlan(profile.getSellerPlan().getPlanType().name());
        } else {
            response.setCurrentPlan("BASIC_SELLER"); // Fallback
        }

        return response;
    }
}
