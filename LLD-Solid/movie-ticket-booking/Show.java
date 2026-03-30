import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

// links a movie to a screen at a time, creates ShowSeats
public class Show {
    private String id;
    private Movie movie;
    private Screen screen;
    private Theatre theatre;
    private LocalDateTime startTime;
    private List<ShowSeat> showSeats;

    public Show(String id, Movie movie, Screen screen, Theatre theatre, LocalDateTime startTime) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.theatre = theatre;
        this.startTime = startTime;

        // create a ShowSeat for each physical seat in the screen
        this.showSeats = new ArrayList<>();
        for (Seat s : screen.getSeats()) {
            showSeats.add(new ShowSeat(s));
        }
    }

    public String getId() { return id; }
    public Movie getMovie() { return movie; }
    public Screen getScreen() { return screen; }
    public Theatre getTheatre() { return theatre; }
    public LocalDateTime getStartTime() { return startTime; }
    public List<ShowSeat> getShowSeats() { return showSeats; }

    // end time = start + movie duration
    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(movie.getDurationMinutes());
    }
}
