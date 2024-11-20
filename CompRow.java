import java.util.Comparator;

public class CompRow implements Comparator<Position> {

    @Override
    public int compare(Position p1, Position p2) {
        return Integer.compare(p1.row(), p2.row());
    }
}

