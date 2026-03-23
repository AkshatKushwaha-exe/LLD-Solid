package com.example.payments;

// Unified contract for all payment providers
public interface PaymentGateway {

    /**
     * Initiate a payment for the given customer.
     *
     * @param custId        unique identifier of the customer
     * @param totalCents    charge amount expressed in cents
     * @return a provider-specific transaction reference
     */
    String processPayment(String custId, int totalCents);
}
