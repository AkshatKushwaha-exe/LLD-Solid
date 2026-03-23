package multi_lvl_parkinglot;

import java.util.List;

public interface SlotAssignmentStrategy {
    Slot assignSlot(Gate entryGate, List<Slot> allSlots, VehicleType vehicleType) throws Exception;
}
