import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Screen {
    private String id;
    private String name;
    private List<Seat> seats;

    public Screen(String id, String name, List<Seat> seats) {
        this.id = id;
        this.name = name;
        this.seats = new ArrayList<>(seats);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Seat> getSeats() { return Collections.unmodifiableList(seats); }
}
