public class Display {
    private int currentFloor;
    private Direction direction;

    public Display() {
        this.currentFloor = 0;
        this.direction = null;
    }

    public void update(int floor, Direction dir) {
        this.currentFloor = floor;
        this.direction = dir;
        System.out.println("  [Display] Floor " + floor + (dir != null ? " | " + dir : " | IDLE"));
    }

    public int getCurrentFloor() { return currentFloor; }
    public Direction getDirection() { return direction; }
}
