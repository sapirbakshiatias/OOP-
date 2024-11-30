import java.util.ArrayList;
import java.util.List;

/**
 * The Move class represents a single move in a game, encapsulating the position of the move,
 * the disc being placed, and the list of positions flipped as a result of the move.
 *
 * <p>This class provides constructors for creating moves with or without flipped positions,
 * and getter methods for accessing the move's details.</p>
 */
public class Move {
    private Position pos;
    private Disc disc;
    private List<Position> flippedPositions;

    /**
     * Constructs a Move with the specified position, disc, and flipped positions.
     *
     * <p>The list of flipped positions is copied to prevent external modification.</p>
     *
     * @param p the position where the move is made.
     * @param d the disc being placed.
     * @param flipped the positions that are flipped as a result of the move.
     */
    public Move(Position p, Disc d, List<Position> flipped) {
        this.pos = p;
        this.disc = d;
        this.flippedPositions = new ArrayList<>(flipped); // שמירת עותק של הרשימה
    }

    /**
     * Constructs a Move with the specified position and disc.
     * This constructor does not include flipped positions.
     *
     * @param p the position where the move is made.
     * @param d the disc being placed.
     */
    public Move(Position p ,Disc d) {
        this.pos = p;
        this.disc = d;
    }
    public Position position() {
        return null;
    }

    public Disc disc() {
        return null;
    }

    /**
     * Returns the position where the move is made.
     *
     * @return the position of the move.
     */
    public Position getPosition() {
        return this.pos;
    }
    /**
     * Returns the disc being placed during this move.
     *
     * @return the disc of the move.
     */
    public Disc getDisc() {
        return this.disc;
    }
}