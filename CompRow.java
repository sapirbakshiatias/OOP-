import java.util.Comparator;

/**
 * Comparator for comparing positions based on their row values.
 */
public class CompRow implements Comparator<Position> {
    /**
     * Compares two positions based on their row values.
     *
     * @param p1 the first position.
     * @param p2 the second position.
     * @return a negative integer, zero, or a positive integer as the first position's row
     *         is less than, equal to, or greater than the second position's row.
     */
    @Override
    public int compare(Position p1, Position p2) {
        return Integer.compare(p1.row(), p2.row());
    }
}

