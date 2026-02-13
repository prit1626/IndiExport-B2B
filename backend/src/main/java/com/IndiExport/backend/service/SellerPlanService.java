package com.IndiExport.backend.service;

import com.IndiExport.backend.entity.Product;
import com.IndiExport.backend.entity.SellerPlan;
import com.IndiExport.backend.exception.ProductExceptions;
import com.IndiExport.backend.repository.ProductRepository;
import com.IndiExport.backend.repository.SellerPlanRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SellerPlanService {

    private static final Logger log = LoggerFactory.getLogger(SellerPlanService.class);

    private final SellerPlanRepository sellerPlanRepository;
    private final ProductRepository productRepository;

    public SellerPlanService(SellerPlanRepository sellerPlanRepository, ProductRepository productRepository) {
        this.sellerPlanRepository = sellerPlanRepository;
        this.productRepository = productRepository;
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
}
