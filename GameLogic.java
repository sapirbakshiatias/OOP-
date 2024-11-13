import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic {
    private static final int SIZE = 8;
    private Disc[][] board = new Disc[SIZE][SIZE];
    private Player firstPlayer;
    private Player secondPlayer;
    private boolean firstPlayerTurn;
    private Stack<Move> history;

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        if ((board[a.row()][a.col()] != null))// this pos is occupied
            return false;
        if (countFlips(a) == 0) //no discs are flippable
            return false;
        //board[a.row()][a.col()] = disc;

        //    // הופכים את כל הדיסקים בעמדות שהוחזרו על ידי getFlippablePositions
        //    for (Position pos : flips) {
        //        board[pos.getX()][pos.getY()].flip();
        //    }
        //
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
        if (position == null)
            return null;
        if (board[position.row()][position.col()] == null) return null;
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
        return true;
    }

    @Override
    public boolean isGameFinished() {
        return false;
    }

    @Override
    public void reset() {
        // clear
        for (int i = 0; i < getBoardSize(); i++) { //שורה
            //עמודה
            for (int j = 0; j < getBoardSize(); j++)
                board[i][j] = null;
        // setup
        board[3][3] = new SimpleDisc(getFirstPlayer());
        board[4][4] = new SimpleDisc(getFirstPlayer());
        board[3][4] = new SimpleDisc(getSecondPlayer());
        board[4][3] = new SimpleDisc(getSecondPlayer());
    }

    @Override
    public void undoLastMove() {
    }

    public List<Disc> getNeighbors(int row, int col) {
        List<Disc> neighbors = new ArrayList<>();
        Disc currentDisc = board[row][col];

        //if (currentDisc == null) return neighbors; //

        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, 1}, {1, 1}, {1, 0},
                {1, -1}, {0, -1}
        };

        for (int[] direction : directions) {
            int x = row + direction[0];
            int y = col + direction[1];

            if (x < 0 || x > getBoardSize() || y < 0 || y > getBoardSize()) continue;
            Disc neighborDisc = board[x][y];

            if (neighborDisc == null || neighborDisc.getOwner().equals(currentDisc.getOwner())) continue;
            {
                neighbors.add(neighborDisc);
            }
        }
        return neighbors;
    }

//    public List<Disc> getFlipableDiscs (Disc disc){
//
//    }
}

