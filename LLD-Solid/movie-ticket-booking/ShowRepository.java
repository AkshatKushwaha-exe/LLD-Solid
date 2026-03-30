import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ShowRepository {
    private final Map<String, Show> shows = new HashMap<>();
    private final Map<String, List<Show>> theatreShows = new HashMap<>();

    public void addShow(Show show) {
        shows.put(show.getId(), show);
        theatreShows.computeIfAbsent(show.getTheatre().getId(), k -> new ArrayList<>()).add(show);
    }

    public Show getShow(String showId) {
        return shows.get(showId);
    }

    public List<Show> getShowsByTheatre(String theatreId) {
        return theatreShows.getOrDefault(theatreId, new ArrayList<>());
    }
}
