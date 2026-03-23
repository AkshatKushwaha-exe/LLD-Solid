package multi_lvl_parkinglot;

import java.util.UUID;

public class Ticket {

    private final String id;
    private final Vehicle vehicle;
    private final Slot slot;
    private long entryTimestamp;

    public Ticket(Vehicle vehicle, Slot slot) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTimestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public Slot getSlot() { return slot; }
    public long getEntryTimestamp() { return entryTimestamp; }

    public void setEntryTimestamp(long timestamp) { this.entryTimestamp = timestamp; }
}
