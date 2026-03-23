package com.example.payments;

import java.util.Map;
import java.util.Objects;

/**
 * High-level service that routes order payments to the correct
 * provider through the PaymentGateway abstraction.
 */
public class OrderService {

    private final Map<String, PaymentGateway> gatewayMap;

    public OrderService(Map<String, PaymentGateway> gatewayMap) {
        this.gatewayMap = Objects.requireNonNull(gatewayMap, "Gateway map is required");
    }

    /**
     * Process an order by delegating to the named payment provider.
     *
     * @param provider      key identifying the payment gateway
     * @param custId        customer placing the order
     * @param totalCents    order total in cents
     * @return transaction reference from the underlying gateway
     */
    public String processOrder(String provider, String custId, int totalCents) {
        Objects.requireNonNull(provider, "Provider name is required");

        PaymentGateway gw = gatewayMap.get(provider);
        if (gw == null) {
            throw new IllegalArgumentException("No gateway registered for provider: " + provider);
        }
        return gw.processPayment(custId, totalCents);
    }
}
