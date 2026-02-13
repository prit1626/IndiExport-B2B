package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.SellerProfileDto;
import com.IndiExport.backend.entity.Category;
import com.IndiExport.backend.entity.SellerProfile;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.CategoryRepository;
import com.IndiExport.backend.repository.SellerProfileRepository;
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
    private final FileStorageService fileStorageService;

    public SellerProfileService(SellerProfileRepository sellerProfileRepository,
                                 CategoryRepository categoryRepository,
                                 FileStorageService fileStorageService) {
        this.sellerProfileRepository = sellerProfileRepository;
        this.categoryRepository = categoryRepository;
        this.fileStorageService = fileStorageService;
    }

    public SellerProfileDto.SellerProfileResponse getProfile(UUID userId) {
        SellerProfile profile = sellerProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResourceNotFoundException("SellerProfile", userId.toString()));
        
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
        return response;
    }
}
