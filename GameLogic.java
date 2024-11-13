import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic {
    private static final int SIZE = 8;
    private Disc[][] board = new Disc[SIZE][SIZE];
    private Player firstPlayer;
    private Player secondPlayer;
    private ArrayList<Position> validMoves;

    private boolean p1Turn;

    @Override
    public boolean locate_disc(Position a, Disc disc) {
        if (positionIsEmpty(a) || !validMoves.contains(a))
            return false;
        board[a.row()][a.col()] = disc;
        //TODO 1.flip (use direction) 2.save in history and 3.wirte string

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

    public boolean positionIsEmpty(Position position) {
        return (board[position.row()][position.col()] == null);
    }

//    public List<Disc> getFlipableDiscs (Disc disc){
//
//    }
}

