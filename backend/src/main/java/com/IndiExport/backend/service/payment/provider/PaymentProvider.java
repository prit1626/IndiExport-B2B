package com.IndiExport.backend.service.payment.provider;

import com.IndiExport.backend.dto.RazorpayOrderResponse;
import com.IndiExport.backend.dto.PaymentVerificationRequest;
import com.IndiExport.backend.entity.Order;

/**
 * Interface for payment provider abstraction.
 */
public interface PaymentProvider {
    /**
     * Create a payment order with the provider.
     */
    RazorpayOrderResponse createPayment(Order order);

    /**
     * Verify payment signature from the provider.
     */
    boolean verifyPayment(PaymentVerificationRequest request);

    /**
     * Create a payment order for plan upgrade.
     */
    RazorpayOrderResponse createPlanUpgradePayment(com.IndiExport.backend.entity.SellerProfile seller, long amountPaise);
}
