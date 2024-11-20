import java.util.Comparator;
import java.util.List;

public class GreedyAI extends AIPlayer {
    public GreedyAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> validMoves = gameStatus.ValidMoves();
        if (validMoves.isEmpty()) {
            System.out.println("validMoves. isEmpty");
            return null;
        } return new Move(validMoves.getFirst(),new SimpleDisc(this));
//        }
//        Position bestMove = null;
//        int maxFlips = -1;
//
//        Comparator<Position> positionComparator = new ComCol().thenComparing(new CompRow());
//
//        for (Position move : validMoves) {
//            int flips = gameStatus.countFlips(move);
//            if (flips > maxFlips) {
//                maxFlips = flips;
//                bestMove = move;
//            } else if (flips == maxFlips) {
//                if (positionComparator.compare(move, bestMove) > 0) {
//                    bestMove = move;
//                }
//            }
//        }
//        return new Move(bestMove, isPlayerOne() ? new SimpleDisc(this) : new SimpleDisc(this));
    }

}
