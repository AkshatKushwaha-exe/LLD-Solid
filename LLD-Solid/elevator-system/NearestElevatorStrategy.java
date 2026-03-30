import java.util.List;

public class NearestElevatorStrategy implements ElevatorDispatchStrategy {

    @Override
    public Elevator dispatch(Request request, List<Elevator> elevators) {
        Elevator best = null;
        int bestScore = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            if (!e.isAvailable()) continue;

            int distance = Math.abs(e.getCurrentFloor() - request.getTargetFloor());
            int score = distance;

            // prefer idle elevators or ones heading the right way
            if (e.getState() == ElevatorState.IDLE) {
                score = distance;
            } else if (request.getDirection() == Direction.UP
                    && e.getState() == ElevatorState.MOVING_UP
                    && e.getCurrentFloor() <= request.getTargetFloor()) {
                score = distance;
            } else if (request.getDirection() == Direction.DOWN
                    && e.getState() == ElevatorState.MOVING_DOWN
                    && e.getCurrentFloor() >= request.getTargetFloor()) {
                score = distance;
            } else {
                score = distance + 1000; // penalty for wrong direction
            }

            if (score < bestScore) {
                bestScore = score;
                best = e;
            }
        }

        if (best == null) {
            System.out.println("No elevator available for floor " + request.getTargetFloor());
        }
        return best;
    }
}
