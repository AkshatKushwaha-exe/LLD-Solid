package com.example.payments;

/**
 * Third-party SafeCash SDK stub.
 * Real implementation would call an external HTTP service.
 */
public class SafeCashClient {

    public SafeCashPayment createPayment(int chargeAmount, String userId) {
        return new SafeCashPayment(chargeAmount, userId);
    }
}
