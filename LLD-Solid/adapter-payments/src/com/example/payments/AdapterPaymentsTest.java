package com.example.payments;

import java.util.HashMap;
import java.util.Map;

/**
 * Self-contained test suite for the adapter-payments project.
 * Uses plain assertions and try-catch (no JUnit dependency).
 *
 * Run:  javac com/example/payments/*.java
 *       java  com.example.payments.AdapterPaymentsTest
 */
public class AdapterPaymentsTest {

    private static int passed = 0;
    private static int failed = 0;

    // ───────────────────────── helpers ─────────────────────────

    private static void pass(String name) {
        passed++;
        System.out.println("  PASS  " + name);
    }

    private static void fail(String name, String reason) {
        failed++;
        System.out.println("  FAIL  " + name + "  -->  " + reason);
    }

    /** Assert two objects are equal. */
    private static void assertEqual(String testName, Object expected, Object actual) {
        if (expected == null ? actual == null : expected.equals(actual)) {
            pass(testName);
        } else {
            fail(testName, "expected <" + expected + "> but got <" + actual + ">");
        }
    }

    /** Assert that a boolean condition holds. */
    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            pass(testName);
        } else {
            fail(testName, "condition was false");
        }
    }

    /** Assert that running the given code throws the expected exception type. */
    private static void assertThrows(String testName,
                                     Class<? extends Throwable> expectedType,
                                     Runnable code) {
        try {
            code.run();
            fail(testName, "expected " + expectedType.getSimpleName() + " but nothing was thrown");
        } catch (Throwable t) {
            if (expectedType.isInstance(t)) {
                pass(testName);
            } else {
                fail(testName, "expected " + expectedType.getSimpleName()
                        + " but got " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
    }

    // ───────────────────────── tests ──────────────────────────

    /** Test 1: FastPayAdapter returns correct format "FP#custId:amount" */
    private static void testFastPayAdapterCorrectFormat() {
        FastPayAdapter adapter = new FastPayAdapter(new FastPayClient());
        String result = adapter.processPayment("cust-42", 1500);
        assertEqual("1. FastPayAdapter returns correct format",
                "FP#cust-42:1500", result);
    }

    /** Test 2: SafeCashAdapter returns correct format "SC#pay(user,amount)" */
    private static void testSafeCashAdapterCorrectFormat() {
        SafeCashAdapter adapter = new SafeCashAdapter(new SafeCashClient());
        String result = adapter.processPayment("cust-99", 2500);
        assertEqual("2. SafeCashAdapter returns correct format",
                "SC#pay(cust-99,2500)", result);
    }

    /** Test 3: FastPayAdapter with null customerId throws NullPointerException */
    private static void testFastPayAdapterNullCustomerId() {
        FastPayAdapter adapter = new FastPayAdapter(new FastPayClient());
        assertThrows("3. FastPayAdapter null customerId throws NPE",
                NullPointerException.class,
                () -> adapter.processPayment(null, 1000));
    }

    /** Test 4: SafeCashAdapter with null customerId throws NullPointerException */
    private static void testSafeCashAdapterNullCustomerId() {
        SafeCashAdapter adapter = new SafeCashAdapter(new SafeCashClient());
        assertThrows("4. SafeCashAdapter null customerId throws NPE",
                NullPointerException.class,
                () -> adapter.processPayment(null, 1000));
    }

    /** Test 5: FastPayAdapter constructor with null client throws NullPointerException */
    private static void testFastPayAdapterNullClient() {
        assertThrows("5. FastPayAdapter null client throws NPE",
                NullPointerException.class,
                () -> new FastPayAdapter(null));
    }

    /** Test 6: SafeCashAdapter constructor with null client throws NullPointerException */
    private static void testSafeCashAdapterNullClient() {
        assertThrows("6. SafeCashAdapter null client throws NPE",
                NullPointerException.class,
                () -> new SafeCashAdapter(null));
    }

    /** Test 7: OrderService with valid fastpay provider works */
    private static void testOrderServiceFastPay() {
        Map<String, PaymentGateway> gateways = new HashMap<>();
        gateways.put("fastpay", new FastPayAdapter(new FastPayClient()));
        gateways.put("safecash", new SafeCashAdapter(new SafeCashClient()));
        OrderService service = new OrderService(gateways);

        String result = service.processOrder("fastpay", "cust-1", 1299);
        assertEqual("7. OrderService fastpay provider works",
                "FP#cust-1:1299", result);
    }

    /** Test 8: OrderService with valid safecash provider works */
    private static void testOrderServiceSafeCash() {
        Map<String, PaymentGateway> gateways = new HashMap<>();
        gateways.put("fastpay", new FastPayAdapter(new FastPayClient()));
        gateways.put("safecash", new SafeCashAdapter(new SafeCashClient()));
        OrderService service = new OrderService(gateways);

        String result = service.processOrder("safecash", "cust-2", 1299);
        assertEqual("8. OrderService safecash provider works",
                "SC#pay(cust-2,1299)", result);
    }

    /** Test 9: OrderService with unknown provider throws IllegalArgumentException */
    private static void testOrderServiceUnknownProvider() {
        Map<String, PaymentGateway> gateways = new HashMap<>();
        gateways.put("fastpay", new FastPayAdapter(new FastPayClient()));
        OrderService service = new OrderService(gateways);

        assertThrows("9. OrderService unknown provider throws IAE",
                IllegalArgumentException.class,
                () -> service.processOrder("bitcoin", "cust-1", 500));
    }

    /** Test 10: OrderService with null provider throws NullPointerException */
    private static void testOrderServiceNullProvider() {
        Map<String, PaymentGateway> gateways = new HashMap<>();
        gateways.put("fastpay", new FastPayAdapter(new FastPayClient()));
        OrderService service = new OrderService(gateways);

        assertThrows("10. OrderService null provider throws NPE",
                NullPointerException.class,
                () -> service.processOrder(null, "cust-1", 500));
    }

    /** Test 11: Both adapters implement PaymentGateway (instanceof check) */
    private static void testAdaptersImplementPaymentGateway() {
        PaymentGateway fp = new FastPayAdapter(new FastPayClient());
        PaymentGateway sc = new SafeCashAdapter(new SafeCashClient());

        assertTrue("11a. FastPayAdapter instanceof PaymentGateway",
                fp instanceof PaymentGateway);
        assertTrue("11b. SafeCashAdapter instanceof PaymentGateway",
                sc instanceof PaymentGateway);
    }

    /** Test 12: OrderService works with multiple charges in sequence */
    private static void testOrderServiceSequentialCharges() {
        Map<String, PaymentGateway> gateways = new HashMap<>();
        gateways.put("fastpay", new FastPayAdapter(new FastPayClient()));
        gateways.put("safecash", new SafeCashAdapter(new SafeCashClient()));
        OrderService service = new OrderService(gateways);

        String r1 = service.processOrder("fastpay", "cust-A", 100);
        String r2 = service.processOrder("safecash", "cust-B", 200);
        String r3 = service.processOrder("fastpay", "cust-C", 300);
        String r4 = service.processOrder("safecash", "cust-D", 400);

        boolean allCorrect =
                "FP#cust-A:100".equals(r1)
             && "SC#pay(cust-B,200)".equals(r2)
             && "FP#cust-C:300".equals(r3)
             && "SC#pay(cust-D,400)".equals(r4);

        assertTrue("12. OrderService sequential charges all correct", allCorrect);
    }

    /** Test 13: Different amounts produce different results */
    private static void testDifferentAmountsProduceDifferentResults() {
        FastPayAdapter fp = new FastPayAdapter(new FastPayClient());
        String r1 = fp.processPayment("cust-1", 100);
        String r2 = fp.processPayment("cust-1", 200);

        assertTrue("13a. FastPay different amounts differ", !r1.equals(r2));

        SafeCashAdapter sc = new SafeCashAdapter(new SafeCashClient());
        String r3 = sc.processPayment("cust-1", 100);
        String r4 = sc.processPayment("cust-1", 200);

        assertTrue("13b. SafeCash different amounts differ", !r3.equals(r4));
    }

    /** Test 14: Zero amount works correctly */
    private static void testZeroAmount() {
        FastPayAdapter fp = new FastPayAdapter(new FastPayClient());
        assertEqual("14a. FastPay zero amount",
                "FP#cust-0:0", fp.processPayment("cust-0", 0));

        SafeCashAdapter sc = new SafeCashAdapter(new SafeCashClient());
        assertEqual("14b. SafeCash zero amount",
                "SC#pay(cust-0,0)", sc.processPayment("cust-0", 0));
    }

    /** Test 15: Large amount works correctly */
    private static void testLargeAmount() {
        int largeAmount = Integer.MAX_VALUE; // 2_147_483_647

        FastPayAdapter fp = new FastPayAdapter(new FastPayClient());
        assertEqual("15a. FastPay large amount",
                "FP#bigcust:" + largeAmount, fp.processPayment("bigcust", largeAmount));

        SafeCashAdapter sc = new SafeCashAdapter(new SafeCashClient());
        assertEqual("15b. SafeCash large amount",
                "SC#pay(bigcust," + largeAmount + ")", sc.processPayment("bigcust", largeAmount));
    }

    // ───────────────────────── runner ─────────────────────────

    public static void main(String[] args) {
        System.out.println("=== Adapter-Payments Test Suite ===\n");

        testFastPayAdapterCorrectFormat();       // 1
        testSafeCashAdapterCorrectFormat();      // 2
        testFastPayAdapterNullCustomerId();      // 3
        testSafeCashAdapterNullCustomerId();     // 4
        testFastPayAdapterNullClient();          // 5
        testSafeCashAdapterNullClient();         // 6
        testOrderServiceFastPay();               // 7
        testOrderServiceSafeCash();              // 8
        testOrderServiceUnknownProvider();       // 9
        testOrderServiceNullProvider();          // 10
        testAdaptersImplementPaymentGateway();   // 11
        testOrderServiceSequentialCharges();     // 12
        testDifferentAmountsProduceDifferentResults(); // 13
        testLargeAmount();                       // 15  (before 14 just for variety — order is fine)
        testZeroAmount();                        // 14

        int total = passed + failed;
        System.out.println("\n=== Summary ===");
        System.out.println("Total : " + total);
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Result: " + (failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED"));

        if (failed > 0) {
            System.exit(1);
        }
    }
}
