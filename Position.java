import java.util.Objects;
/**
 * The Position class represents a specific coordinate on a two-dimensional grid.
 * Each position is defined by its row and column indices.
 * Instances of this class are immutable.
 *
 * <p>This class provides methods to access the row and column indices,
 * and overrides `equals`, `hashCode`, and `toString` for usage in collections
 * and debugging.</p>
 */
public class Position {
    private int currentRow; //y
    private int currentCol; //x

    /**
     * Constructs a Position instance with the specified row and column indices.
     *
     * @param row the row index of the position.
     * @param col the column index of the position.
     */
    public Position(int row, int col) {
        this.currentCol = col;
        this.currentRow = row;
    }
    /**
     * Returns the column index of this position.
     *
     * @return the column index.
     */
    public int col() {
        return currentCol;
    }
    /**
     * Returns the row index of this position.
     *
     * @return the row index.
     */
    public int row() {
        return currentRow;
    }

    /**
     * Checks if this position is equal to another object.
     * Two positions are considered equal if they have the same row and column indices.
     *
     * @param obj the object to compare with.
     * @return {@code true} if the specified object is equal to this position; {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return currentRow == position.currentRow && currentCol == position.currentCol;
    }
    /**
     * Returns a string representation of this position.
     * The format is "Position[x=<col>, y=<row>]".
     *
     * @return a string representation of this position.
     */
    @Override
    public String toString() {
        return "Position[x=" + currentCol + ", y=" + currentRow + "]";
    }

}