package multi_lvl_parkinglot;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingLotTest {

    static int passed = 0;
    static int failed = 0;

    static void check(String testName, boolean condition) {
        if (condition) {
            System.out.println("PASS: " + testName);
            passed++;
        } else {
            System.out.println("FAIL: " + testName);
            failed++;
        }
    }

    // ---------- Test 1: Vehicle stores numberPlate and type correctly ----------
    static void testVehicleStoresFields() {
        Vehicle car = new Vehicle("KA-01-1234", VehicleType.CAR);
        check("Vehicle stores numberPlate correctly",
                "KA-01-1234".equals(car.getNumberPlate()));
        check("Vehicle stores vehicleType correctly",
                VehicleType.CAR == car.getVehicleType());

        Vehicle bike = new Vehicle("MH-02-5678", VehicleType.TWO_WHEELER);
        check("Vehicle stores TWO_WHEELER type correctly",
                VehicleType.TWO_WHEELER == bike.getVehicleType());

        Vehicle bus = new Vehicle("DL-03-9999", VehicleType.BUS);
        check("Vehicle stores BUS type correctly",
                VehicleType.BUS == bus.getVehicleType());
    }

    // ---------- Test 2: Gate stores all fields correctly ----------
    static void testGateStoresFields() {
        Gate gate = new Gate("G1", 2, 3.5, 7.2);
        check("Gate stores gateId correctly",
                "G1".equals(gate.getGateId()));
        check("Gate stores floorNumber correctly",
                gate.getFloorNumber() == 2);
        check("Gate stores xCoord correctly",
                Math.abs(gate.getXCoord() - 3.5) < 1e-9);
        check("Gate stores yCoord correctly",
                Math.abs(gate.getYCoord() - 7.2) < 1e-9);
    }

    // ---------- Test 3: Slot starts as not occupied ----------
    static void testSlotStartsNotOccupied() {
        Slot slot = new Slot("S1", SlotType.MEDIUM, 1, 10.0, 20.0);
        check("Slot starts as not occupied",
                !slot.isOccupied());
    }

    // ---------- Test 4: Slot markOccupied/markFree toggles state ----------
    static void testSlotOccupiedToggle() {
        Slot slot = new Slot("S1", SlotType.SMALL, 1, 5.0, 5.0);
        check("Slot initially not occupied", !slot.isOccupied());

        slot.markOccupied();
        check("Slot is occupied after markOccupied", slot.isOccupied());

        slot.markFree();
        check("Slot is free after markFree", !slot.isOccupied());

        // Toggle again
        slot.markOccupied();
        check("Slot is occupied after second markOccupied", slot.isOccupied());

        slot.markOccupied();
        check("Slot remains occupied after double markOccupied", slot.isOccupied());

        slot.markFree();
        slot.markFree();
        check("Slot remains free after double markFree", !slot.isOccupied());
    }

    // ---------- Test 5: Slot distanceTo calculates 3D Euclidean distance correctly ----------
    static void testSlotDistanceTo() {
        // Same floor: distance = sqrt(dx^2 + dy^2)
        Gate gate = new Gate("G1", 1, 0.0, 0.0);
        Slot slot1 = new Slot("S1", SlotType.MEDIUM, 1, 3.0, 4.0);
        double dist1 = slot1.distanceTo(gate);
        check("distanceTo same floor (3,4) = 5.0",
                Math.abs(dist1 - 5.0) < 1e-9);

        // Different floor: floorDiff * 10
        // Slot on floor 2, gate on floor 1 => floorDiff = 1 * 10 = 10
        // dx=3, dy=4, floorDiff=10 => sqrt(9 + 16 + 100) = sqrt(125)
        Slot slot2 = new Slot("S2", SlotType.MEDIUM, 2, 3.0, 4.0);
        double dist2 = slot2.distanceTo(gate);
        double expected2 = Math.sqrt(9 + 16 + 100);
        check("distanceTo different floor sqrt(9+16+100) = sqrt(125)",
                Math.abs(dist2 - expected2) < 1e-9);

        // Slot on floor 3, gate on floor 1 => floorDiff = 2 * 10 = 20
        // dx=0, dy=0 => sqrt(0 + 0 + 400) = 20.0
        Slot slot3 = new Slot("S3", SlotType.SMALL, 3, 0.0, 0.0);
        double dist3 = slot3.distanceTo(gate);
        check("distanceTo only floor diff (2 floors apart) = 20.0",
                Math.abs(dist3 - 20.0) < 1e-9);

        // Negative floor diff should still work (gate on higher floor)
        Gate gateFloor3 = new Gate("G2", 3, 0.0, 0.0);
        Slot slotFloor1 = new Slot("S4", SlotType.LARGE, 1, 0.0, 0.0);
        double dist4 = slotFloor1.distanceTo(gateFloor3);
        check("distanceTo negative floor diff still yields 20.0",
                Math.abs(dist4 - 20.0) < 1e-9);
    }

    // ---------- Test 6: NearestEuclideanSlotStrategy assigns nearest slot of correct type ----------
    static void testNearestSlotAssignment() throws Exception {
        NearestEuclideanSlotStrategy strategy = new NearestEuclideanSlotStrategy();
        Gate gate = new Gate("G1", 1, 0.0, 0.0);

        // Two MEDIUM slots: S1 closer (10,10), S2 farther (50,50)
        Slot nearSlot = new Slot("S1", SlotType.MEDIUM, 1, 10.0, 10.0);
        Slot farSlot = new Slot("S2", SlotType.MEDIUM, 1, 50.0, 50.0);
        List<Slot> slots = new ArrayList<>();
        slots.add(farSlot);   // add far one first to ensure it's not just picking first
        slots.add(nearSlot);

        Slot assigned = strategy.assignSlot(gate, slots, VehicleType.CAR);
        check("Nearest slot strategy assigns closest MEDIUM slot (S1)",
                "S1".equals(assigned.getSlotId()));
    }

    // ---------- Test 7: NearestEuclideanSlotStrategy skips occupied slots ----------
    static void testNearestSlotSkipsOccupied() throws Exception {
        NearestEuclideanSlotStrategy strategy = new NearestEuclideanSlotStrategy();
        Gate gate = new Gate("G1", 1, 0.0, 0.0);

        Slot nearSlot = new Slot("S1", SlotType.MEDIUM, 1, 10.0, 10.0);
        Slot farSlot = new Slot("S2", SlotType.MEDIUM, 1, 50.0, 50.0);
        nearSlot.markOccupied(); // nearest is occupied

        List<Slot> slots = new ArrayList<>();
        slots.add(nearSlot);
        slots.add(farSlot);

        Slot assigned = strategy.assignSlot(gate, slots, VehicleType.CAR);
        check("Nearest slot strategy skips occupied slot, assigns S2",
                "S2".equals(assigned.getSlotId()));
    }

    // ---------- Test 8: NearestEuclideanSlotStrategy throws when no slot available ----------
    static void testNearestSlotThrowsWhenNoneAvailable() {
        NearestEuclideanSlotStrategy strategy = new NearestEuclideanSlotStrategy();
        Gate gate = new Gate("G1", 1, 0.0, 0.0);

        // All MEDIUM slots occupied
        Slot s1 = new Slot("S1", SlotType.MEDIUM, 1, 10.0, 10.0);
        s1.markOccupied();
        List<Slot> slots = new ArrayList<>();
        slots.add(s1);

        boolean threwException = false;
        try {
            strategy.assignSlot(gate, slots, VehicleType.CAR);
        } catch (Exception e) {
            threwException = true;
            check("Exception message contains slot type",
                    e.getMessage().contains("MEDIUM"));
        }
        check("NearestSlotStrategy throws when no slot available", threwException);

        // Empty slot list
        boolean threwOnEmpty = false;
        try {
            strategy.assignSlot(gate, new ArrayList<>(), VehicleType.TWO_WHEELER);
        } catch (Exception e) {
            threwOnEmpty = true;
        }
        check("NearestSlotStrategy throws on empty slot list", threwOnEmpty);

        // Wrong type slots only (all SMALL, need MEDIUM for CAR)
        Slot smallSlot = new Slot("S2", SlotType.SMALL, 1, 5.0, 5.0);
        List<Slot> wrongTypeSlots = new ArrayList<>();
        wrongTypeSlots.add(smallSlot);

        boolean threwOnWrongType = false;
        try {
            strategy.assignSlot(gate, wrongTypeSlots, VehicleType.CAR);
        } catch (Exception e) {
            threwOnWrongType = true;
        }
        check("NearestSlotStrategy throws when only wrong type available", threwOnWrongType);
    }

    // ---------- Test 9: NearestEuclideanSlotStrategy maps vehicle types correctly ----------
    static void testVehicleToSlotMapping() throws Exception {
        NearestEuclideanSlotStrategy strategy = new NearestEuclideanSlotStrategy();
        Gate gate = new Gate("G1", 1, 0.0, 0.0);

        Slot smallSlot = new Slot("S-SMALL", SlotType.SMALL, 1, 5.0, 5.0);
        Slot mediumSlot = new Slot("S-MEDIUM", SlotType.MEDIUM, 1, 5.0, 5.0);
        Slot largeSlot = new Slot("S-LARGE", SlotType.LARGE, 1, 5.0, 5.0);
        List<Slot> allSlots = new ArrayList<>();
        allSlots.add(smallSlot);
        allSlots.add(mediumSlot);
        allSlots.add(largeSlot);

        Slot assignedBike = strategy.assignSlot(gate, allSlots, VehicleType.TWO_WHEELER);
        check("TWO_WHEELER maps to SMALL slot",
                SlotType.SMALL == assignedBike.getSlotType());

        Slot assignedCar = strategy.assignSlot(gate, allSlots, VehicleType.CAR);
        check("CAR maps to MEDIUM slot",
                SlotType.MEDIUM == assignedCar.getSlotType());

        Slot assignedBus = strategy.assignSlot(gate, allSlots, VehicleType.BUS);
        check("BUS maps to LARGE slot",
                SlotType.LARGE == assignedBus.getSlotType());
    }

    // ---------- Test 10: HourlyPricingStrategy computes fee correctly for 2 hours ----------
    static void testHourlyPricingTwoHours() {
        Map<SlotType, Double> rates = new HashMap<>();
        rates.put(SlotType.SMALL, 5.0);
        rates.put(SlotType.MEDIUM, 10.0);
        rates.put(SlotType.LARGE, 20.0);
        HourlyPricingStrategy pricing = new HourlyPricingStrategy(rates);

        Vehicle car = new Vehicle("ABC-123", VehicleType.CAR);
        Slot slot = new Slot("S1", SlotType.MEDIUM, 1, 0, 0);
        Ticket ticket = new Ticket(car, slot);

        // Simulate 2 hours + 1 second (to avoid exact boundary)
        long entryTime = 1000000000L;
        ticket.setEntryTimestamp(entryTime);
        long exitTime = entryTime + (2 * 60 * 60 * 1000) + 1000; // 2h + 1s

        double fee = pricing.computeFee(ticket, exitTime);
        // Math.ceil(2.000277...) = 3 hours * 10 = 30
        check("HourlyPricing 2h+1s at MEDIUM rate => ceil(2.000..)*10 = 30.0",
                Math.abs(fee - 30.0) < 1e-9);

        // Exactly 2 hours
        long exitExact2h = entryTime + (2 * 60 * 60 * 1000);
        double feeExact = pricing.computeFee(ticket, exitExact2h);
        // Math.ceil(2.0) = 2 hours * 10 = 20
        check("HourlyPricing exactly 2h at MEDIUM rate => 2*10 = 20.0",
                Math.abs(feeExact - 20.0) < 1e-9);
    }

    // ---------- Test 11: HourlyPricingStrategy minimum 1 hour charge ----------
    static void testHourlyPricingMinOneHour() {
        Map<SlotType, Double> rates = new HashMap<>();
        rates.put(SlotType.SMALL, 5.0);
        rates.put(SlotType.MEDIUM, 10.0);
        rates.put(SlotType.LARGE, 20.0);
        HourlyPricingStrategy pricing = new HourlyPricingStrategy(rates);

        Vehicle bike = new Vehicle("MH-01-111", VehicleType.TWO_WHEELER);
        Slot slot = new Slot("S1", SlotType.SMALL, 1, 0, 0);
        Ticket ticket = new Ticket(bike, slot);

        // Very short duration: 1 millisecond
        long entryTime = 1000000000L;
        ticket.setEntryTimestamp(entryTime);
        long exitTime = entryTime + 1; // 1 ms

        double fee = pricing.computeFee(ticket, exitTime);
        // Math.ceil(1ms / 3600000ms) = Math.ceil(~0.000000278) = 1 hour * 5 = 5
        check("HourlyPricing 1ms duration charges minimum 1 hour (5.0 for SMALL)",
                Math.abs(fee - 5.0) < 1e-9);

        // Zero duration (entry == exit): ceil(0) = 0, but then the if(totalHours==0) branch sets to 1
        long exitSame = entryTime;
        double feeZero = pricing.computeFee(ticket, exitSame);
        // durationMs=0, totalHours = ceil(0)=0, then set to 1 => 1*5 = 5.0
        check("HourlyPricing 0ms duration charges minimum 1 hour (5.0 for SMALL)",
                Math.abs(feeZero - 5.0) < 1e-9);
    }

    // ---------- Test 12: HourlyPricingStrategy uses default rate for unknown slot type ----------
    static void testHourlyPricingDefaultRate() {
        // Create a pricing strategy with only SMALL rate defined
        Map<SlotType, Double> rates = new HashMap<>();
        rates.put(SlotType.SMALL, 5.0);
        HourlyPricingStrategy pricing = new HourlyPricingStrategy(rates);

        Vehicle car = new Vehicle("DL-01-999", VehicleType.CAR);
        Slot slot = new Slot("S1", SlotType.MEDIUM, 1, 0, 0);
        Ticket ticket = new Ticket(car, slot);

        // 1 hour parking
        long entryTime = 1000000000L;
        ticket.setEntryTimestamp(entryTime);
        long exitTime = entryTime + (1 * 60 * 60 * 1000);

        double fee = pricing.computeFee(ticket, exitTime);
        // MEDIUM not in map => default rate 10.0, 1 hour => 10.0
        check("HourlyPricing uses default rate 10.0 for unlisted SlotType",
                Math.abs(fee - 10.0) < 1e-9);

        // LARGE also not in map
        Slot largeSlot = new Slot("S2", SlotType.LARGE, 1, 0, 0);
        Ticket largeTicket = new Ticket(car, largeSlot);
        largeTicket.setEntryTimestamp(entryTime);

        double feeLarge = pricing.computeFee(largeTicket, exitTime);
        check("HourlyPricing uses default rate 10.0 for LARGE when not in map",
                Math.abs(feeLarge - 10.0) < 1e-9);
    }

    // ---------- Test 13: Ticket has unique ID, correct vehicle and slot references ----------
    static void testTicketProperties() {
        Vehicle car = new Vehicle("KA-01-1234", VehicleType.CAR);
        Slot slot = new Slot("S1", SlotType.MEDIUM, 1, 10.0, 10.0);
        Ticket ticket = new Ticket(car, slot);

        check("Ticket ID is not null", ticket.getId() != null);
        check("Ticket ID has length 8", ticket.getId().length() == 8);
        check("Ticket references correct vehicle", ticket.getVehicle() == car);
        check("Ticket references correct slot", ticket.getSlot() == slot);
        check("Ticket entryTimestamp is positive", ticket.getEntryTimestamp() > 0);

        // Two tickets should have different IDs (extremely likely with UUID)
        Ticket ticket2 = new Ticket(car, slot);
        check("Two tickets have different IDs",
                !ticket.getId().equals(ticket2.getId()));

        // setEntryTimestamp works
        long customTime = 999999L;
        ticket.setEntryTimestamp(customTime);
        check("Ticket setEntryTimestamp updates correctly",
                ticket.getEntryTimestamp() == customTime);
    }

    // ---------- Test 14: ParkingLot parkVehicle assigns slot and returns ticket ----------
    static void testParkVehicleReturnsTicket() {
        Map<SlotType, Double> rates = new HashMap<>();
        rates.put(SlotType.MEDIUM, 10.0);
        ParkingLot lot = new ParkingLot(new NearestEuclideanSlotStrategy(), new HourlyPricingStrategy(rates));

        Gate gate = new Gate("G1", 1, 0.0, 0.0);
        lot.addGate(gate);

        Slot slot = new Slot("S1", SlotType.MEDIUM, 1, 10.0, 10.0);
        lot.addSlot(slot);

        Vehicle car = new Vehicle("KA-01-1234", VehicleType.CAR);
        Ticket ticket = lot.parkVehicle(car, gate);

        check("parkVehicle returns non-null ticket", ticket != null);
        check("parkVehicle ticket references the vehicle",
                ticket.getVehicle() == car);
        check("parkVehicle ticket references the assigned slot",
                ticket.getSlot() == slot);
        check("parkVehicle marks slot as occupied", slot.isOccupied());
    }

    // ---------- Test 15: ParkingLot parkVehicle returns null when lot is full ----------
    static void testParkVehicleReturnsNullWhenFull() {
        Map<SlotType, Double> rates = new HashMap<>();
        rates.put(SlotType.MEDIUM, 10.0);
        ParkingLot lot = new ParkingLot(new NearestEuclideanSlotStrategy(), new HourlyPricingStrategy(rates));

        Gate gate = new Gate("G1", 1, 0.0, 0.0);
        lot.addGate(gate);

        // Only one MEDIUM slot
        Slot slot = new Slot("S1", SlotType.MEDIUM, 1, 10.0, 10.0);
        lot.addSlot(slot);

        Vehicle car1 = new Vehicle("KA-01-1111", VehicleType.CAR);
        Ticket ticket1 = lot.parkVehicle(car1, gate);
        check("First car parks successfully", ticket1 != null);

        // Second car should fail (lot full for MEDIUM)
        Vehicle car2 = new Vehicle("KA-01-2222", VehicleType.CAR);
        Ticket ticket2 = lot.parkVehicle(car2, gate);
        check("parkVehicle returns null when lot is full", ticket2 == null);
    }

    // ---------- Test 16: ParkingLot processExit frees the slot and returns correct fee ----------
    static void testProcessExitFreesSlotAndReturnsFee() {
        Map<SlotType, Double> rates = new HashMap<>();
        rates.put(SlotType.MEDIUM, 10.0);
        HourlyPricingStrategy pricing = new HourlyPricingStrategy(rates);
        ParkingLot lot = new ParkingLot(new NearestEuclideanSlotStrategy(), pricing);

        Gate gate = new Gate("G1", 1, 0.0, 0.0);
        lot.addGate(gate);

        Slot slot = new Slot("S1", SlotType.MEDIUM, 1, 10.0, 10.0);
        lot.addSlot(slot);

        Vehicle car = new Vehicle("KA-01-1234", VehicleType.CAR);
        Ticket ticket = lot.parkVehicle(car, gate);

        check("Slot is occupied after parking", slot.isOccupied());

        // processExit should free the slot
        double fee = lot.processExit(ticket);

        check("Slot is free after processExit", !slot.isOccupied());
        check("processExit returns a positive fee", fee > 0);
    }

    // ---------- Test 17: Multiple vehicles can park in different slots ----------
    static void testMultipleVehiclesPark() {
        Map<SlotType, Double> rates = new HashMap<>();
        rates.put(SlotType.SMALL, 5.0);
        rates.put(SlotType.MEDIUM, 10.0);
        rates.put(SlotType.LARGE, 20.0);
        ParkingLot lot = new ParkingLot(new NearestEuclideanSlotStrategy(), new HourlyPricingStrategy(rates));

        Gate gate = new Gate("G1", 1, 0.0, 0.0);
        lot.addGate(gate);

        Slot smallSlot = new Slot("S-SM", SlotType.SMALL, 1, 5.0, 5.0);
        Slot medSlot1 = new Slot("S-MD1", SlotType.MEDIUM, 1, 10.0, 10.0);
        Slot medSlot2 = new Slot("S-MD2", SlotType.MEDIUM, 1, 20.0, 20.0);
        Slot largeSlot = new Slot("S-LG", SlotType.LARGE, 1, 15.0, 15.0);
        lot.addSlot(smallSlot);
        lot.addSlot(medSlot1);
        lot.addSlot(medSlot2);
        lot.addSlot(largeSlot);

        Vehicle bike = new Vehicle("MH-01-111", VehicleType.TWO_WHEELER);
        Vehicle car1 = new Vehicle("KA-01-222", VehicleType.CAR);
        Vehicle car2 = new Vehicle("DL-01-333", VehicleType.CAR);
        Vehicle bus = new Vehicle("TN-01-444", VehicleType.BUS);

        Ticket t1 = lot.parkVehicle(bike, gate);
        Ticket t2 = lot.parkVehicle(car1, gate);
        Ticket t3 = lot.parkVehicle(car2, gate);
        Ticket t4 = lot.parkVehicle(bus, gate);

        check("Bike parked successfully", t1 != null);
        check("Car1 parked successfully", t2 != null);
        check("Car2 parked successfully", t3 != null);
        check("Bus parked successfully", t4 != null);

        check("Bike assigned to SMALL slot", t1.getSlot().getSlotType() == SlotType.SMALL);
        check("Car1 assigned to nearest MEDIUM slot (S-MD1)",
                "S-MD1".equals(t2.getSlot().getSlotId()));
        check("Car2 assigned to farther MEDIUM slot (S-MD2)",
                "S-MD2".equals(t3.getSlot().getSlotId()));
        check("Bus assigned to LARGE slot", t4.getSlot().getSlotType() == SlotType.LARGE);

        check("All four slots occupied",
                smallSlot.isOccupied() && medSlot1.isOccupied()
                        && medSlot2.isOccupied() && largeSlot.isOccupied());

        // Now another car should fail
        Vehicle car3 = new Vehicle("GJ-01-555", VehicleType.CAR);
        Ticket t5 = lot.parkVehicle(car3, gate);
        check("No more MEDIUM slots => returns null", t5 == null);
    }

    // ---------- Test 18: ParkingLot displayStatus shows correct counts ----------
    static void testDisplayStatus() {
        Map<SlotType, Double> rates = new HashMap<>();
        rates.put(SlotType.SMALL, 5.0);
        rates.put(SlotType.MEDIUM, 10.0);
        rates.put(SlotType.LARGE, 20.0);
        ParkingLot lot = new ParkingLot(new NearestEuclideanSlotStrategy(), new HourlyPricingStrategy(rates));

        Gate gate = new Gate("G1", 1, 0.0, 0.0);
        lot.addGate(gate);

        lot.addSlot(new Slot("S1", SlotType.SMALL, 1, 5.0, 5.0));
        lot.addSlot(new Slot("S2", SlotType.SMALL, 1, 6.0, 6.0));
        lot.addSlot(new Slot("S3", SlotType.MEDIUM, 1, 10.0, 10.0));
        lot.addSlot(new Slot("S4", SlotType.LARGE, 1, 15.0, 15.0));

        // Capture output of displayStatus with all free
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(baos));
        lot.displayStatus(null);
        System.setOut(oldOut);
        String outputAll = baos.toString();

        check("displayStatus shows SMALL slots available",
                outputAll.contains("SMALL") && outputAll.contains("2"));
        check("displayStatus shows MEDIUM slots available",
                outputAll.contains("MEDIUM") && outputAll.contains("1"));
        check("displayStatus shows LARGE slots available",
                outputAll.contains("LARGE") && outputAll.contains("1"));

        // Park a bike (uses SMALL)
        // Need to restore System.out temporarily for parkVehicle's prints
        baos.reset();
        System.setOut(new PrintStream(baos)); // capture park output too
        Vehicle bike = new Vehicle("MH-01-111", VehicleType.TWO_WHEELER);
        lot.parkVehicle(bike, gate);
        System.setOut(oldOut);

        // Now check status again
        baos.reset();
        System.setOut(new PrintStream(baos));
        lot.displayStatus(null);
        System.setOut(oldOut);
        String outputAfterPark = baos.toString();

        // SMALL should now show 1 (was 2, one is occupied)
        check("displayStatus after parking shows reduced SMALL count",
                outputAfterPark.contains("SMALL") && outputAfterPark.contains("1"));

        // Test filtered displayStatus (only MEDIUM)
        baos.reset();
        System.setOut(new PrintStream(baos));
        lot.displayStatus(SlotType.MEDIUM);
        System.setOut(oldOut);
        String outputFiltered = baos.toString();

        check("displayStatus filtered by MEDIUM shows only MEDIUM",
                outputFiltered.contains("MEDIUM")
                        && !outputFiltered.contains("SMALL")
                        && !outputFiltered.contains("LARGE"));
    }

    // ---------- Main entry point ----------
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  Multi-Level Parking Lot - Test Suite  ");
        System.out.println("========================================\n");

        System.out.println("--- Test 1: Vehicle stores fields ---");
        testVehicleStoresFields();

        System.out.println("\n--- Test 2: Gate stores fields ---");
        testGateStoresFields();

        System.out.println("\n--- Test 3: Slot starts not occupied ---");
        testSlotStartsNotOccupied();

        System.out.println("\n--- Test 4: Slot occupied toggle ---");
        testSlotOccupiedToggle();

        System.out.println("\n--- Test 5: Slot distanceTo ---");
        testSlotDistanceTo();

        System.out.println("\n--- Test 6: Nearest slot assignment ---");
        try {
            testNearestSlotAssignment();
        } catch (Exception e) {
            System.out.println("FAIL: Unexpected exception in test 6: " + e.getMessage());
            failed++;
        }

        System.out.println("\n--- Test 7: Nearest slot skips occupied ---");
        try {
            testNearestSlotSkipsOccupied();
        } catch (Exception e) {
            System.out.println("FAIL: Unexpected exception in test 7: " + e.getMessage());
            failed++;
        }

        System.out.println("\n--- Test 8: Nearest slot throws when none available ---");
        testNearestSlotThrowsWhenNoneAvailable();

        System.out.println("\n--- Test 9: Vehicle-to-slot type mapping ---");
        try {
            testVehicleToSlotMapping();
        } catch (Exception e) {
            System.out.println("FAIL: Unexpected exception in test 9: " + e.getMessage());
            failed++;
        }

        System.out.println("\n--- Test 10: Hourly pricing for 2 hours ---");
        testHourlyPricingTwoHours();

        System.out.println("\n--- Test 11: Hourly pricing minimum 1 hour ---");
        testHourlyPricingMinOneHour();

        System.out.println("\n--- Test 12: Hourly pricing default rate ---");
        testHourlyPricingDefaultRate();

        System.out.println("\n--- Test 13: Ticket properties ---");
        testTicketProperties();

        System.out.println("\n--- Test 14: ParkingLot parkVehicle returns ticket ---");
        testParkVehicleReturnsTicket();

        System.out.println("\n--- Test 15: ParkingLot parkVehicle returns null when full ---");
        testParkVehicleReturnsNullWhenFull();

        System.out.println("\n--- Test 16: ParkingLot processExit frees slot ---");
        testProcessExitFreesSlotAndReturnsFee();

        System.out.println("\n--- Test 17: Multiple vehicles park ---");
        testMultipleVehiclesPark();

        System.out.println("\n--- Test 18: ParkingLot displayStatus ---");
        testDisplayStatus();

        // Summary
        System.out.println("\n========================================");
        System.out.println("           TEST SUMMARY");
        System.out.println("========================================");
        System.out.println("  PASSED: " + passed);
        System.out.println("  FAILED: " + failed);
        System.out.println("  TOTAL:  " + (passed + failed));
        System.out.println("========================================");

        if (failed > 0) {
            System.out.println("  RESULT: SOME TESTS FAILED");
        } else {
            System.out.println("  RESULT: ALL TESTS PASSED");
        }
        System.out.println("========================================");
    }
}
