import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomAI extends AIPlayer {

    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        if (gameStatus instanceof GameLogic gameLogic) {
            List<Position> validMoves = gameLogic.ValidMoves();

            if (validMoves.isEmpty()) {
                gameLogic.isGameFinished();
                gameLogic.reset();
            }
            else {
                int randomIndex = (int) (Math.random() * validMoves.size());
                Position randomMove = validMoves.get(randomIndex);
                Disc discToPlace = chooseRandomDisc();
                gameLogic.locate_disc(randomMove, discToPlace);
                return new Move(randomMove, discToPlace);
            }
        }
        Position endGamePosition = new Position(9, 9);
        Disc dummyDisc = new SimpleDisc(this);
        return new Move(endGamePosition, dummyDisc);
    }

    private Disc chooseRandomDisc() {
        Random rand = new Random();

        List<Disc> discs = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            discs.add(new UnflippableDisc(this));
        }
        for (int i = 0; i < 2; i++) {
            discs.add(new BombDisc(this));
        }
        int remainingDiscs = 32 - discs.size();
        for (int i = 0; i < remainingDiscs; i++) {
            discs.add(new SimpleDisc(this));
        }
        int randomIndex = rand.nextInt(discs.size());
        return discs.get(randomIndex);
    }
}
