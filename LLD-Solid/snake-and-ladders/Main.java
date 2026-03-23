import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Snake> snakes = Arrays.asList(
                new Snake(16, 6),
                new Snake(47, 26),
                new Snake(49, 11),
                new Snake(99, 1)
        );

        List<Ladder> ladders = Arrays.asList(
                new Ladder(1, 38),
                new Ladder(4, 14),
                new Ladder(21, 42),
                new Ladder(80, 98)
        );

        List<String> playerNames = Arrays.asList("Max Verstappen", "Charles Leclerc");

        Game game = GameFactory.createGame(playerNames, snakes, ladders, "EASY");
        game.play();
    }
}
