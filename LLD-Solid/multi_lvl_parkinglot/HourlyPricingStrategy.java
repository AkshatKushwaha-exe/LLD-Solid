package multi_lvl_parkinglot;

import java.util.Map;

public class HourlyPricingStrategy implements PricingStrategy {

    private final Map<SlotType, Double> ratePerHour;

    public HourlyPricingStrategy(Map<SlotType, Double> ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    @Override
    public double computeFee(Ticket ticket, long exitTimestamp) {
        long durationMs = exitTimestamp - ticket.getEntryTimestamp();
        double totalHours = Math.ceil(durationMs / (1000.0 * 60 * 60));
        if (totalHours == 0) totalHours = 1;

        double hourlyRate = ratePerHour.getOrDefault(ticket.getSlot().getSlotType(), 10.0);
        return totalHours * hourlyRate;
    }
}
