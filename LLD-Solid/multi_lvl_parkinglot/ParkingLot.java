package multi_lvl_parkinglot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParkingLot {

    private final List<Slot> slots = new ArrayList<>();
    private final List<Gate> gates = new ArrayList<>();
    private final SlotAssignmentStrategy slotStrategy;
    private final PricingStrategy pricingStrategy;

    public ParkingLot(SlotAssignmentStrategy slotStrategy, PricingStrategy pricingStrategy) {
        this.slotStrategy = slotStrategy;
        this.pricingStrategy = pricingStrategy;
    }

    public void addSlot(Slot slot) { slots.add(slot); }
    public void addGate(Gate gate) { gates.add(gate); }

    public Ticket parkVehicle(Vehicle vehicle, Gate entryGate) {
        try {
            System.out.println("Attempting to park " + vehicle.getVehicleType() + " at Gate " + entryGate.getGateId());
            Slot assigned = slotStrategy.assignSlot(entryGate, slots, vehicle.getVehicleType());

            assigned.markOccupied();
            Ticket ticket = new Ticket(vehicle, assigned);
            System.out.println("Assigned Slot: " + assigned.getSlotId() + " on Floor " + assigned.getFloorNumber());
            return ticket;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public double processExit(Ticket ticket) {
        ticket.getSlot().markFree();
        double fee = pricingStrategy.computeFee(ticket, System.currentTimeMillis());

        System.out.println("Vehicle " + ticket.getVehicle().getNumberPlate()
                + " exited. Slot " + ticket.getSlot().getSlotId() + " is now free. Fee: $" + fee);
        return fee;
    }

    public void displayStatus(SlotType filterType) {
        Map<SlotType, Long> availableByType = slots.stream()
                .filter(s -> !s.isOccupied())
                .filter(s -> filterType == null || s.getSlotType() == filterType)
                .collect(Collectors.groupingBy(Slot::getSlotType, Collectors.counting()));

        System.out.println("\n--- Parking Lot Status ---");
        if (availableByType.isEmpty()) {
            System.out.println("No slots available.");
        } else {
            availableByType.forEach((type, count) ->
                    System.out.println(type + " SLOTS available: " + count));
        }
        System.out.println("--------------------------\n");
    }
}
