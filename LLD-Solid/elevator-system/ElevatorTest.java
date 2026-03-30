import java.util.List;
import java.util.ArrayList;

public class ElevatorTest {
    static int passed = 0, failed = 0;

    static void check(String name, boolean condition) {
        if (condition) { passed++; System.out.println("  PASS: " + name); }
        else { failed++; System.out.println("  FAIL: " + name); }
    }

    // ====== ELEVATOR BASICS ======

    static void testDefaults() {
        System.out.println("\n[1] Elevator Defaults");
        Elevator e = new Elevator("E1", 700);
        check("starts at floor 0", e.getCurrentFloor() == 0);
        check("state is IDLE", e.getState() == ElevatorState.IDLE);
        check("weight is 0", e.getCurrentWeightKg() == 0);
        check("door closed", !e.isDoorOpen());
        check("max weight correct", e.getMaxWeightKg() == 700);
        check("queue empty", e.getRequestQueue().isEmpty());
        check("id correct", e.getId().equals("E1"));
        check("display exists", e.getDisplay() != null);
        check("internal button exists", e.getInternalButton() != null);
    }

    static void testMoveUp() {
        System.out.println("\n[2] Move Up");
        Elevator e = new Elevator("E1", 700);
        e.moveToFloor(5);
        check("reached floor 5", e.getCurrentFloor() == 5);
        check("state IDLE after arrival", e.getState() == ElevatorState.IDLE);
        check("door open after arrival", e.isDoorOpen());
        check("display shows floor 5", e.getDisplay().getCurrentFloor() == 5);
    }

    static void testMoveDown() {
        System.out.println("\n[3] Move Down");
        Elevator e = new Elevator("E1", 700);
        e.moveToFloor(7);
        e.moveToFloor(2);
        check("reached floor 2", e.getCurrentFloor() == 2);
        check("state IDLE", e.getState() == ElevatorState.IDLE);
        check("door open", e.isDoorOpen());
    }

    static void testMoveToSameFloor() {
        System.out.println("\n[4] Move to Same Floor (no-op move)");
        Elevator e = new Elevator("E1", 700);
        e.moveToFloor(0);
        check("still at floor 0", e.getCurrentFloor() == 0);
        check("state IDLE", e.getState() == ElevatorState.IDLE);
    }

    // ====== WEIGHT ======

    static void testOverweightStopsElevator() {
        System.out.println("\n[5] Overweight Stops Elevator");
        Elevator e = new Elevator("E1", 500);
        e.updateWeight(600);
        check("state is IDLE (stopped)", e.getState() == ElevatorState.IDLE);
        check("door forced open", e.isDoorOpen());
    }

    static void testOverweightCannotMove() {
        System.out.println("\n[6] Overweight Cannot Move");
        Elevator e = new Elevator("E1", 500);
        e.updateWeight(600);
        e.moveToFloor(3);
        check("didn't move", e.getCurrentFloor() == 0);
    }

    static void testOverweightCannotCloseDoor() {
        System.out.println("\n[7] Overweight Cannot Close Door");
        Elevator e = new Elevator("E1", 500);
        e.updateWeight(600);
        e.closeDoor();
        check("door still open", e.isDoorOpen());
    }

    static void testWeightAtExactLimit() {
        System.out.println("\n[8] Weight at Exact Limit (should be OK)");
        Elevator e = new Elevator("E1", 700);
        e.updateWeight(700);
        check("available at exact limit", e.isAvailable());
        e.moveToFloor(3);
        check("can move at exact limit", e.getCurrentFloor() == 3);
    }

    static void testWeightRecovery() {
        System.out.println("\n[9] Weight Recovery After Overload");
        Elevator e = new Elevator("E1", 500);
        e.updateWeight(600); // overweight
        check("cannot move overweight", e.isAvailable() == false);
        e.updateWeight(400); // back to normal
        check("available again", e.isAvailable());
        e.moveToFloor(5);
        check("can move after weight reduced", e.getCurrentFloor() == 5);
    }

    static void testDifferentWeightLimits() {
        System.out.println("\n[10] Different Weight Limits Per Elevator");
        Elevator e1 = new Elevator("E1", 700);
        Elevator e2 = new Elevator("E2", 400);
        e1.updateWeight(500);
        e2.updateWeight(500);
        check("E1 OK at 500/700", e1.isAvailable());
        check("E2 overweight at 500/400", !e2.isAvailable());
    }

    // ====== EMERGENCY ======

    static void testEmergencyClearsQueue() {
        System.out.println("\n[11] Emergency Clears Queue");
        Elevator e = new Elevator("E1", 700);
        e.addRequest(new Request(3));
        e.addRequest(new Request(7));
        e.addRequest(new Request(9));
        check("3 requests queued", e.getRequestQueue().size() == 3);
        e.triggerEmergency();
        check("queue cleared", e.getRequestQueue().isEmpty());
        check("state IDLE", e.getState() == ElevatorState.IDLE);
        check("door open", e.isDoorOpen());
    }

    static void testEmergencyViaInternalButton() {
        System.out.println("\n[12] Emergency Via Internal Button");
        Elevator e = new Elevator("E1", 700);
        e.addRequest(new Request(5));
        e.getInternalButton().pressEmergency();
        check("queue cleared via button", e.getRequestQueue().isEmpty());
        check("door open", e.isDoorOpen());
    }

    // ====== MAINTENANCE ======

    static void testMaintenanceRejectsRequests() {
        System.out.println("\n[13] Maintenance Rejects Requests");
        Elevator e = new Elevator("E1", 700);
        e.setMaintenance(true);
        check("state is MAINTENANCE", e.getState() == ElevatorState.MAINTENANCE);
        e.addRequest(new Request(5));
        check("request rejected", e.getRequestQueue().isEmpty());
    }

    static void testMaintenanceCannotMove() {
        System.out.println("\n[14] Maintenance Cannot Move");
        Elevator e = new Elevator("E1", 700);
        e.setMaintenance(true);
        e.moveToFloor(5);
        check("didn't move", e.getCurrentFloor() == 0);
    }

    static void testMaintenanceClearsExistingQueue() {
        System.out.println("\n[15] Maintenance Clears Existing Queue");
        Elevator e = new Elevator("E1", 700);
        e.addRequest(new Request(3));
        e.addRequest(new Request(7));
        e.setMaintenance(true);
        check("queue cleared", e.getRequestQueue().isEmpty());
    }

    static void testMaintenanceRecovery() {
        System.out.println("\n[16] Maintenance Recovery");
        Elevator e = new Elevator("E1", 700);
        e.setMaintenance(true);
        check("not available", !e.isAvailable());
        e.setMaintenance(false);
        check("state back to IDLE", e.getState() == ElevatorState.IDLE);
        check("available again", e.isAvailable());
        e.addRequest(new Request(4));
        check("accepts requests again", e.getRequestQueue().size() == 1);
    }

    // ====== DOOR ======

    static void testDoorOpenClose() {
        System.out.println("\n[17] Door Open/Close");
        Elevator e = new Elevator("E1", 700);
        check("door starts closed", !e.isDoorOpen());
        e.openDoor();
        check("door opened", e.isDoorOpen());
        e.closeDoor();
        check("door closed", !e.isDoorOpen());
    }

    static void testInternalButtonOpenClose() {
        System.out.println("\n[18] Internal Button Open/Close");
        Elevator e = new Elevator("E1", 700);
        e.getInternalButton().pressOpen();
        check("door opened via button", e.isDoorOpen());
        e.getInternalButton().pressClose();
        check("door closed via button", !e.isDoorOpen());
    }

    // ====== REQUEST QUEUE ======

    static void testRequestQueueProcessing() {
        System.out.println("\n[19] Request Queue FIFO Processing");
        Elevator e = new Elevator("E1", 700);
        e.addRequest(new Request(3));
        e.addRequest(new Request(7));
        e.processNextRequest();
        check("processed first request (floor 3)", e.getCurrentFloor() == 3);
        check("one request left", e.getRequestQueue().size() == 1);
        e.processNextRequest();
        check("processed second request (floor 7)", e.getCurrentFloor() == 7);
        check("queue empty", e.getRequestQueue().isEmpty());
    }

    static void testInternalButtonAddsRequest() {
        System.out.println("\n[20] Internal Button Adds Request");
        Elevator e = new Elevator("E1", 700);
        e.getInternalButton().pressFloor(6);
        check("request queued", e.getRequestQueue().size() == 1);
        e.processNextRequest();
        check("moved to floor 6", e.getCurrentFloor() == 6);
    }

    static void testProcessEmptyQueue() {
        System.out.println("\n[21] Process Empty Queue (no-op)");
        Elevator e = new Elevator("E1", 700);
        e.processNextRequest(); // should not crash
        check("still at floor 0", e.getCurrentFloor() == 0);
    }

    // ====== DISPATCH STRATEGY ======

    static void testNearestIdlePicked() {
        System.out.println("\n[22] Nearest Idle Elevator Picked");
        NearestElevatorStrategy strat = new NearestElevatorStrategy();
        Elevator e1 = new Elevator("E1", 700); // floor 0
        Elevator e2 = new Elevator("E2", 700);
        e2.moveToFloor(4); // floor 4
        Elevator chosen = strat.dispatch(new Request(5, Direction.UP), List.of(e1, e2));
        check("picked E2 (closer to floor 5)", chosen.getId().equals("E2"));
    }

    static void testStrategySkipsMaintenance() {
        System.out.println("\n[23] Strategy Skips Maintenance Elevator");
        NearestElevatorStrategy strat = new NearestElevatorStrategy();
        Elevator e1 = new Elevator("E1", 700);
        e1.moveToFloor(4);
        e1.setMaintenance(true); // closer but unavailable
        Elevator e2 = new Elevator("E2", 700); // floor 0
        Elevator chosen = strat.dispatch(new Request(5, Direction.UP), List.of(e1, e2));
        check("skipped maintenance, picked E2", chosen.getId().equals("E2"));
    }

    static void testStrategySkipsOverweight() {
        System.out.println("\n[24] Strategy Skips Overweight Elevator");
        NearestElevatorStrategy strat = new NearestElevatorStrategy();
        Elevator e1 = new Elevator("E1", 500);
        e1.moveToFloor(4);
        e1.updateWeight(600); // overweight
        Elevator e2 = new Elevator("E2", 700); // floor 0
        Elevator chosen = strat.dispatch(new Request(5, Direction.UP), List.of(e1, e2));
        check("skipped overweight, picked E2", chosen.getId().equals("E2"));
    }

    static void testStrategyAllUnavailable() {
        System.out.println("\n[25] Strategy All Unavailable");
        NearestElevatorStrategy strat = new NearestElevatorStrategy();
        Elevator e1 = new Elevator("E1", 700);
        e1.setMaintenance(true);
        Elevator e2 = new Elevator("E2", 500);
        e2.updateWeight(600);
        Elevator chosen = strat.dispatch(new Request(3, Direction.UP), List.of(e1, e2));
        check("returns null when all unavailable", chosen == null);
    }

    static void testStrategySingleElevator() {
        System.out.println("\n[26] Strategy With Single Elevator");
        NearestElevatorStrategy strat = new NearestElevatorStrategy();
        Elevator e1 = new Elevator("E1", 700);
        Elevator chosen = strat.dispatch(new Request(8, Direction.UP), List.of(e1));
        check("picks the only elevator", chosen.getId().equals("E1"));
    }

    // ====== CONTROLLER ======

    static void testControllerFloorMaintenance() {
        System.out.println("\n[27] Controller Floor Maintenance Rejects Request");
        ElevatorController ctrl = new ElevatorController(new NearestElevatorStrategy(), 10);
        Elevator e = new Elevator("E1", 700);
        ctrl.addElevator(e);
        ctrl.setFloorMaintenance(5, true);
        ctrl.handleExternalRequest(new Request(5, Direction.UP));
        check("elevator didn't move to maintenance floor", e.getCurrentFloor() == 0);
    }

    static void testControllerFloorMaintenanceRecovery() {
        System.out.println("\n[28] Controller Floor Maintenance Recovery");
        ElevatorController ctrl = new ElevatorController(new NearestElevatorStrategy(), 10);
        Elevator e = new Elevator("E1", 700);
        ctrl.addElevator(e);
        ctrl.setFloorMaintenance(5, true);
        ctrl.handleExternalRequest(new Request(5, Direction.UP));
        check("rejected during maintenance", e.getCurrentFloor() == 0);
        ctrl.setFloorMaintenance(5, false);
        ctrl.handleExternalRequest(new Request(5, Direction.UP));
        check("accepted after maintenance lifted", e.getCurrentFloor() == 5);
    }

    static void testControllerInvalidFloor() {
        System.out.println("\n[29] Controller Invalid Floor");
        ElevatorController ctrl = new ElevatorController(new NearestElevatorStrategy(), 10);
        Elevator e = new Elevator("E1", 700);
        ctrl.addElevator(e);
        ctrl.handleExternalRequest(new Request(15, Direction.UP)); // floor 15 doesn't exist
        check("elevator didn't move", e.getCurrentFloor() == 0);
        ctrl.handleExternalRequest(new Request(-1, Direction.DOWN)); // negative floor
        check("elevator didn't move for negative floor", e.getCurrentFloor() == 0);
    }

    static void testControllerElevatorMaintenance() {
        System.out.println("\n[30] Controller Elevator Maintenance");
        ElevatorController ctrl = new ElevatorController(new NearestElevatorStrategy(), 10);
        Elevator e1 = new Elevator("E1", 700);
        Elevator e2 = new Elevator("E2", 700);
        ctrl.addElevator(e1);
        ctrl.addElevator(e2);
        ctrl.setElevatorMaintenance("E1", true);
        check("E1 in maintenance", e1.getState() == ElevatorState.MAINTENANCE);
        // external request should go to E2
        ctrl.handleExternalRequest(new Request(3, Direction.UP));
        check("E2 dispatched instead", e2.getCurrentFloor() == 3);
        check("E1 untouched", e1.getCurrentFloor() == 0);
    }

    static void testControllerGetFloor() {
        System.out.println("\n[31] Controller GetFloor");
        ElevatorController ctrl = new ElevatorController(new NearestElevatorStrategy(), 5);
        check("floor 0 exists", ctrl.getFloor(0) != null);
        check("floor 4 exists", ctrl.getFloor(4) != null);
        check("floor 5 returns null", ctrl.getFloor(5) == null);
        check("floor -1 returns null", ctrl.getFloor(-1) == null);
    }

    static void testExternalButton() {
        System.out.println("\n[32] External Button Integration");
        ElevatorController ctrl = new ElevatorController(new NearestElevatorStrategy(), 10);
        Elevator e = new Elevator("E1", 700);
        ctrl.addElevator(e);
        ExternalButton btn = new ExternalButton(ctrl.getFloor(6), ctrl);
        btn.pressUp();
        check("elevator moved to floor 6", e.getCurrentFloor() == 6);
    }

    static void testMultipleExternalCalls() {
        System.out.println("\n[33] Multiple External Calls Route Correctly");
        ElevatorController ctrl = new ElevatorController(new NearestElevatorStrategy(), 10);
        Elevator e1 = new Elevator("E1", 700);
        Elevator e2 = new Elevator("E2", 700);
        ctrl.addElevator(e1);
        ctrl.addElevator(e2);

        // first call goes to one of them (both at floor 0, could be either)
        ctrl.handleExternalRequest(new Request(8, Direction.UP));
        // one should be at 8, the other at 0
        boolean oneAtEight = (e1.getCurrentFloor() == 8 || e2.getCurrentFloor() == 8);
        check("one elevator dispatched to 8", oneAtEight);

        // second call to floor 2 should pick the one still at 0
        ctrl.handleExternalRequest(new Request(2, Direction.UP));
        boolean covered = (e1.getCurrentFloor() == 8 && e2.getCurrentFloor() == 2)
                       || (e1.getCurrentFloor() == 2 && e2.getCurrentFloor() == 8);
        check("both calls served by different elevators", covered);
    }

    // ====== MAIN ======

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   ELEVATOR SYSTEM - FULL TEST SUITE");
        System.out.println("========================================");

        testDefaults();
        testMoveUp();
        testMoveDown();
        testMoveToSameFloor();
        testOverweightStopsElevator();
        testOverweightCannotMove();
        testOverweightCannotCloseDoor();
        testWeightAtExactLimit();
        testWeightRecovery();
        testDifferentWeightLimits();
        testEmergencyClearsQueue();
        testEmergencyViaInternalButton();
        testMaintenanceRejectsRequests();
        testMaintenanceCannotMove();
        testMaintenanceClearsExistingQueue();
        testMaintenanceRecovery();
        testDoorOpenClose();
        testInternalButtonOpenClose();
        testRequestQueueProcessing();
        testInternalButtonAddsRequest();
        testProcessEmptyQueue();
        testNearestIdlePicked();
        testStrategySkipsMaintenance();
        testStrategySkipsOverweight();
        testStrategyAllUnavailable();
        testStrategySingleElevator();
        testControllerFloorMaintenance();
        testControllerFloorMaintenanceRecovery();
        testControllerInvalidFloor();
        testControllerElevatorMaintenance();
        testControllerGetFloor();
        testExternalButton();
        testMultipleExternalCalls();

        System.out.println("\n========================================");
        System.out.println("  PASSED: " + passed + " / " + (passed + failed));
        System.out.println("  FAILED: " + failed);
        System.out.println("========================================");
        if (failed > 0) System.exit(1);
    }
}
