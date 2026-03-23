package com.example.payments;

/**
 * Value object returned by the SafeCash SDK representing
 * a pending transaction that must be explicitly confirmed.
 */
public class SafeCashPayment {

    private final int chargeAmount;
    private final String userId;

    public SafeCashPayment(int chargeAmount, String userId) {
        this.chargeAmount = chargeAmount;
        this.userId = userId;
    }

    /** Finalize the transaction and return a receipt string. */
    public String confirm() {
        return "SC#pay(" + userId + "," + chargeAmount + ")";
    }
}
