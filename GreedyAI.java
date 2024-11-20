import java.util.Comparator;
import java.util.List;

public class GreedyAI extends AIPlayer {
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        if (gameStatus instanceof GameLogic gameLogic) {

            List<Position> validMoves = gameStatus.ValidMoves();
            if (validMoves.isEmpty()) {
                gameLogic.isGameFinished();
                gameLogic.reset();
            } else {
                Position bestMove = null;
                int maxFlips = -1;

                Comparator<Position> positionComparator = new ComCol().thenComparing(new CompRow());

                for (Position move : validMoves) {
                    int flips = gameStatus.countFlips(move);
                    if (flips > maxFlips) {
                        maxFlips = flips;
                        bestMove = move;
                    } else if (flips == maxFlips) {
                        if (positionComparator.compare(move, bestMove) > 0) {
                            bestMove = move;
                        }
                    }
                }
                Disc discToPlace = isPlayerOne() ? new SimpleDisc(this) : new SimpleDisc(this);
                gameLogic.locate_disc(bestMove, discToPlace);
                return new Move(bestMove, discToPlace);
            }
        }

        Position endGamePosition = new Position(9, 9);
        Disc dummyDisc = new SimpleDisc(this);
        return new Move(endGamePosition, dummyDisc);
    }
}

