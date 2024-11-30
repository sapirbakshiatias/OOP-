import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI player that makes random moves during the game.
 */
public class RandomAI extends AIPlayer {
    /**
     * Constructs a RandomAI player.
     *
     * @param isPlayerOne whether the player is Player 1.
     */
    public RandomAI(boolean isPlayerOne) {
        super(isPlayerOne);
    }

    /**
     * Executes the AI player's move. Chooses a random valid position and places a random disc.
     * If no valid moves are available, the game state is checked for completion and reset if necessary.
     *
     * @param gameStatus the current state of the game.
     * @return the chosen Move, or a default end-game move if no valid moves exist.
     */
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
    /**
     * Chooses a random disc type for the AI to place.
     *
     * @return a randomly selected Disc.
     */
    private Disc chooseRandomDisc() {
        Random rand = new Random();

        List<Disc> discs = new ArrayList<>();

        for (int i = 0; i < number_of_unflippedable; i++) {
            discs.add(new UnflippableDisc(this));
        }
        for (int i = 0; i < number_of_bombs; i++) {
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
