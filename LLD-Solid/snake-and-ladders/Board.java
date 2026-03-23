import java.util.List;

public class Board {

    private final int totalSquares;
    private final List<Snake> snakes;
    private final List<Ladder> ladders;

    public Board(int totalSquares, List<Snake> snakes, List<Ladder> ladders) {
        this.totalSquares = totalSquares;
        this.snakes = snakes;
        this.ladders = ladders;
    }

    public int getTotalSquares() { return totalSquares; }

    public int resolvePosition(int position) {
        for (Snake snake : snakes) {
            if (snake.getHead() == position) {
                return snake.getTail();
            }
        }
        for (Ladder ladder : ladders) {
            if (ladder.getBottom() == position) {
                return ladder.getTop();
            }
        }
        return position;
    }
}
