import java.util.Comparator;

public class ComCol implements Comparator<Position> {
    @Override
    public int compare(Position p1, Position p2) {
        return Integer.compare(p1.col(), p2.col());
    }
}
