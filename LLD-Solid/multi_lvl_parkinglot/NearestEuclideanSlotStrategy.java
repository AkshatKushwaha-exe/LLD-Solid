package multi_lvl_parkinglot;

import java.util.List;

public class NearestEuclideanSlotStrategy implements SlotAssignmentStrategy {

    private SlotType mapVehicleToSlot(VehicleType vehicleType) {
        switch (vehicleType) {
            case TWO_WHEELER: return SlotType.SMALL;
            case CAR:         return SlotType.MEDIUM;
            case BUS:         return SlotType.LARGE;
            default: throw new IllegalArgumentException("Unsupported vehicle type: " + vehicleType);
        }
    }

    @Override
    public Slot assignSlot(Gate entryGate, List<Slot> allSlots, VehicleType vehicleType) throws Exception {
        SlotType needed = mapVehicleToSlot(vehicleType);

        Slot closest = null;
        double shortestDist = Double.MAX_VALUE;

        for (Slot s : allSlots) {
            if (!s.isOccupied() && s.getSlotType() == needed) {
                double dist = s.distanceTo(entryGate);
                if (dist < shortestDist) {
                    shortestDist = dist;
                    closest = s;
                }
            }
        }

        if (closest == null) {
            throw new Exception("No available slot for type: " + needed);
        }
        return closest;
    }
}
