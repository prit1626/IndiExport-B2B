package com.IndiExport.backend.service;

import com.IndiExport.backend.entity.Product;
import com.IndiExport.backend.entity.SellerPlan;
import com.IndiExport.backend.entity.SellerProfile;
import com.IndiExport.backend.exception.ProductExceptions;
import com.IndiExport.backend.repository.ProductRepository;
import com.IndiExport.backend.repository.SellerPlanRepository;
import com.IndiExport.backend.repository.SellerProfileRepository;
import com.IndiExport.backend.service.admin.AdminSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SellerPlanService {

    private static final Logger log = LoggerFactory.getLogger(SellerPlanService.class);

    private final SellerPlanRepository sellerPlanRepository;
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final AdminSettingsService adminSettingsService;
    private final com.IndiExport.backend.service.payment.provider.PaymentProvider paymentProvider;


    public SellerPlanService(SellerPlanRepository sellerPlanRepository, 
                             ProductRepository productRepository,
                             SellerProfileRepository sellerProfileRepository,
                             AdminSettingsService adminSettingsService,
                             com.IndiExport.backend.service.payment.provider.PaymentProvider paymentProvider) {
        this.sellerPlanRepository = sellerPlanRepository;
        this.productRepository = productRepository;
        this.sellerProfileRepository = sellerProfileRepository;
        this.adminSettingsService = adminSettingsService;
        this.paymentProvider = paymentProvider;
    }

    /**
     * Checks if a seller can activate another product based on their current plan.
     * Throws ActiveProductLimitExceededException if limit is reached.
     */
    public void validateActiveProductLimit(UUID sellerId) {
        SellerPlan plan = sellerPlanRepository.findWithLockBySellerIdAndIsActiveTrue(sellerId)
                .orElseThrow(() -> new ProductExceptions.UnauthorizedProductAccessException("Seller has no active plan"));

        if (plan.getPlanType() == SellerPlan.PlanType.ADVANCED_SELLER) {
            return; // Unlimited
        }

        long activeCount = productRepository.countBySellerIdAndStatus(sellerId, Product.ProductStatus.ACTIVE);
        
        if (activeCount >= plan.getMaxActiveProducts()) {
            log.warn("Seller {} reached active product limit of {}", sellerId, plan.getMaxActiveProducts());
            throw new ProductExceptions.ActiveProductLimitExceededException(
                    "Active product limit reached (" + plan.getMaxActiveProducts() + "). Please upgrade your plan or deactivate other products."
            );
        }
    }

    public Integer getMaxActiveProducts(UUID sellerId) {
        return sellerPlanRepository.findBySellerIdAndIsActiveTrue(sellerId)
                .map(SellerPlan::getMaxActiveProducts)
                .orElse(0);
    }

    public com.IndiExport.backend.dto.RazorpayOrderResponse initiatePlanUpgrade(UUID userId) {
        SellerProfile seller = sellerProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new com.IndiExport.backend.exception.ResourceNotFoundException("SellerProfile", userId.toString()));

        SellerPlan currentPlan = sellerPlanRepository.findBySellerIdAndIsActiveTrue(seller.getId())
                .orElseThrow(() -> new ProductExceptions.UnauthorizedProductAccessException("No active plan found"));

        if (currentPlan.getPlanType() == SellerPlan.PlanType.ADVANCED_SELLER) {
            throw new com.IndiExport.backend.exception.BusinessRuleViolationException("You are already on the Advanced Plan");
        }

        // Create dummy order for Razorpay since PaymentProvider expects an Order
        // In a real system, we might want a separate Subscription endpoint or more generic payment provider
        // for now, we'll adapt to the existing PaymentProvider which expects Order
        // Actually, let's create a minimal Order-like structure or modify PaymentProvider to be more generic.
        // For simplicity, I'll just use a special ID or internal logic.
        
        // Wait, PaymentProvider.createPayment(Order order)
        // I should probably refactor PaymentProvider to take a generic PaymentRequest or similar if I want to reuse it.
        // But for this task, I'll create a "Plan Upgrade" order record.
        
        long amountPaise = adminSettingsService.getSettingsEntity().getAdvancedSellerPlanPriceInrPaise();
        return paymentProvider.createPlanUpgradePayment(seller, amountPaise);
    }

    @org.springframework.transaction.annotation.Transactional
    public void verifyPlanUpgrade(UUID userId, com.IndiExport.backend.dto.PaymentVerificationRequest request) {
        boolean isValid = paymentProvider.verifyPayment(request);
        if (!isValid) {
            throw new com.IndiExport.backend.exception.InvalidSignatureException("Invalid payment signature");
        }

        SellerProfile seller = sellerProfileRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new com.IndiExport.backend.exception.ResourceNotFoundException("SellerProfile", userId.toString()));

        SellerPlan plan = sellerPlanRepository.findBySellerIdAndIsActiveTrue(seller.getId())
                .orElseThrow(() -> new ProductExceptions.UnauthorizedProductAccessException("No active plan found"));

        plan.setPlanType(SellerPlan.PlanType.ADVANCED_SELLER);
        plan.setMaxActiveProducts(999999);
        plan.setUpdatedAt(java.time.LocalDateTime.now());
        
        sellerPlanRepository.save(plan);
        log.info("Seller {} upgraded to ADVANCED_SELLER", seller.getId());
    }
}
