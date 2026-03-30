import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // setup repos
        TheatreRepository theatreRepo = new TheatreRepository();
        ShowRepository showRepo = new ShowRepository();
        BookingRepository bookingRepo = new BookingRepository();
        ShowService showService = new ShowService(showRepo);
        BookingService api = new BookingService(showService, bookingRepo, theatreRepo, showRepo);

        // create seats
        List<Seat> seats = new ArrayList<>();
        seats.add(new Seat("A1", 1, 1, SeatType.GOLD));
        seats.add(new Seat("A2", 1, 2, SeatType.GOLD));
        seats.add(new Seat("A3", 1, 3, SeatType.GOLD));
        seats.add(new Seat("B1", 2, 1, SeatType.SILVER));
        seats.add(new Seat("B2", 2, 2, SeatType.SILVER));
        seats.add(new Seat("C1", 3, 1, SeatType.PLATINUM));

        Screen screen1 = new Screen("SCR-1", "Audi 1", seats);
        Theatre pvr = new Theatre("T1", "PVR Cinemas", "Bangalore", List.of(screen1));
        theatreRepo.addTheatre(pvr);

        Movie movie1 = new Movie("M1", "Interstellar", "English", "Sci-Fi", 169);
        Movie movie2 = new Movie("M2", "Dune Part Two", "English", "Sci-Fi", 166);

        Show show1 = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 3, 30, 14, 0));
        Show show2 = new Show("S2", movie2, screen1, pvr, LocalDateTime.of(2026, 3, 30, 18, 0));
        showService.addShow(show1);
        showService.addShow(show2);

        // API: showMovies
        System.out.println("\n=== Movies in Bangalore ===");
        List<Movie> movies = api.showMovies("Bangalore");
        movies.forEach(m -> System.out.println("  " + m));

        // API: showTheatres
        System.out.println("\n=== Theatres in Bangalore ===");
        List<Theatre> theatres = api.showTheatres("Bangalore");
        theatres.forEach(t -> System.out.println("  " + t));

        // API: book tickets
        System.out.println("\n=== Booking ===");
        Booking b1 = api.bookTickets("S1", List.of("A1", "A2"), "user-akshat");

        // concurrency: two users race for same seat
        System.out.println("\n=== Concurrency Test: 2 users racing for seat A3 ===");
        Thread t1 = new Thread(() -> {
            try {
                Booking b = api.bookTickets("S1", List.of("A3"), "user-raj");
                System.out.println("  Thread-1 got booking: " + b.getBookingId());
            } catch (Exception e) {
                System.out.println("  Thread-1 failed: " + e.getMessage());
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                Booking b = api.bookTickets("S1", List.of("A3"), "user-priya");
                System.out.println("  Thread-2 got booking: " + b.getBookingId());
            } catch (Exception e) {
                System.out.println("  Thread-2 failed: " + e.getMessage());
            }
        });
        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException ignored) {}

        // cancellation
        System.out.println("\n=== Cancellation ===");
        api.cancelBooking(b1.getBookingId());

        // rebook after cancellation
        System.out.println("\n=== Rebooking after cancellation ===");
        Booking b3 = api.bookTickets("S1", List.of("A1"), "user-new");

        System.out.println("\nDone.");
    }
}
