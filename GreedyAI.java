import java.util.Comparator;
import java.util.List;
/**
 * AI player that selects moves maximizing the number of discs flipped.
 */
public class GreedyAI extends AIPlayer {
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }
    /**
     * Selects the best move for the AI player using a greedy algorithm.
     * The move is chosen based on maximizing the number of discs flipped,
     * and in case of a tie, it is resolved by row and column order.
     *
     * @param gameStatus the current state of the game, which provides information
     *                   about valid moves and game rules.
     * @return the best move as determined by the greedy algorithm, or a default move
     *         if no valid moves are available.
     */
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

                Comparator<Position> positionComparator = new CompCol().thenComparing(new CompRow());

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

