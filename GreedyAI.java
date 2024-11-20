import java.util.List;

public class GreedyAI extends AIPlayer {
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> validMoves = gameStatus.ValidMoves();
        if (validMoves.isEmpty()) {
            return null;
        }

        Position bestMove = null;
        int maxFlips = 0;

        for (Position move : validMoves) {
            int flips = gameStatus.countFlips(move); // חישוב מספר ההפיכות
            if (bestMove == null || flips > maxFlips ||
                    (flips == maxFlips && move.compareTo(bestMove) > 0)) { // השוואה לפי compareTo
                maxFlips = flips;
                bestMove = move;
            }
        }

        return new Move(bestMove, isPlayerOne() ? new SimpleDisc(this) : new SimpleDisc(this));
    }
}
