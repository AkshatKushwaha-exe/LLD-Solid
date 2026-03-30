import java.util.UUID;

public class Payment {
    private String paymentId;
    private double amount;
    private PaymentStatus status;

    public Payment(double amount) {
        this.paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 6);
        this.amount = amount;
        this.status = PaymentStatus.SUCCESS;
    }

    public String getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }

    public void refund() {
        this.status = PaymentStatus.REFUNDED;
        System.out.println("Refund of Rs." + amount + " processed for payment " + paymentId);
    }
}
