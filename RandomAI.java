import java.util.List;
import java.util.Random;

public class RandomAI extends AIPlayer {
    private final Random random;

    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
        this.random = new Random();
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {

        List<Position> validMoves = gameStatus.ValidMoves(); // קבלת כל המהלכים האפשריים
        if (validMoves.isEmpty()) {
            return null; // אם אין מהלך חוקי, מחזירים null
        }

        int randomIndex = random.nextInt(validMoves.size());
        Position randomMove = validMoves.get(randomIndex);

        return new Move(randomMove, isPlayerOne() ? new SimpleDisc(this) : new SimpleDisc(this));
    }
}
