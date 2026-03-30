import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingTest {
    static int passed = 0, failed = 0;

    static void check(String name, boolean condition) {
        if (condition) { passed++; System.out.println("  PASS: " + name); }
        else { failed++; System.out.println("  FAIL: " + name); }
    }

    // Helper to build a fresh system for each test group
    static ShowRepository showRepo;
    static TheatreRepository theatreRepo;
    static BookingRepository bookingRepo;
    static ShowService showService;
    static BookingService api;
    static Theatre pvr;
    static Screen screen1;
    static Movie movie1;

    static void freshSetup() {
        showRepo = new ShowRepository();
        theatreRepo = new TheatreRepository();
        bookingRepo = new BookingRepository();
        showService = new ShowService(showRepo);
        api = new BookingService(showService, bookingRepo, theatreRepo, showRepo);

        List<Seat> seats = new ArrayList<>();
        seats.add(new Seat("A1", 1, 1, SeatType.GOLD));
        seats.add(new Seat("A2", 1, 2, SeatType.GOLD));
        seats.add(new Seat("A3", 1, 3, SeatType.GOLD));
        seats.add(new Seat("B1", 2, 1, SeatType.SILVER));
        seats.add(new Seat("B2", 2, 2, SeatType.SILVER));
        seats.add(new Seat("C1", 3, 1, SeatType.PLATINUM));

        screen1 = new Screen("SCR-1", "Audi 1", seats);
        pvr = new Theatre("T1", "PVR Cinemas", "Bangalore", List.of(screen1));
        theatreRepo.addTheatre(pvr);
        movie1 = new Movie("M1", "Interstellar", "English", "Sci-Fi", 169);
    }

    // ====== DOMAIN MODEL TESTS ======

    static void testSeatTypes() {
        System.out.println("\n[1] Seat Types & Pricing");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        List<ShowSeat> allSeats = show.getShowSeats();
        check("6 show seats created", allSeats.size() == 6);

        // check pricing
        for (ShowSeat ss : allSeats) {
            if (ss.getSeat().getType() == SeatType.GOLD)
                check("GOLD seat " + ss.getSeat().getId() + " priced at 500", ss.getPrice() == 500);
            if (ss.getSeat().getType() == SeatType.SILVER)
                check("SILVER seat " + ss.getSeat().getId() + " priced at 200", ss.getPrice() == 200);
            if (ss.getSeat().getType() == SeatType.PLATINUM)
                check("PLATINUM seat " + ss.getSeat().getId() + " priced at 800", ss.getPrice() == 800);
        }
    }

    static void testShowEndTime() {
        System.out.println("\n[2] Show End Time Calculation");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        LocalDateTime expected = LocalDateTime.of(2026, 4, 1, 16, 49); // 14:00 + 169 min
        check("end time correct", show.getEndTime().equals(expected));
    }

    static void testMovieEquality() {
        System.out.println("\n[3] Movie Equality (by ID)");
        Movie m1 = new Movie("M1", "Interstellar", "English", "Sci-Fi", 169);
        Movie m2 = new Movie("M1", "Interstellar", "English", "Sci-Fi", 169);
        Movie m3 = new Movie("M2", "Dune", "English", "Sci-Fi", 166);
        check("same id equals", m1.equals(m2));
        check("different id not equals", !m1.equals(m3));
        check("same hashcode", m1.hashCode() == m2.hashCode());
    }

    static void testScreenImmutableSeats() {
        System.out.println("\n[4] Screen Returns Immutable Seat List");
        freshSetup();
        boolean threw = false;
        try {
            screen1.getSeats().add(new Seat("Z1", 9, 9, SeatType.GOLD));
        } catch (UnsupportedOperationException e) {
            threw = true;
        }
        check("cannot modify screen seats", threw);
    }

    // ====== BOOKING API TESTS ======

    static void testBasicBooking() {
        System.out.println("\n[5] Basic Booking");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        Booking b = api.bookTickets("S1", List.of("A1", "A2"), "user1");
        check("booking not null", b != null);
        check("booking status CONFIRMED", b.getStatus() == BookingStatus.CONFIRMED);
        check("2 seats booked", b.getBookedSeats().size() == 2);
        check("total is 1000 (2x GOLD)", b.getPayment().getAmount() == 1000);
        check("payment status SUCCESS", b.getPayment().getStatus() == PaymentStatus.SUCCESS);
        check("userId correct", b.getUserId().equals("user1"));
        check("bookingId starts with BKG-", b.getBookingId().startsWith("BKG-"));
    }

    static void testBookMultipleSeatTypes() {
        System.out.println("\n[6] Book Multiple Seat Types");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        Booking b = api.bookTickets("S1", List.of("A1", "B1", "C1"), "user1");
        double expectedTotal = 500 + 200 + 800; // GOLD + SILVER + PLATINUM
        check("total is 1500", b.getPayment().getAmount() == expectedTotal);
    }

    static void testDoubleBookingFails() {
        System.out.println("\n[7] Double Booking Same Seat Fails");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        api.bookTickets("S1", List.of("A1"), "user1");
        boolean threw = false;
        try {
            api.bookTickets("S1", List.of("A1"), "user2"); // should fail
        } catch (RuntimeException e) {
            threw = true;
            check("error mentions seat A1", e.getMessage().contains("A1"));
        }
        check("double booking threw exception", threw);
    }

    static void testBookNonExistentSeat() {
        System.out.println("\n[8] Book Non-Existent Seat");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        boolean threw = false;
        try {
            api.bookTickets("S1", List.of("Z99"), "user1");
        } catch (RuntimeException e) {
            threw = true;
            check("error mentions seat Z99", e.getMessage().contains("Z99"));
        }
        check("non-existent seat threw exception", threw);
    }

    static void testPartialBookingAtomicity() {
        System.out.println("\n[9] Partial Booking Atomicity (one bad seat in batch)");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        api.bookTickets("S1", List.of("A1"), "user1"); // book A1 first

        boolean threw = false;
        try {
            api.bookTickets("S1", List.of("A2", "A1"), "user2"); // A2 free, A1 taken
        } catch (RuntimeException e) {
            threw = true;
        }
        check("booking failed", threw);

        // check A2 was NOT booked (atomicity) - it should still be available
        List<ShowSeat> available = showService.getAvailableSeats("S1");
        boolean a2Available = available.stream().anyMatch(ss -> ss.getSeat().getId().equals("A2"));
        check("A2 still available (atomic rollback)", a2Available);
    }

    // ====== CANCELLATION TESTS ======

    static void testCancellation() {
        System.out.println("\n[10] Cancellation & Refund");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        Booking b = api.bookTickets("S1", List.of("A1", "A2"), "user1");
        String bookingId = b.getBookingId();
        api.cancelBooking(bookingId);
        check("status CANCELLED", b.getStatus() == BookingStatus.CANCELLED);
        check("payment REFUNDED", b.getPayment().getStatus() == PaymentStatus.REFUNDED);
    }

    static void testCancelledSeatsBecomeAvailable() {
        System.out.println("\n[11] Cancelled Seats Become Available");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        Booking b = api.bookTickets("S1", List.of("A1"), "user1");
        int availBefore = showService.getAvailableSeats("S1").size();
        api.cancelBooking(b.getBookingId());
        int availAfter = showService.getAvailableSeats("S1").size();
        check("one more seat available after cancel", availAfter == availBefore + 1);

        // can rebook the same seat
        Booking b2 = api.bookTickets("S1", List.of("A1"), "user2");
        check("rebooked A1 successfully", b2 != null);
        check("rebooked booking confirmed", b2.getStatus() == BookingStatus.CONFIRMED);
    }

    static void testDoubleCancelFails() {
        System.out.println("\n[12] Double Cancel Fails");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        Booking b = api.bookTickets("S1", List.of("A1"), "user1");
        api.cancelBooking(b.getBookingId());
        boolean threw = false;
        try {
            api.cancelBooking(b.getBookingId());
        } catch (RuntimeException e) {
            threw = true;
            check("error says already cancelled", e.getMessage().contains("already cancelled"));
        }
        check("double cancel threw exception", threw);
    }

    static void testCancelNonExistentBooking() {
        System.out.println("\n[13] Cancel Non-Existent Booking");
        freshSetup();
        boolean threw = false;
        try {
            api.cancelBooking("BKG-FAKE");
        } catch (RuntimeException e) {
            threw = true;
            check("error says not found", e.getMessage().contains("not found"));
        }
        check("non-existent cancel threw exception", threw);
    }

    // ====== QUERY API TESTS ======

    static void testShowTheatres() {
        System.out.println("\n[14] showTheatres API");
        freshSetup();
        List<Theatre> result = api.showTheatres("Bangalore");
        check("found 1 theatre in Bangalore", result.size() == 1);
        check("name is PVR", result.get(0).getName().equals("PVR Cinemas"));

        List<Theatre> empty = api.showTheatres("Mumbai");
        check("no theatres in Mumbai", empty.isEmpty());
    }

    static void testShowTheatresCaseInsensitive() {
        System.out.println("\n[15] showTheatres Case Insensitive");
        freshSetup();
        List<Theatre> r1 = api.showTheatres("bangalore");
        List<Theatre> r2 = api.showTheatres("BANGALORE");
        check("lowercase works", r1.size() == 1);
        check("uppercase works", r2.size() == 1);
    }

    static void testShowMovies() {
        System.out.println("\n[16] showMovies API");
        freshSetup();
        Movie m2 = new Movie("M2", "Dune", "English", "Sci-Fi", 166);
        Show s1 = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        Show s2 = new Show("S2", m2, screen1, pvr, LocalDateTime.of(2026, 4, 1, 18, 0));
        showService.addShow(s1);
        showService.addShow(s2);

        List<Movie> movies = api.showMovies("Bangalore");
        check("found 2 movies", movies.size() == 2);
    }

    static void testShowMoviesNoDuplicates() {
        System.out.println("\n[17] showMovies No Duplicates");
        freshSetup();
        // same movie, different show times
        List<Seat> seats2 = List.of(new Seat("X1", 1, 1, SeatType.SILVER));
        Screen screen2 = new Screen("SCR-2", "Audi 2", seats2);
        Theatre pvr2 = new Theatre("T1", "PVR Cinemas", "Bangalore", List.of(screen1, screen2));
        theatreRepo = new TheatreRepository();
        theatreRepo.addTheatre(pvr2);
        api = new BookingService(showService, bookingRepo, theatreRepo, showRepo);

        Show s1 = new Show("S1", movie1, screen1, pvr2, LocalDateTime.of(2026, 4, 1, 14, 0));
        Show s2 = new Show("S2", movie1, screen2, pvr2, LocalDateTime.of(2026, 4, 1, 18, 0));
        showService.addShow(s1);
        showService.addShow(s2);

        List<Movie> movies = api.showMovies("Bangalore");
        check("same movie not duplicated", movies.size() == 1);
    }

    static void testShowMoviesEmptyCity() {
        System.out.println("\n[18] showMovies Empty City");
        freshSetup();
        List<Movie> movies = api.showMovies("Delhi");
        check("no movies in Delhi", movies.isEmpty());
    }

    // ====== SHOW OVERLAP TESTS ======

    static void testOverlappingShowRejected() {
        System.out.println("\n[19] Overlapping Show on Same Screen Rejected");
        freshSetup();
        Show s1 = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(s1);
        // s1 ends at 16:49. Try adding a show at 16:00 (overlaps)
        Show s2 = new Show("S2", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 16, 0));
        boolean threw = false;
        try {
            showService.addShow(s2);
        } catch (RuntimeException e) {
            threw = true;
            check("error mentions screen", e.getMessage().contains("Audi 1"));
        }
        check("overlapping show rejected", threw);
    }

    static void testNonOverlappingShowAccepted() {
        System.out.println("\n[20] Non-Overlapping Show Accepted");
        freshSetup();
        Show s1 = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(s1);
        // s1 ends at 16:49. Show at 17:00 should be fine
        Show s2 = new Show("S2", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 17, 0));
        boolean ok = true;
        try {
            showService.addShow(s2);
        } catch (RuntimeException e) {
            ok = false;
        }
        check("non-overlapping show accepted", ok);
    }

    static void testAvailableSeats() {
        System.out.println("\n[21] Available Seats Tracking");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        List<ShowSeat> avail0 = showService.getAvailableSeats("S1");
        check("all 6 seats available initially", avail0.size() == 6);

        api.bookTickets("S1", List.of("A1", "B1"), "user1");
        List<ShowSeat> avail1 = showService.getAvailableSeats("S1");
        check("4 seats after booking 2", avail1.size() == 4);

        api.bookTickets("S1", List.of("A2", "A3", "B2", "C1"), "user2");
        List<ShowSeat> avail2 = showService.getAvailableSeats("S1");
        check("0 seats after booking all", avail2.size() == 0);
    }

    static void testBookAllSeatsThenFail() {
        System.out.println("\n[22] Book All Seats Then Next Fails");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        api.bookTickets("S1", List.of("A1", "A2", "A3", "B1", "B2", "C1"), "user1");
        boolean threw = false;
        try {
            api.bookTickets("S1", List.of("A1"), "user2");
        } catch (RuntimeException e) {
            threw = true;
        }
        check("fully booked show rejects new booking", threw);
    }

    // ====== CONCURRENCY TESTS ======

    static void testConcurrentBookingDifferentSeats() {
        System.out.println("\n[23] Concurrent Booking Different Seats (both succeed)");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        AtomicInteger success = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            try { api.bookTickets("S1", List.of("A1"), "u1"); success.incrementAndGet(); }
            catch (Exception e) {}
            latch.countDown();
        });
        Thread t2 = new Thread(() -> {
            try { api.bookTickets("S1", List.of("B1"), "u2"); success.incrementAndGet(); }
            catch (Exception e) {}
            latch.countDown();
        });
        t1.start(); t2.start();
        try { latch.await(); } catch (InterruptedException ignored) {}
        check("both bookings for different seats succeeded", success.get() == 2);
    }

    static void testConcurrentBookingSameSeat() {
        System.out.println("\n[24] Concurrent Booking Same Seat (exactly one wins)");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger failure = new AtomicInteger(0);
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(10);

        // 10 threads all racing for seat A1
        for (int i = 0; i < 10; i++) {
            final String user = "user-" + i;
            new Thread(() -> {
                try { startGun.await(); } catch (InterruptedException ignored) {}
                try {
                    api.bookTickets("S1", List.of("A1"), user);
                    success.incrementAndGet();
                } catch (RuntimeException e) {
                    failure.incrementAndGet();
                }
                done.countDown();
            }).start();
        }

        startGun.countDown(); // fire!
        try { done.await(); } catch (InterruptedException ignored) {}

        check("exactly 1 thread won", success.get() == 1);
        check("exactly 9 threads failed", failure.get() == 9);
    }

    static void testConcurrentBookingMultipleShows() {
        System.out.println("\n[25] Concurrent Booking Across Different Shows (no blocking)");
        freshSetup();
        Show s1 = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));

        List<Seat> seats2 = List.of(new Seat("X1", 1, 1, SeatType.SILVER));
        Screen screen2 = new Screen("SCR-2", "Audi 2", seats2);
        Movie m2 = new Movie("M2", "Dune", "English", "Sci-Fi", 166);
        Show s2 = new Show("S2", m2, screen2, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));

        showService.addShow(s1);
        showService.addShow(s2);

        AtomicInteger success = new AtomicInteger(0);
        CountDownLatch done = new CountDownLatch(2);

        new Thread(() -> {
            try { api.bookTickets("S1", List.of("A1"), "u1"); success.incrementAndGet(); }
            catch (Exception e) {}
            done.countDown();
        }).start();
        new Thread(() -> {
            try { api.bookTickets("S2", List.of("X1"), "u2"); success.incrementAndGet(); }
            catch (Exception e) {}
            done.countDown();
        }).start();

        try { done.await(); } catch (InterruptedException ignored) {}
        check("both shows booked concurrently", success.get() == 2);
    }

    // ====== MULTI-THEATRE TESTS ======

    static void testMultipleTheatres() {
        System.out.println("\n[26] Multiple Theatres in Different Cities");
        freshSetup();
        List<Seat> seats2 = List.of(new Seat("D1", 1, 1, SeatType.GOLD));
        Screen scr2 = new Screen("SCR-2", "Screen 1", seats2);
        Theatre inox = new Theatre("T2", "INOX", "Mumbai", List.of(scr2));
        theatreRepo.addTheatre(inox);

        check("1 theatre in Bangalore", api.showTheatres("Bangalore").size() == 1);
        check("1 theatre in Mumbai", api.showTheatres("Mumbai").size() == 1);
        check("0 theatres in Delhi", api.showTheatres("Delhi").size() == 0);
    }

    static void testBookingImmutableSeatList() {
        System.out.println("\n[27] Booking Seat List is Immutable");
        freshSetup();
        Show show = new Show("S1", movie1, screen1, pvr, LocalDateTime.of(2026, 4, 1, 14, 0));
        showService.addShow(show);
        Booking b = api.bookTickets("S1", List.of("A1"), "user1");
        boolean threw = false;
        try {
            b.getBookedSeats().add(show.getShowSeats().get(0));
        } catch (UnsupportedOperationException e) {
            threw = true;
        }
        check("booking seat list is immutable", threw);
    }

    // ====== MAIN ======

    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("  MOVIE TICKET BOOKING - FULL TEST SUITE");
        System.out.println("=============================================");

        testSeatTypes();
        testShowEndTime();
        testMovieEquality();
        testScreenImmutableSeats();
        testBasicBooking();
        testBookMultipleSeatTypes();
        testDoubleBookingFails();
        testBookNonExistentSeat();
        testPartialBookingAtomicity();
        testCancellation();
        testCancelledSeatsBecomeAvailable();
        testDoubleCancelFails();
        testCancelNonExistentBooking();
        testShowTheatres();
        testShowTheatresCaseInsensitive();
        testShowMovies();
        testShowMoviesNoDuplicates();
        testShowMoviesEmptyCity();
        testOverlappingShowRejected();
        testNonOverlappingShowAccepted();
        testAvailableSeats();
        testBookAllSeatsThenFail();
        testConcurrentBookingDifferentSeats();
        testConcurrentBookingSameSeat();
        testConcurrentBookingMultipleShows();
        testMultipleTheatres();
        testBookingImmutableSeatList();

        System.out.println("\n=============================================");
        System.out.println("  PASSED: " + passed + " / " + (passed + failed));
        System.out.println("  FAILED: " + failed);
        System.out.println("=============================================");
        if (failed > 0) System.exit(1);
    }
}
