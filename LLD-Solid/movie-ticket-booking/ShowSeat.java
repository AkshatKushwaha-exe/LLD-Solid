// per-show wrapper around a physical Seat
public class ShowSeat {
    private Seat seat;
    private boolean booked;
    private double price;

    public ShowSeat(Seat seat) {
        this.seat = seat;
        this.booked = false;
        // pricing based on seat type
        switch (seat.getType()) {
            case SILVER:   this.price = 200; break;
            case GOLD:     this.price = 500; break;
            case PLATINUM: this.price = 800; break;
        }
    }

    public Seat getSeat() { return seat; }
    public boolean isBooked() { return booked; }
    public double getPrice() { return price; }

    public void book() { this.booked = true; }
    public void unbook() { this.booked = false; }
}
