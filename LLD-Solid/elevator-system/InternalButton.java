public class InternalButton {
    private final Elevator elevator;

    public InternalButton(Elevator elevator) {
        this.elevator = elevator;
    }

    public void pressFloor(int floor) {
        System.out.println("[Internal] Floor " + floor + " pressed in Elevator " + elevator.getId());
        elevator.addRequest(new Request(floor));
    }

    public void pressOpen() {
        System.out.println("[Internal] OPEN pressed in Elevator " + elevator.getId());
        elevator.openDoor();
    }

    public void pressClose() {
        System.out.println("[Internal] CLOSE pressed in Elevator " + elevator.getId());
        elevator.closeDoor();
    }

    public void pressEmergency() {
        System.out.println("[Internal] EMERGENCY pressed in Elevator " + elevator.getId());
        elevator.triggerEmergency();
    }
}
