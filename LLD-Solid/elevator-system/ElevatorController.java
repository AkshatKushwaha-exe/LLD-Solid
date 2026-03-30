import java.util.ArrayList;
import java.util.List;

public class ElevatorController {
    private final List<Elevator> elevators;
    private final List<Floor> floors;
    private final ElevatorDispatchStrategy strategy;

    public ElevatorController(ElevatorDispatchStrategy strategy, int totalFloors) {
        this.strategy = strategy;
        this.elevators = new ArrayList<>();
        this.floors = new ArrayList<>();
        for (int i = 0; i < totalFloors; i++) {
            floors.add(new Floor(i));
        }
    }

    public void addElevator(Elevator elevator) { elevators.add(elevator); }
    public List<Elevator> getElevators() { return elevators; }
    public List<Floor> getFloors() { return floors; }

    public Floor getFloor(int num) {
        if (num < 0 || num >= floors.size()) return null;
        return floors.get(num);
    }

    public void handleExternalRequest(Request request) {
        int target = request.getTargetFloor();
        if (target < 0 || target >= floors.size()) {
            System.out.println("Invalid floor: " + target);
            return;
        }
        if (floors.get(target).isUnderMaintenance()) {
            System.out.println("Floor " + target + " is under maintenance. Request rejected.");
            return;
        }

        Elevator chosen = strategy.dispatch(request, elevators);
        if (chosen != null) {
            System.out.println("Dispatching Elevator " + chosen.getId() + " to floor " + target);
            chosen.addRequest(request);
            chosen.processNextRequest();
        }
    }

    public void setFloorMaintenance(int floorNum, boolean maintenance) {
        if (floorNum < 0 || floorNum >= floors.size()) {
            System.out.println("Invalid floor: " + floorNum);
            return;
        }
        floors.get(floorNum).setUnderMaintenance(maintenance);
        String status = maintenance ? "UNDER MAINTENANCE" : "BACK IN SERVICE";
        System.out.println("Floor " + floorNum + ": " + status);
    }

    public void setElevatorMaintenance(String elevatorId, boolean maintenance) {
        for (Elevator e : elevators) {
            if (e.getId().equals(elevatorId)) {
                e.setMaintenance(maintenance);
                return;
            }
        }
        System.out.println("Elevator " + elevatorId + " not found.");
    }

    public void printStatus() {
        System.out.println("\n--- System Status ---");
        for (Elevator e : elevators) {
            System.out.println("  " + e.getId()
                + " | Floor: " + e.getCurrentFloor()
                + " | State: " + e.getState()
                + " | Weight: " + e.getCurrentWeightKg() + "/" + e.getMaxWeightKg() + "kg"
                + " | Pending: " + e.getRequestQueue().size());
        }
        for (Floor f : floors) {
            if (f.isUnderMaintenance()) {
                System.out.println("  Floor " + f.getFloorNumber() + ": MAINTENANCE");
            }
        }
        System.out.println("---------------------\n");
    }
}
