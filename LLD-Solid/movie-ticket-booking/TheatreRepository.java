import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TheatreRepository {
    private final Map<String, Theatre> theatres = new HashMap<>();

    public void addTheatre(Theatre t) {
        theatres.put(t.getId(), t);
    }

    public Theatre getTheatre(String id) {
        return theatres.get(id);
    }

    public List<Theatre> getTheatresByCity(String city) {
        return theatres.values().stream()
            .filter(t -> t.getCity().equalsIgnoreCase(city))
            .collect(Collectors.toList());
    }
}
