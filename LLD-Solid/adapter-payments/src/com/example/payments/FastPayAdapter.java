package com.example.payments;

import java.util.Objects;

/**
 * Bridges the FastPay SDK into our common PaymentGateway contract.
 */
public class FastPayAdapter implements PaymentGateway {

    private final FastPayClient client;

    public FastPayAdapter(FastPayClient client) {
        this.client = Objects.requireNonNull(client, "FastPayClient instance is required");
    }

    @Override
    public String processPayment(String custId, int totalCents) {
        Objects.requireNonNull(custId, "Customer id cannot be null");
        return client.payNow(custId, totalCents);
    }
}
