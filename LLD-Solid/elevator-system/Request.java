public class Request {
    private final int targetFloor;
    private final Direction direction; // null for internal requests

    public Request(int targetFloor, Direction direction) {
        this.targetFloor = targetFloor;
        this.direction = direction;
    }

    public Request(int targetFloor) {
        this(targetFloor, null);
    }

    public int getTargetFloor() { return targetFloor; }
    public Direction getDirection() { return direction; }
}
