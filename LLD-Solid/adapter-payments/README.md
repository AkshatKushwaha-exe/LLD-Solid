# Adapter Pattern -- Payment Gateway Integration

## What This Shows
Two incompatible payment SDKs (`FastPayClient` and `SafeCashClient`) are wrapped
behind a single `PaymentGateway` interface using the **Adapter** pattern.
`OrderService` never touches the vendor SDKs directly; it programs only against
the common interface.

## Design Highlights
| Element | Role |
|---|---|
| `PaymentGateway` | Target interface -- one method: `processPayment` |
| `FastPayAdapter` | Converts `FastPayClient.payNow` to the target interface |
| `SafeCashAdapter` | Converts the two-step `createPayment` / `confirm` flow |
| `OrderService` | Routes orders via a `Map<String, PaymentGateway>` registry (O(1) lookup) |

Adding a new payment provider requires only a new adapter class and a single
`put` call in the registry -- `OrderService` stays untouched.

## Build & Run
```bash
cd adapter-payments/src
javac com/example/payments/*.java
java com.example.payments.App
```

### Expected Output
```
FP#cust-1:1299
SC#pay(cust-2,1299)
```
