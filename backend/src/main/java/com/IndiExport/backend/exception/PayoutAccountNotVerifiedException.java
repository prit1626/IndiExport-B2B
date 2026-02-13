package com.IndiExport.backend.exception;

public class PayoutAccountNotVerifiedException extends ApiException {
    public PayoutAccountNotVerifiedException(String sellerId) {
        super("PAYOUT_ACCOUNT_NOT_VERIFIED",
              "Seller payout account is not verified: " + sellerId, 400);
    }
}
