public class EasyMoveStrategy implements MakeMoveStrategy {

    @Override
    public boolean executeTurn(Player player, Board board, Dice dice) {
        boolean continueTurn = true;

        while (continueTurn) {
            int diceValue = dice.roll();
            System.out.println(player.getName() + " rolled a " + diceValue);

            if (diceValue == 6) {
                System.out.println("Rolled a 6! Extra turn granted.");
            } else {
                continueTurn = false;
            }

            int newPos = player.getCurrentPosition() + diceValue;
            if (newPos > board.getTotalSquares()) {
                continue;
            }

            newPos = board.resolvePosition(newPos);
            player.setCurrentPosition(newPos);
            System.out.println(player.getName() + " moved to " + newPos);

            if (player.getCurrentPosition() == board.getTotalSquares()) {
                return true;
            }
        }
        return false;
    }
}
