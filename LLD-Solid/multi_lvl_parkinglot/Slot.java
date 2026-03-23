package multi_lvl_parkinglot;

public class Slot {

    private final String slotId;
    private final SlotType slotType;
    private final int floorNumber;
    private final double xCoord;
    private final double yCoord;
    private boolean occupied;

    public Slot(String slotId, SlotType slotType, int floorNumber, double xCoord, double yCoord) {
        this.slotId = slotId;
        this.slotType = slotType;
        this.floorNumber = floorNumber;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.occupied = false;
    }

    public String getSlotId() { return slotId; }
    public SlotType getSlotType() { return slotType; }
    public int getFloorNumber() { return floorNumber; }
    public boolean isOccupied() { return occupied; }

    public void markOccupied() { this.occupied = true; }
    public void markFree() { this.occupied = false; }

    public double distanceTo(Gate gate) {
        double dx = this.xCoord - gate.getXCoord();
        double dy = this.yCoord - gate.getYCoord();
        double floorDiff = (this.floorNumber - gate.getFloorNumber()) * 10.0;
        return Math.sqrt(dx * dx + dy * dy + floorDiff * floorDiff);
    }
}
