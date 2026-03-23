import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameFactory {

    public static Game createGame(List<String> playerNames, List<Snake> snakes,
                                  List<Ladder> ladders, String difficulty) {
        Board board = new Board(100, snakes, ladders);
        Dice dice = new Dice(6);

        Queue<Player> players = new LinkedList<>();
        for (String name : playerNames) {
            players.offer(new Player(name));
        }

        MakeMoveStrategy strategy;
        if ("HARD".equalsIgnoreCase(difficulty)) {
            strategy = new HardMoveStrategy();
        } else {
            strategy = new EasyMoveStrategy();
        }

        return new Game(board, players, dice, strategy);
    }
}
