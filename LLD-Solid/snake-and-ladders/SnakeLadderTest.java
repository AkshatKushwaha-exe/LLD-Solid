import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SnakeLadderTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void report(String testName, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("PASS: " + testName);
        } else {
            failed++;
            System.out.println("FAIL: " + testName);
        }
    }

    // ---------- helpers for suppressing stdout ----------
    private static PrintStream originalOut;
    private static ByteArrayOutputStream silentBuffer;

    private static void suppressOutput() {
        originalOut = System.out;
        silentBuffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(silentBuffer));
    }

    private static void restoreOutput() {
        System.setOut(originalOut);
    }

    // ---------- reflection helper ----------
    private static Object getPrivateField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    // ===================== TEST METHODS =====================

    // 1. Snake stores head and tail correctly
    private static void testSnakeHeadAndTail() {
        Snake snake = new Snake(16, 6);
        boolean ok = (snake.getHead() == 16) && (snake.getTail() == 6);
        report("Snake stores head and tail correctly", ok);
    }

    // 2. Ladder stores bottom and top correctly
    private static void testLadderBottomAndTop() {
        Ladder ladder = new Ladder(4, 14);
        boolean ok = (ladder.getBottom() == 4) && (ladder.getTop() == 14);
        report("Ladder stores bottom and top correctly", ok);
    }

    // 3. Player starts at position 0
    private static void testPlayerStartsAtZero() {
        Player player = new Player("TestPlayer");
        report("Player starts at position 0", player.getCurrentPosition() == 0);
    }

    // 4. Player setCurrentPosition updates correctly
    private static void testPlayerSetCurrentPosition() {
        Player player = new Player("TestPlayer");
        player.setCurrentPosition(42);
        report("Player setCurrentPosition updates correctly", player.getCurrentPosition() == 42);
    }

    // 5. Dice roll always returns between 1 and faces (roll 1000 times)
    private static void testDiceRollRange() {
        Dice dice = new Dice(6);
        boolean allInRange = true;
        for (int i = 0; i < 1000; i++) {
            int value = dice.roll();
            if (value < 1 || value > 6) {
                allInRange = false;
                break;
            }
        }
        report("Dice roll always returns between 1 and faces (1000 rolls)", allInRange);
    }

    // 6. Board resolvePosition with snake returns tail
    private static void testBoardResolveSnake() {
        List<Snake> snakes = Arrays.asList(new Snake(16, 6));
        List<Ladder> ladders = Collections.emptyList();
        Board board = new Board(100, snakes, ladders);
        int resolved = board.resolvePosition(16);
        report("Board resolvePosition with snake returns tail", resolved == 6);
    }

    // 7. Board resolvePosition with ladder returns top
    private static void testBoardResolveLadder() {
        List<Snake> snakes = Collections.emptyList();
        List<Ladder> ladders = Arrays.asList(new Ladder(4, 14));
        Board board = new Board(100, snakes, ladders);
        int resolved = board.resolvePosition(4);
        report("Board resolvePosition with ladder returns top", resolved == 14);
    }

    // 8. Board resolvePosition with no snake/ladder returns same position
    private static void testBoardResolveNoEffect() {
        List<Snake> snakes = Arrays.asList(new Snake(16, 6));
        List<Ladder> ladders = Arrays.asList(new Ladder(4, 14));
        Board board = new Board(100, snakes, ladders);
        int resolved = board.resolvePosition(50);
        report("Board resolvePosition with no snake/ladder returns same position", resolved == 50);
    }

    // 9. Board getTotalSquares returns correct value
    private static void testBoardGetTotalSquares() {
        Board board = new Board(100, Collections.emptyList(), Collections.emptyList());
        report("Board getTotalSquares returns correct value", board.getTotalSquares() == 100);
    }

    // 10. EasyMoveStrategy: player position changes after turn
    private static void testEasyMoveStrategyMovesPlayer() {
        suppressOutput();
        try {
            List<Snake> snakes = Collections.emptyList();
            List<Ladder> ladders = Collections.emptyList();
            Board board = new Board(100, snakes, ladders);
            Dice dice = new Dice(6);
            Player player = new Player("EasyPlayer");
            EasyMoveStrategy strategy = new EasyMoveStrategy();

            // Execute many turns to be confident the player moves at least once
            for (int i = 0; i < 10; i++) {
                strategy.executeTurn(player, board, dice);
            }

            boolean moved = player.getCurrentPosition() > 0;
            restoreOutput();
            report("EasyMoveStrategy: player position changes after turn", moved);
        } catch (Exception e) {
            restoreOutput();
            report("EasyMoveStrategy: player position changes after turn", false);
        }
    }

    // 11. HardMoveStrategy: player position changes after turn
    private static void testHardMoveStrategyMovesPlayer() {
        suppressOutput();
        try {
            List<Snake> snakes = Collections.emptyList();
            List<Ladder> ladders = Collections.emptyList();
            Board board = new Board(100, snakes, ladders);
            Dice dice = new Dice(6);
            Player player = new Player("HardPlayer");
            HardMoveStrategy strategy = new HardMoveStrategy();

            for (int i = 0; i < 10; i++) {
                strategy.executeTurn(player, board, dice);
            }

            boolean moved = player.getCurrentPosition() > 0;
            restoreOutput();
            report("HardMoveStrategy: player position changes after turn", moved);
        } catch (Exception e) {
            restoreOutput();
            report("HardMoveStrategy: player position changes after turn", false);
        }
    }

    // 12. GameFactory creates game with EasyMoveStrategy for "EASY"
    private static void testGameFactoryEasyStrategy() {
        try {
            List<String> names = Arrays.asList("Alice");
            List<Snake> snakes = Collections.emptyList();
            List<Ladder> ladders = Collections.emptyList();
            Game game = GameFactory.createGame(names, snakes, ladders, "EASY");

            Object strategy = getPrivateField(game, "moveStrategy");
            report("GameFactory creates game with EasyMoveStrategy for EASY",
                    strategy instanceof EasyMoveStrategy);
        } catch (Exception e) {
            report("GameFactory creates game with EasyMoveStrategy for EASY", false);
        }
    }

    // 13. GameFactory creates game with HardMoveStrategy for "HARD"
    private static void testGameFactoryHardStrategy() {
        try {
            List<String> names = Arrays.asList("Alice");
            List<Snake> snakes = Collections.emptyList();
            List<Ladder> ladders = Collections.emptyList();
            Game game = GameFactory.createGame(names, snakes, ladders, "HARD");

            Object strategy = getPrivateField(game, "moveStrategy");
            report("GameFactory creates game with HardMoveStrategy for HARD",
                    strategy instanceof HardMoveStrategy);
        } catch (Exception e) {
            report("GameFactory creates game with HardMoveStrategy for HARD", false);
        }
    }

    // 14. Game with single player eventually terminates (play a full game)
    private static void testSinglePlayerGameTerminates() {
        suppressOutput();
        try {
            List<Snake> snakes = Collections.emptyList();
            List<Ladder> ladders = Arrays.asList(new Ladder(2, 99));
            Board board = new Board(100, snakes, ladders);
            Dice dice = new Dice(6);
            Queue<Player> players = new LinkedList<>();
            players.offer(new Player("Solo"));
            EasyMoveStrategy strategy = new EasyMoveStrategy();
            Game game = new Game(board, players, dice, strategy);

            game.play();

            restoreOutput();
            // If play() returned, the game terminated
            report("Game with single player eventually terminates", true);
        } catch (Exception e) {
            restoreOutput();
            report("Game with single player eventually terminates", false);
        }
    }

    // 15. Player name is preserved correctly
    private static void testPlayerNamePreserved() {
        Player player = new Player("Max Verstappen");
        report("Player name is preserved correctly",
                "Max Verstappen".equals(player.getName()));
    }

    // 16. Board with no snakes or ladders returns position unchanged
    private static void testEmptyBoardResolve() {
        Board board = new Board(100, Collections.emptyList(), Collections.emptyList());
        boolean allUnchanged = true;
        for (int pos = 1; pos <= 100; pos++) {
            if (board.resolvePosition(pos) != pos) {
                allUnchanged = false;
                break;
            }
        }
        report("Board with no snakes or ladders returns position unchanged", allUnchanged);
    }

    // 17. Multiple players via GameFactory all get created
    @SuppressWarnings("unchecked")
    private static void testGameFactoryMultiplePlayers() {
        try {
            List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
            List<Snake> snakes = Collections.emptyList();
            List<Ladder> ladders = Collections.emptyList();
            Game game = GameFactory.createGame(names, snakes, ladders, "EASY");

            Queue<Player> playerQueue = (Queue<Player>) getPrivateField(game, "playerQueue");
            boolean correctCount = playerQueue.size() == 3;

            // Verify all names are present
            boolean namesMatch = true;
            int index = 0;
            String[] expected = {"Alice", "Bob", "Charlie"};
            for (Player p : playerQueue) {
                if (!expected[index].equals(p.getName())) {
                    namesMatch = false;
                    break;
                }
                index++;
            }

            report("Multiple players via GameFactory all get created",
                    correctCount && namesMatch);
        } catch (Exception e) {
            report("Multiple players via GameFactory all get created", false);
        }
    }

    // ===================== MAIN =====================

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Snake and Ladders - Test Suite");
        System.out.println("========================================");
        System.out.println();

        testSnakeHeadAndTail();
        testLadderBottomAndTop();
        testPlayerStartsAtZero();
        testPlayerSetCurrentPosition();
        testDiceRollRange();
        testBoardResolveSnake();
        testBoardResolveLadder();
        testBoardResolveNoEffect();
        testBoardGetTotalSquares();
        testEasyMoveStrategyMovesPlayer();
        testHardMoveStrategyMovesPlayer();
        testGameFactoryEasyStrategy();
        testGameFactoryHardStrategy();
        testSinglePlayerGameTerminates();
        testPlayerNamePreserved();
        testEmptyBoardResolve();
        testGameFactoryMultiplePlayers();

        System.out.println();
        System.out.println("========================================");
        System.out.println("   SUMMARY: " + passed + " passed, " + failed + " failed, "
                + (passed + failed) + " total");
        System.out.println("========================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
