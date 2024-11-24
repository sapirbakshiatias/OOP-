
public class Position {
    private int currentRow; //y
    private int currentCol; //x

    public Position(int row, int col) {
        this.currentCol = col;
        this.currentRow = row;
    }
    public int col() {
        return currentCol;
    }
    public int row() {
        return currentRow;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return currentRow == position.currentRow && currentCol == position.currentCol;
    }
//    public int flipInDirection(int row, int col, boolean toFlip) {
//        int totalFlips = 0;
//        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;
//        bombNeighborsToFlip = new ArrayList<>();
//
//        for (int i = 0; i < rowDirections.length; i++) {
//            int rowD = rowDirections[i];
//            int colD = colDirections[i];
//            int x = row + rowD;
//            int y = col + colD;
//            List<Position> canBeFlipped = new ArrayList<>();
//
//            while (isInBounds(x, y) && board[x][y] != null) {
//                Disc currentDisc = board[x][y];
//
//                if (currentDisc.getOwner().equals(currentPlayer)) {
//                    if (!canBeFlipped.isEmpty()) {
//                        System.out.println("if to flip- this is the canbe" + canBeFlipped);
//                        if (toFlip) {
//                            for (Position pos : canBeFlipped) {
//                                board[pos.row()][pos.col()].setOwner(currentPlayer);
//                                System.out.println("Player " + (isFirstPlayerTurn() ? "1 " : "2 ") + "flipped the " + currentDisc.getType() + " in " + "(" + pos.row() + "," + pos.col() + ")");
//                                //FIXME לזהות פצצה שמתהפכת
//                            }
//                        }
//                        totalFlips += canBeFlipped.size();
//                    }
//                    break;
//                }
//                if (currentDisc instanceof UnflippableDisc) {
//                    if (!currentDisc.getOwner().equals(currentPlayer)) {
//                        x += rowD;
//                        y += colD;
//                        continue;
//                    } else break;
//                }
//                canBeFlipped.add(new Position(x, y));
//                x += rowD;
//                y += colD;
//            }
//        }
//        return totalFlips;
//    }
    @Override
    public String toString() {
        return "Position[x=" + currentCol + ", y=" + currentRow + "]";
    }
}