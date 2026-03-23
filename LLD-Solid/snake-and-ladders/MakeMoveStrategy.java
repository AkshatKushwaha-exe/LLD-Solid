public interface MakeMoveStrategy {
    boolean executeTurn(Player player, Board board, Dice dice);
}
