public class ExternalButton {
    private final Floor floor;
    private final ElevatorController controller;

    public ExternalButton(Floor floor, ElevatorController controller) {
        this.floor = floor;
        this.controller = controller;
    }

    public void pressUp() {
        System.out.println("[External] UP pressed on Floor " + floor.getFloorNumber());
        controller.handleExternalRequest(new Request(floor.getFloorNumber(), Direction.UP));
    }

    public void pressDown() {
        System.out.println("[External] DOWN pressed on Floor " + floor.getFloorNumber());
        controller.handleExternalRequest(new Request(floor.getFloorNumber(), Direction.DOWN));
    }
}
