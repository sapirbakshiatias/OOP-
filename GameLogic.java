import java.util.*;

public class GameLogic implements PlayableLogic {
    private static final int SIZE = 8;
    private Disc[][] board = new Disc[SIZE][SIZE];
    private Player firstPlayer;
    private Player secondPlayer;
    private boolean p1Turn;
    private ArrayList<Position> validMoves;
    int[] rowDirections = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] colDirections = {-1, 0, 1, -1, 1, -1, 0, 1};
    private Map<Position, Integer> flipCounts = new HashMap<>();


    @Override
    public boolean locate_disc(Position a, Disc disc) {
        if (positionIsEmpty(a) || !validMoves.contains(a))
            return false;
        board[a.row()][a.col()] = disc;


        //TODO 1.flip (use direction) 2.save in history and 3.wirte string 4.bomb

        //    // הופכים את כל הדיסקים בעמדות שהוחזרו על ידי getFlippablePositions
        //    for (Position pos : flips) {
        //        board[pos.getX()][pos.getY()].flip();
        //    }
        //jhddslksk
        //    // שמירת המהלך במחסנית לצורך אפשרות של Undo
        //    moveHistory.push(new Move(a, disc));
        //
        //    // מעבר תור
        //    firstPlayerTurn = !firstPlayerTurn;
        //
        return true;
    }

    @Override
    public Disc getDiscAtPosition(Position position) {
        if (position == null || positionIsEmpty(position))
            return null;
        return board[position.row()][position.col()];
    }

    @Override
    public int getBoardSize() {
        return board.length;
    }

    @Override
    public List<Position> ValidMoves() {
        List<Position> validPositions = new ArrayList<>();
        flipCounts.clear();

        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                Position position = new Position(row, col);

                if (positionIsEmpty(position)) {
                    int flips = countFlips(position);
                    if (flips > 0) {
                        validPositions.add(position);
                        flipCounts.put(position, flips);
                    }
                }
            }
        }
        return validPositions;
    }

    @Override
    public int countFlips(Position a) {
        return flipInDirection(a.row(), a.col(), false);
    }

    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    @Override
    public Player getSecondPlayer() {
        return secondPlayer;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {
        firstPlayer = player1;
        secondPlayer = player2;
    }

    @Override
    public boolean isFirstPlayerTurn() {
        return p1Turn;
    }

    @Override
    public boolean isGameFinished() {
        return ValidMoves().isEmpty();
    }

    @Override
    public void reset() {
        //TODO bombs + unflappable + turn
        // clear
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++)
                board[i][j] = null;
            // setup
            board[(SIZE + 1) / 2][(SIZE + 1) / 2] = new SimpleDisc(getFirstPlayer()); //[4][4]
            board[(SIZE - 1) / 2][(SIZE - 1) / 2] = new SimpleDisc(getFirstPlayer()); //[3][3]
            board[(SIZE + 1) / 2][(SIZE - 1) / 2] = new SimpleDisc(getSecondPlayer()); //[4][3]
            board[(SIZE - 1) / 2][(SIZE + 1) / 2] = new SimpleDisc(getSecondPlayer()); //[3][4]
        }
    }

    @Override
    public void undoLastMove() {
    }

    public List<Position> getNeighbors(int row, int col) {
        List<Position> neighbors = new ArrayList<>();
        Disc currentDisc = board[row][col];

        if (currentDisc == null) return neighbors;
        for (int i = 0; i < 8; i++) {
            int x = row + rowDirections[i];
            int y = col + colDirections[i];

            if (isInBounds(x, y)) continue;
            Disc neighborDisc = board[x][y];
            if (neighborDisc == null || neighborDisc.getOwner().equals(currentDisc.getOwner())) continue;
            {
                neighbors.add(new Position(x, y));
            }
        }
        return neighbors;
    }

    public boolean positionIsEmpty(Position position) {
        return (board[position.row()][position.col()] == null);
    }

    public int flipInDirection(int row, int col, boolean toFlip) {
        int totalFlips = 0;
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;

        for (int i = 0; i < rowDirections.length; i++) {
            int rowD = rowDirections[i];
            int colD = colDirections[i];
            int x = row + rowD;
            int y = col + colD;
            List<Position> canBeFlipped = new ArrayList<>();

            while (isInBounds(x, y) && board[x][y] != null) {
                Disc currentDisc = board[x][y];

                //belonging to the current player
                if (currentDisc.getOwner().equals(currentPlayer)) {
                    if (toFlip) {
                        for (Position pos : canBeFlipped) {
                            board[pos.row()][pos.col()].setOwner(currentPlayer);
                        }
                    }
                    totalFlips += canBeFlipped.size();
                    break;
                }

                // UnflippableDisc
                if (currentDisc instanceof UnflippableDisc) {
                    break;
                }

                canBeFlipped.add(new Position(x, y));
                x += rowD;
                y += colD;
            }
        }
        return totalFlips;
    }


    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < getBoardSize() && col >= 0 && col < getBoardSize();
    }


}

