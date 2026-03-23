package com.example.payments;

/**
 * Third-party FastPay SDK stub.
 * In production this would be provided as an external dependency.
 */
public class FastPayClient {

    public String payNow(String customerIdentifier, int cents) {
        // Simulate a transaction and return a reference id
        return "FP#" + customerIdentifier + ":" + cents;
    }
}
