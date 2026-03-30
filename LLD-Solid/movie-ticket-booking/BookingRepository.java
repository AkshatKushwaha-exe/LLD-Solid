import java.util.Map;
import java.util.HashMap;

public class BookingRepository {
    private final Map<String, Booking> bookings = new HashMap<>();

    public void save(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
    }

    public Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }
}
