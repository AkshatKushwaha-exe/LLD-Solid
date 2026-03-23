import java.util.Queue;

public class Game {

    private final Board board;
    private final Queue<Player> playerQueue;
    private final Dice dice;
    private final MakeMoveStrategy moveStrategy;

    public Game(Board board, Queue<Player> playerQueue, Dice dice, MakeMoveStrategy moveStrategy) {
        this.board = board;
        this.playerQueue = playerQueue;
        this.dice = dice;
        this.moveStrategy = moveStrategy;
    }

    public void play() {
        boolean hasWinner = false;

        while (!hasWinner && !playerQueue.isEmpty()) {
            Player active = playerQueue.poll();
            hasWinner = moveStrategy.executeTurn(active, board, dice);

            if (hasWinner) {
                System.out.println(active.getName() + " wins the game!");
            } else {
                playerQueue.offer(active);
            }
        }
    }
}
