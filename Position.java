public class Position{
    private int currentRow; //y
    private int currentCol; //x
//    private Disc currentDisc;

//    private boolean isEmpty;
    public Position(int row, int col) {
        this.currentCol = col;
        this.currentRow = row;

    }
    public int col(){
        return currentCol;
    }

    public int row(){
        return currentRow;
    }
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return currentRow == position.currentRow && currentCol == position.currentCol;
    }
    @Override
    public String toString() {
        return "Position[x=" + currentCol + ", y=" + currentRow + "]";
    }


}