package multi_lvl_parkinglot;

public class Gate {

    private final String gateId;
    private final int floorNumber;
    private final double xCoord;
    private final double yCoord;

    public Gate(String gateId, int floorNumber, double xCoord, double yCoord) {
        this.gateId = gateId;
        this.floorNumber = floorNumber;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public String getGateId() { return gateId; }
    public int getFloorNumber() { return floorNumber; }
    public double getXCoord() { return xCoord; }
    public double getYCoord() { return yCoord; }
}
