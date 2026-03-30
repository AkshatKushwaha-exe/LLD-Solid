public class Main {
    public static void main(String[] args) {
        System.out.println("=== Elevator System Demo ===\n");

        ElevatorDispatchStrategy strategy = new NearestElevatorStrategy();
        ElevatorController controller = new ElevatorController(strategy, 10);

        // different weight limits
        Elevator e1 = new Elevator("E1", 700.0);
        Elevator e2 = new Elevator("E2", 500.0);
        Elevator e3 = new Elevator("E3", 800.0);
        controller.addElevator(e1);
        controller.addElevator(e2);
        controller.addElevator(e3);

        controller.printStatus();

        // someone on floor 5 presses UP
        System.out.println(">> Scenario 1: External call from floor 5");
        ExternalButton btn5 = new ExternalButton(controller.getFloor(5), controller);
        btn5.pressUp();
        controller.printStatus();

        // inside E1 someone presses floor 8
        System.out.println(">> Scenario 2: Internal button press in E1");
        e1.getInternalButton().pressFloor(8);
        e1.processNextRequest();
        controller.printStatus();

        // overweight
        System.out.println(">> Scenario 3: Overweight in E2");
        e2.moveToFloor(3);
        e2.updateWeight(520.0); // exceeds 500kg limit
        controller.printStatus();

        // emergency
        System.out.println(">> Scenario 4: Emergency in E3");
        e3.getInternalButton().pressFloor(9);
        e3.getInternalButton().pressFloor(7);
        e3.getInternalButton().pressEmergency();
        controller.printStatus();

        // floor maintenance
        System.out.println(">> Scenario 5: Floor 4 under maintenance");
        controller.setFloorMaintenance(4, true);
        ExternalButton btn4 = new ExternalButton(controller.getFloor(4), controller);
        btn4.pressUp(); // should be rejected

        // elevator maintenance
        System.out.println("\n>> Scenario 6: E2 maintenance");
        controller.setElevatorMaintenance("E2", true);
        controller.printStatus();
    }
}
