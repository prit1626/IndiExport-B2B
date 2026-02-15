package com.IndiExport.backend.service.analytics;

import com.IndiExport.backend.dto.analytics.*;
import com.IndiExport.backend.entity.Product;
import com.IndiExport.backend.entity.ProductView;
import com.IndiExport.backend.entity.SellerPlan;
import com.IndiExport.backend.entity.SellerProfile;
import com.IndiExport.backend.exception.AnalyticsAccessDeniedException;
import com.IndiExport.backend.exception.InvalidDateRangeException;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.repository.BuyerProfileRepository;
import com.IndiExport.backend.repository.ProductRepository;
import com.IndiExport.backend.repository.ProductViewRepository;
import com.IndiExport.backend.repository.SellerProfileRepository;
import com.IndiExport.backend.repository.analytics.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final BuyerProfileRepository buyerProfileRepository;
    private final ProductViewRepository productViewRepository;
    private final ProductRepository productRepository;

    // Maximum allowed date range for standard queries (e.g., 1 year)
    private static final long MAX_DAYS_RANGE = 365;

    @Transactional(readOnly = true)
    public BuyerDashboardAnalyticsResponse getBuyerAnalytics(UUID userId, Instant from, Instant to) {
        validateDateRange(from, to);
        
        var buyerProfile = buyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer profile not found"));
        
        return analyticsRepository.getBuyerAnalytics(buyerProfile.getId(), from, to);
    }

    @Transactional(readOnly = true)
    public SellerDashboardAnalyticsResponse getSellerAnalytics(UUID userId, Instant from, Instant to) {
        validateDateRange(from, to);
        
        var sellerProfile = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));
        
        return analyticsRepository.getSellerAnalytics(sellerProfile.getId(), from, to);
    }

    @Transactional(readOnly = true)
    public AdminDashboardAnalyticsResponse getAdminAnalytics(Instant from, Instant to) {
        validateDateRange(from, to);
        // Admin has global view, no profile check needed
        return analyticsRepository.getAdminAnalytics(from, to);
    }

    @Transactional(readOnly = true)
    public AdvancedSellerAnalyticsResponse getAdvancedSellerAnalytics(UUID userId, Instant from, Instant to) {
        validateDateRange(from, to);
        
        var sellerProfile = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found"));
        
        // Check for ADVANCED_SELLER plan
        SellerPlan plan = sellerProfile.getSellerPlan();
        if (plan == null || !plan.getIsActive() || plan.getPlanType() != SellerPlan.PlanType.ADVANCED_SELLER) {
            throw new AnalyticsAccessDeniedException("This feature requires an ADVANCED_SELLER plan.");
        }
        
        return analyticsRepository.getAdvancedSellerAnalytics(sellerProfile.getId(), from, to);
    }
    
    @Transactional
    public void recordProductView(UUID productId, UUID buyerId, String country) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                
        ProductView view = ProductView.builder()
                .product(product)
                .buyerId(buyerId) // Nullable
                .country(country != null ? country : "XX")
                .viewedAt(Instant.now())
                .build();
                
        productViewRepository.save(view);
    }

    private void validateDateRange(Instant from, Instant to) {
        if (from == null || to == null) {
            throw new InvalidDateRangeException("Date range parameters 'from' and 'to' are required.");
        }
        if (from.isAfter(to)) {
            throw new InvalidDateRangeException("'from' date cannot be after 'to' date.");
        }
        long days = ChronoUnit.DAYS.between(from, to);
        if (days > MAX_DAYS_RANGE) {
            throw new InvalidDateRangeException("Date range cannot exceed " + MAX_DAYS_RANGE + " days.");
        }
    }
}
