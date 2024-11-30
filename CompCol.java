import java.util.Comparator;
/**
 * Comparator for comparing positions based on their column values.
 */
public class CompCol implements Comparator<Position> {
    /**
     * Compares two positions based on their column values.
     *
     * @param p1 the first position.
     * @param p2 the second position.
     * @return a negative integer, zero, or a positive integer as the first position's column
     *         is less than, equal to, or greater than the second position's column.
     */
    @Override
    public int compare(Position p1, Position p2) {
        return Integer.compare(p1.col(), p2.col());
    }
}
