package com.IndiExport.backend.service.payment.provider;

import com.IndiExport.backend.dto.PaymentResponse;
import com.IndiExport.backend.dto.PaymentVerificationRequest;
import com.IndiExport.backend.dto.RazorpayOrderResponse;
import com.IndiExport.backend.entity.Order;
import com.IndiExport.backend.exception.PaymentGatewayException;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RazorpayPaymentProvider implements PaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(RazorpayPaymentProvider.class);

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    private RazorpayClient getClient() throws RazorpayException {
        return new RazorpayClient(keyId, keySecret);
    }

    @Override
    public RazorpayOrderResponse createPayment(Order order) {
        try {
            RazorpayClient client = getClient();
            
            // Amount in paise (multiply by 100 if storing in main unit, or use directly if already in paise/minor)
            // Assuming order.getTotalAmountInrPaise() returns amount in paise
            long amountPaise = order.getTotalAmountPaise();

            JSONObject options = new JSONObject();
            options.put("amount", amountPaise);
            options.put("currency", "INR");
            options.put("receipt", order.getOrderNumber());
            
            Map<String, String> notes = new HashMap<>();
            notes.put("order_id", order.getId().toString());
            notes.put("buyer_email", order.getBuyer().getUser().getEmail());
            options.put("notes", new JSONObject(notes));

            com.razorpay.Order razorpayOrder = client.orders.create(options);

            return RazorpayOrderResponse.builder()
                    .razorpay(PaymentResponse.builder()
                            .razorpayOrderId(razorpayOrder.get("id"))
                            .key(keyId)
                            .amountMinor(amountPaise)
                            .currency("INR")
                            .buyerName(order.getBuyer().getUser().getFirstName() + " " + order.getBuyer().getUser().getLastName())
                            .buyerEmail(order.getBuyer().getUser().getEmail())
                            .buyerPhone(order.getBuyer().getUser().getPhoneNumber())
                            .notes(notes)
                            .build())
                    .build();

        } catch (RazorpayException e) {
            log.error("Razorpay Order Creation Failed: {}", e.getMessage());
            throw new PaymentGatewayException("RAZORPAY", "Failed to create order: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyPayment(PaymentVerificationRequest request) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", request.getRazorpayOrderId());
            options.put("razorpay_payment_id", request.getRazorpayPaymentId());
            options.put("razorpay_signature", request.getRazorpaySignature());

            return Utils.verifyPaymentSignature(options, keySecret);
        } catch (RazorpayException e) {
            log.error("Razorpay Signature Verification Failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public RazorpayOrderResponse createPlanUpgradePayment(com.IndiExport.backend.entity.SellerProfile seller, long amountPaise) {
        try {
            RazorpayClient client = getClient();
            
            JSONObject options = new JSONObject();
            options.put("amount", amountPaise);
            options.put("currency", "INR");
            options.put("receipt", "PLAN_UPGRADE_" + seller.getId().toString().substring(0, 8));
            
            Map<String, String> notes = new HashMap<>();
            notes.put("seller_id", seller.getId().toString());
            notes.put("type", "PLAN_UPGRADE");
            notes.put("target_plan", "ADVANCED_SELLER");
            options.put("notes", new JSONObject(notes));

            com.razorpay.Order razorpayOrder = client.orders.create(options);

            return RazorpayOrderResponse.builder()
                    .razorpay(PaymentResponse.builder()
                            .razorpayOrderId(razorpayOrder.get("id"))
                            .key(keyId)
                            .amountMinor(amountPaise)
                            .currency("INR")
                            .buyerName(seller.getUser().getFirstName() + " " + seller.getUser().getLastName())
                            .buyerEmail(seller.getUser().getEmail())
                            .buyerPhone(seller.getUser().getPhoneNumber())
                            .notes(notes)
                            .build())
                    .build();

        } catch (RazorpayException e) {
            log.error("Razorpay Plan Upgrade Order Creation Failed: {}", e.getMessage());
            throw new PaymentGatewayException("RAZORPAY", "Failed to create plan upgrade order: " + e.getMessage());
        }
    }
    @Override
    public RazorpayOrderResponse createRefundPayment(com.IndiExport.backend.entity.Dispute dispute, long amountPaise) {
        try {
            RazorpayClient client = getClient();
            
            JSONObject options = new JSONObject();
            options.put("amount", amountPaise);
            options.put("currency", "INR");
            options.put("receipt", "REFUND_" + dispute.getId().toString().substring(0, 8));
            
            Map<String, String> notes = new HashMap<>();
            notes.put("dispute_id", dispute.getId().toString());
            notes.put("type", "DISPUTE_REFUND");
            notes.put("order_id", dispute.getOrder().getId().toString());
            options.put("notes", new JSONObject(notes));

            com.razorpay.Order razorpayOrder = client.orders.create(options);
            
            // Assume the seller pays the refund
            com.IndiExport.backend.entity.User sellerUser = dispute.getOrder().getSeller().getUser();

            return RazorpayOrderResponse.builder()
                    .razorpay(PaymentResponse.builder()
                            .razorpayOrderId(razorpayOrder.get("id"))
                            .key(keyId)
                            .amountMinor(amountPaise)
                            .currency("INR")
                            .buyerName(sellerUser.getFirstName() + " " + sellerUser.getLastName())
                            .buyerEmail(sellerUser.getEmail())
                            .buyerPhone(sellerUser.getPhoneNumber())
                            .notes(notes)
                            .build())
                    .build();

        } catch (RazorpayException e) {
            log.error("Razorpay Refund Order Creation Failed: {}", e.getMessage());
            throw new PaymentGatewayException("RAZORPAY", "Failed to create refund order: " + e.getMessage());
        }
    }
}
