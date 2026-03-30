public class Seat {
    private String id;
    private int row;
    private int col;
    private SeatType type;

    public Seat(String id, int row, int col, SeatType type) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public String getId() { return id; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public SeatType getType() { return type; }

    @Override
    public String toString() { return id + " (" + type + ")"; }
}
