import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class Booking {
    private String bookingId;
    private Show show;
    private List<ShowSeat> bookedSeats;
    private String userId;
    private BookingStatus status;
    private Payment payment;

    public Booking(Show show, List<ShowSeat> bookedSeats, String userId, double totalAmount) {
        this.bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 6);
        this.show = show;
        this.bookedSeats = new ArrayList<>(bookedSeats);
        this.userId = userId;
        this.status = BookingStatus.CONFIRMED;
        this.payment = new Payment(totalAmount);
    }

    public String getBookingId() { return bookingId; }
    public Show getShow() { return show; }
    public List<ShowSeat> getBookedSeats() { return Collections.unmodifiableList(bookedSeats); }
    public String getUserId() { return userId; }
    public BookingStatus getStatus() { return status; }
    public Payment getPayment() { return payment; }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        payment.refund();
    }
}
