public class HumanPlayer extends Player {
    /**
     * Represents a human player in the game.
     */
    public HumanPlayer(boolean b) {
        super(b);
    }

    @Override
    boolean isHuman() {
        return true;
    }
}
