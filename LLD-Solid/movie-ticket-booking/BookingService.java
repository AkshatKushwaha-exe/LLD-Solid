import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

// the API layer
public class BookingService {
    private final ShowService showService;
    private final BookingRepository bookingRepo;
    private final TheatreRepository theatreRepo;
    private final ShowRepository showRepo;

    public BookingService(ShowService showService, BookingRepository bookingRepo,
                          TheatreRepository theatreRepo, ShowRepository showRepo) {
        this.showService = showService;
        this.bookingRepo = bookingRepo;
        this.theatreRepo = theatreRepo;
        this.showRepo = showRepo;
    }

    // API: bookTickets(showId, seatIds, userId) -> Booking
    public Booking bookTickets(String showId, List<String> seatIds, String userId) {
        List<ShowSeat> locked = showService.lockAndValidateSeats(showId, seatIds);
        double total = locked.stream().mapToDouble(ShowSeat::getPrice).sum();

        Show show = showRepo.getShow(showId);
        Booking booking = new Booking(show, locked, userId, total);
        bookingRepo.save(booking);

        System.out.println("Booking " + booking.getBookingId() + " confirmed for " + userId
            + " | Seats: " + seatIds + " | Total: Rs." + total);
        return booking;
    }

    // API: showTheatres(city) -> List<Theatre>
    public List<Theatre> showTheatres(String city) {
        return theatreRepo.getTheatresByCity(city);
    }

    // API: showMovies(city) -> unique movies playing in that city
    public List<Movie> showMovies(String city) {
        List<Theatre> theatres = theatreRepo.getTheatresByCity(city);
        List<Movie> movies = new ArrayList<>();

        for (Theatre t : theatres) {
            List<Show> shows = showRepo.getShowsByTheatre(t.getId());
            for (Show s : shows) {
                if (!movies.contains(s.getMovie())) {
                    movies.add(s.getMovie());
                }
            }
        }
        return movies;
    }

    // API: cancelBooking(bookingId)
    public void cancelBooking(String bookingId) {
        Booking booking = bookingRepo.getBooking(bookingId);
        if (booking == null) {
            throw new RuntimeException("Booking " + bookingId + " not found");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking " + bookingId + " is already cancelled");
        }

        showService.unlockSeats(booking.getShow().getId(), booking.getBookedSeats());
        booking.cancel();
        System.out.println("Booking " + bookingId + " cancelled. Refund processed.");
    }
}
