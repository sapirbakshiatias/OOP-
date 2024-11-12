public class Move {
    private Position pos;
    private Disc disc;


    public Move(Position p ,Disc d) {
        this.pos = p;
        this.disc = d;
    }
    public Position position() {
        return pos;
    }

    public Disc disc() {
        return disc;
    }
//    public Position[][] undo(){
//
//    }
}