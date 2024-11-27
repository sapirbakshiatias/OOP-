import java.util.ArrayList;
import java.util.List;

public class Move {
    private Position pos;
    private Disc disc;
    private List<Position> flippedPositions;

    public Move(Position p, Disc d, List<Position> flipped) {
        this.pos = p;
        this.disc = d;
        this.flippedPositions = new ArrayList<>(flipped); // שמירת עותק של הרשימה
    }


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

    public Position getPosition() {
        return this.pos;
    }

    public Disc getDisc() {
        return this.disc;
    }
}