import java.util.List;

public interface ElevatorDispatchStrategy {
    Elevator dispatch(Request request, List<Elevator> elevators);
}
