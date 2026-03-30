import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

// handles concurrency with per-show ReentrantLock
public class ShowService {
    private final ShowRepository showRepo;
    private final ConcurrentHashMap<String, ReentrantLock> showLocks = new ConcurrentHashMap<>();

    public ShowService(ShowRepository showRepo) {
        this.showRepo = showRepo;
    }

    // synchronized so two admins cant add conflicting shows at the same time
    public synchronized void addShow(Show show) {
        List<Show> existing = showRepo.getShowsByTheatre(show.getTheatre().getId());
        for (Show s : existing) {
            if (s.getScreen().getId().equals(show.getScreen().getId())) {
                // check time overlap
                boolean overlaps = show.getStartTime().isBefore(s.getEndTime())
                                && s.getStartTime().isBefore(show.getEndTime());
                if (overlaps) {
                    throw new RuntimeException("Screen " + show.getScreen().getName()
                        + " already has a show at this time!");
                }
            }
        }
        showRepo.addShow(show);
        showLocks.put(show.getId(), new ReentrantLock());
        System.out.println("Show added: " + show.getMovie().getTitle() + " at "
            + show.getTheatre().getName() + " " + show.getScreen().getName()
            + " [" + show.getStartTime() + "]");
    }

    public List<ShowSeat> getAvailableSeats(String showId) {
        Show show = showRepo.getShow(showId);
        return show.getShowSeats().stream()
            .filter(ss -> !ss.isBooked())
            .collect(Collectors.toList());
    }

    // locks the show, validates all seats are free, marks them booked
    public List<ShowSeat> lockAndValidateSeats(String showId, List<String> seatIds) {
        ReentrantLock lock = showLocks.computeIfAbsent(showId, k -> new ReentrantLock());
        lock.lock();
        try {
            Show show = showRepo.getShow(showId);
            List<ShowSeat> toBook = new ArrayList<>();

            for (String seatId : seatIds) {
                ShowSeat found = null;
                for (ShowSeat ss : show.getShowSeats()) {
                    if (ss.getSeat().getId().equals(seatId)) {
                        found = ss;
                        break;
                    }
                }
                if (found == null) {
                    throw new RuntimeException("Seat " + seatId + " does not exist in this show");
                }
                if (found.isBooked()) {
                    throw new RuntimeException("Seat " + seatId + " is already booked!");
                }
                toBook.add(found);
            }

            // all seats are free, book them
            for (ShowSeat ss : toBook) {
                ss.book();
            }
            return toBook;
        } finally {
            lock.unlock();
        }
    }

    // used during cancellation to free up seats
    public void unlockSeats(String showId, List<ShowSeat> seats) {
        ReentrantLock lock = showLocks.computeIfAbsent(showId, k -> new ReentrantLock());
        lock.lock();
        try {
            for (ShowSeat ss : seats) {
                ss.unbook();
            }
        } finally {
            lock.unlock();
        }
    }
}
