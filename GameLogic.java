import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic {
    private static final int SIZE = 8;
    private Disc[][] board = new Disc[SIZE][SIZE];
    private Player firstPlayer;
    private Player secondPlayer;
    private boolean p1Turn;
    private ArrayList<Position> validMoves;
    int[] rowDirections = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] colDirections = {-1, 0, 1, -1, 1, -1, 0, 1};

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
        //האם אם אני ריק?
        //לא- תמשיך הלאה
        //כן- האם 8- סביבי ריקים?
        //כן- תמשיך הלאה
        //?לא-תעבור אחד אחד:  האם משוייך לשחקן הנכוחי?
        //כן - תמשיך הלאה
        //לא: האם יתהפכו דיסקיות?
        //לא- תמשיך הלאה
        //כן- תכניס לרשימה


        return new ArrayList<>();
    }

    @Override
    public int countFlips(Position a) {
        return 0;
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

    //    public List<Disc> getFlipableDiscs (Disc disc){
//
//    }
    public List<Position> flipInDirection(int row, int col, int rowD, int colD, boolean toFlip) {
        List<Position> canBeFlipped = new ArrayList<Position>();

        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;
        int numF = 0;
        int x = row + rowD;
        int y = col + colD;

        while ((isInBounds(x, y)) && (board[x][y] != null)) {
            Disc currentDisc = board[x][y];

            if (currentDisc.getOwner().equals(currentPlayer)) //same player
                break;
            if (currentDisc instanceof UnflippableDisc){ //unflappable disc
                x += rowD;
                y += colD;
                continue;}

            canBeFlipped.add(new Position(x,y));
            numF += 1;

            x += rowD;
            y += colD;
        }
        //check if ends at current player
        if ((isInBounds(x,y)) && board[x][y] != null && board[x][y].getOwner().equals(currentPlayer)) {
            if (toFlip) {
                for (Position pos : canBeFlipped) {
                    board[pos.row()][pos.col()].setOwner(currentPlayer);
                }
            }
        return canBeFlipped;
        }
        return Collections.emptyList();
    }

    public boolean isInBounds(int row, int col) {
        return row < 0 || row > getBoardSize() || col < 0 || col > getBoardSize();

    }

}

