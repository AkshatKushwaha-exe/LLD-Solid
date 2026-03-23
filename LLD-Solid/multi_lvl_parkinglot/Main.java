package multi_lvl_parkinglot;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<SlotType, Double> hourlyRates = new HashMap<>();
        hourlyRates.put(SlotType.SMALL, 5.0);
        hourlyRates.put(SlotType.MEDIUM, 10.0);
        hourlyRates.put(SlotType.LARGE, 20.0);

        PricingStrategy pricing = new HourlyPricingStrategy(hourlyRates);
        SlotAssignmentStrategy slotFinder = new NearestEuclideanSlotStrategy();

        ParkingLot lot = new ParkingLot(slotFinder, pricing);

        Gate mainGate = new Gate("G1", 1, 0, 0);
        lot.addGate(mainGate);

        lot.addSlot(new Slot("S1", SlotType.MEDIUM, 1, 10, 10));
        lot.addSlot(new Slot("S2", SlotType.MEDIUM, 1, 50, 50));
        lot.addSlot(new Slot("S3", SlotType.SMALL, 2, 5, 5));

        lot.displayStatus(null);

        Vehicle car = new Vehicle("ABC-123", VehicleType.CAR);
        Ticket ticket = lot.parkVehicle(car, mainGate);

        if (ticket != null) {
            long simulatedEntry = System.currentTimeMillis() - (2 * 60 * 60 * 1000) - 1000;
            ticket.setEntryTimestamp(simulatedEntry);
            lot.processExit(ticket);
        }

        lot.displayStatus(null);
    }
}
