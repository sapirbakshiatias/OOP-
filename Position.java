public class Position{
    private int currentRow; //y
    private int currentCol; //x
//    private Disc currentDisc;

//    private boolean isEmpty;
    public Position(int row, int col) {
        this.currentCol = col;
        this.currentRow = row;
//        currentDisc = null;
//        isEmpty = true;
    }
    public int col(){
        return currentCol;
    }

    public int row(){
        return currentRow;
    }
//    public boolean getIsEmpty(){
//        return isEmpty;}

//    public void setIsEmpty(){
//        this.isEmpty = true;
//    }

//    public void setDisc(Disc disc){
//        currentDisc = disc;
//        this.isEmpty = false;
//    }

//    public Disc getDisc(){
//        return currentDisc;
//    }

}