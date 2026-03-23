package multi_lvl_parkinglot;

public interface PricingStrategy {
    double computeFee(Ticket ticket, long exitTimestamp);
}
