package com.IndiExport.backend.service.payment;

import com.IndiExport.backend.exception.PaymentGatewayException;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RazorpayPaymentProvider {

    private static final Logger log = LoggerFactory.getLogger(RazorpayPaymentProvider.class);

    @Value("${razorpay.key-id}")
    private String keyId;

    @Value("${razorpay.key-secret}")
    private String keySecret;

    private RazorpayClient getClient() throws RazorpayException {
        return new RazorpayClient(keyId, keySecret);
    }

    public Order createOrder(long amountMinor, String currency, Map<String, String> notes) {
        try {
            RazorpayClient client = getClient();
            JSONObject options = new JSONObject();
            options.put("amount", amountMinor);
            options.put("currency", currency);
            options.put("notes", new JSONObject(notes));
            options.put("payment_capture", 1); // Auto capture

            return client.orders.create(options);
        } catch (RazorpayException e) {
            log.error("Razorpay Order Creation Failed: {}", e.getMessage());
            throw new PaymentGatewayException("RAZORPAY", "Failed to create order: " + e.getMessage());
        }
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            return Utils.verifyPaymentSignature(options, keySecret);
        } catch (RazorpayException e) {
            log.error("Razorpay Signature Verification Failed: {}", e.getMessage());
            return false;
        }
    }

    public String getKeyId() {
        return keyId;
    }
}
