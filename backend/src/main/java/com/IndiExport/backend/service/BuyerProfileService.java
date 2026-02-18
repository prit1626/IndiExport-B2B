package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.BuyerProfileDto;
import com.IndiExport.backend.entity.BuyerProfile;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuyerProfileService {

    private final BuyerProfileRepository buyerProfileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public BuyerProfileDto.BuyerProfileResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
        
        BuyerProfile profile = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", userId.toString()));
        
        return mapToResponse(user, profile);
    }

    @Transactional
    public BuyerProfileDto.BuyerProfileResponse updateProfile(UUID userId, BuyerProfileDto.UpdateBuyerProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
        
        BuyerProfile profile = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyerProfile", userId.toString()));

        // Update User info
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        userRepository.save(user);

        // Update BuyerProfile info
        profile.setCompanyName(request.getCompanyName());
        profile.setCountry(request.getCountry());
        profile.setState(request.getState());
        profile.setCity(request.getCity());
        profile.setAddress(request.getAddress());
        profile.setPostalCode(request.getPostalCode());
        profile.setPreferredCurrency(request.getPreferredCurrency());
        buyerProfileRepository.save(profile);

        return mapToResponse(user, profile);
    }

    @Transactional
    public String uploadPhoto(UUID userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        fileStorageService.validateFile(file, new String[]{"image/png", "image/jpeg", "image/jpg"}, 2);
        
        String photoUrl = fileStorageService.uploadFile(file, "profile/" + user.getId() + "/photo");
        user.setProfilePictureUrl(photoUrl);
        userRepository.save(user);
        
        return photoUrl;
    }

    private BuyerProfileDto.BuyerProfileResponse mapToResponse(User user, BuyerProfile profile) {
        return BuyerProfileDto.BuyerProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .profilePictureUrl(user.getProfilePictureUrl())
                .companyName(profile.getCompanyName())
                .country(profile.getCountry())
                .state(profile.getState())
                .city(profile.getCity())
                .address(profile.getAddress())
                .postalCode(profile.getPostalCode())
                .preferredCurrency(profile.getPreferredCurrency())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
