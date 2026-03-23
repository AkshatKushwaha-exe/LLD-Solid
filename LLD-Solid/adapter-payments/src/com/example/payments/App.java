package com.example.payments;

import java.util.HashMap;
import java.util.Map;

/** Quick demo wiring up two payment adapters and processing sample orders. */
public class App {

    public static void main(String[] args) {

        // Build a registry of available gateways
        Map<String, PaymentGateway> gateways = new HashMap<>();
        gateways.put("fastpay",  new FastPayAdapter(new FastPayClient()));
        gateways.put("safecash", new SafeCashAdapter(new SafeCashClient()));

        OrderService orders = new OrderService(gateways);

        // Run two sample transactions
        String ref1 = orders.processOrder("fastpay",  "cust-1", 1299);
        String ref2 = orders.processOrder("safecash", "cust-2", 1299);

        System.out.println(ref1);
        System.out.println(ref2);
    }
}
