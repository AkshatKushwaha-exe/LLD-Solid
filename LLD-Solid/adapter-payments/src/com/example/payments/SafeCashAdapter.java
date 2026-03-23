package com.example.payments;

import java.util.Objects;

/**
 * Adapts SafeCashClient to the PaymentGateway interface.
 * Handles the two-step create-then-confirm flow internally.
 */
public class SafeCashAdapter implements PaymentGateway {

    private final SafeCashClient client;

    public SafeCashAdapter(SafeCashClient client) {
        this.client = Objects.requireNonNull(client, "SafeCashClient instance is required");
    }

    @Override
    public String processPayment(String custId, int totalCents) {
        Objects.requireNonNull(custId, "Customer id cannot be null");
        SafeCashPayment payment = client.createPayment(totalCents, custId);
        return payment.confirm();
    }
}
