import java.util.LinkedList;
import java.util.Queue;

public class Elevator {
    private final String id;
    private int currentFloor;
    private ElevatorState state;
    private final double maxWeightKg;
    private double currentWeightKg;
    private boolean doorOpen;
    private final Display display;
    private final InternalButton internalButton;
    private final Queue<Request> requestQueue;

    public Elevator(String id, double maxWeightKg) {
        this.id = id;
        this.currentFloor = 0;
        this.state = ElevatorState.IDLE;
        this.maxWeightKg = maxWeightKg;
        this.currentWeightKg = 0;
        this.doorOpen = false;
        this.display = new Display();
        this.internalButton = new InternalButton(this);
        this.requestQueue = new LinkedList<>();
    }

    public String getId() { return id; }
    public int getCurrentFloor() { return currentFloor; }
    public ElevatorState getState() { return state; }
    public double getMaxWeightKg() { return maxWeightKg; }
    public double getCurrentWeightKg() { return currentWeightKg; }
    public boolean isDoorOpen() { return doorOpen; }
    public Display getDisplay() { return display; }
    public InternalButton getInternalButton() { return internalButton; }
    public Queue<Request> getRequestQueue() { return requestQueue; }

    // called when the weight sensor updates
    public void updateWeight(double weightKg) {
        this.currentWeightKg = weightKg;
        if (currentWeightKg > maxWeightKg) {
            handleOverweight();
        }
    }

    private void handleOverweight() {
        System.out.println("Elevator " + id + ": OVERWEIGHT detected! Stopping.");
        this.state = ElevatorState.IDLE;
        openDoor();
        System.out.println("Elevator " + id + ": ALARM - Please reduce the weight to continue.");
    }

    public void addRequest(Request req) {
        if (state == ElevatorState.MAINTENANCE) {
            System.out.println("Elevator " + id + " is under maintenance, request ignored.");
            return;
        }
        requestQueue.add(req);
    }

    public void moveToFloor(int target) {
        if (state == ElevatorState.MAINTENANCE) {
            System.out.println("Elevator " + id + " is under maintenance. Can't move.");
            return;
        }
        if (currentWeightKg > maxWeightKg) {
            System.out.println("Elevator " + id + " is overweight. Can't move.");
            return;
        }

        closeDoor();
        if (doorOpen) return; // couldn't close (overweight)

        if (target > currentFloor) {
            state = ElevatorState.MOVING_UP;
        } else if (target < currentFloor) {
            state = ElevatorState.MOVING_DOWN;
        }

        Direction dir = (target >= currentFloor) ? Direction.UP : Direction.DOWN;
        System.out.println("Elevator " + id + ": Moving from floor " + currentFloor + " to " + target);

        while (currentFloor != target) {
            currentFloor += (target > currentFloor) ? 1 : -1;
            display.update(currentFloor, dir);
        }

        state = ElevatorState.IDLE;
        display.update(currentFloor, null);
        openDoor();
    }

    public void openDoor() {
        doorOpen = true;
        System.out.println("Elevator " + id + ": Door OPENED at floor " + currentFloor);
    }

    public void closeDoor() {
        if (currentWeightKg > maxWeightKg) {
            System.out.println("Elevator " + id + ": Can't close door - overweight!");
            return;
        }
        doorOpen = false;
        System.out.println("Elevator " + id + ": Door CLOSED");
    }

    public void triggerEmergency() {
        System.out.println("Elevator " + id + ": *** EMERGENCY *** Stopping immediately!");
        this.state = ElevatorState.IDLE;
        openDoor();
        requestQueue.clear();
        System.out.println("Elevator " + id + ": ALARM ringing. All pending requests cleared.");
    }

    public void setMaintenance(boolean on) {
        if (on) {
            this.state = ElevatorState.MAINTENANCE;
            requestQueue.clear();
            System.out.println("Elevator " + id + ": Switched to MAINTENANCE mode.");
        } else {
            this.state = ElevatorState.IDLE;
            System.out.println("Elevator " + id + ": Back in service.");
        }
    }

    public void processNextRequest() {
        if (requestQueue.isEmpty()) return;
        Request next = requestQueue.poll();
        moveToFloor(next.getTargetFloor());
    }

    public boolean isAvailable() {
        return state != ElevatorState.MAINTENANCE && currentWeightKg <= maxWeightKg;
    }
}
