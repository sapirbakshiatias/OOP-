import java.util.*;

public class GameLogic implements PlayableLogic {

    private static final int SIZE = 8;
    private Disc[][] board = new Disc[SIZE][SIZE];
    private Player firstPlayer;
    private Player secondPlayer;
    private boolean p1Turn;
    private ArrayList<Position> validMoves;
    private Stack<Move> moveHistory = new Stack<>();
    private Stack<List<Position>> flipedHistory = new Stack<>();
    private Stack<Disc[][]> boardHistory = new Stack<>();
    private List<Position> reallyFliped = new ArrayList<>();
    int[] rowDirections = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] colDirections = {-1, 0, 1, -1, 1, -1, 0, 1};


    @Override
    public boolean locate_disc(Position a, Disc disc) {
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;

        if (!positionIsEmpty(a) || !validMoves.contains(a) ||
                (currentPlayer.getNumber_of_bombs() <= 0) && ("💣".equals(disc.getType())) ||
                (currentPlayer.getNumber_of_unflippedable() <= 0) && ("⭕".equals(disc.getType()))) {
            return false;
        }
        boardHistory.push(copyBoard());
        disc.setOwner(currentPlayer);
        board[a.row()][a.col()] = disc;
        System.out.println("Player " + (isFirstPlayerTurn() ? "1 " : "2 ") + "placed a " +
                disc.getType() + " in " + "(" + a.row() + "," + a.col() + ")\n");

        flipInDirection(a.row(), a.col(), true);

        moveHistory.push(new Move(a, disc));

        ReduceDiscType(disc, currentPlayer);

        p1Turn = !p1Turn;
        System.out.println();
        return true;
    }

    @Override
    public Disc getDiscAtPosition(Position position) {
        Disc disc = board[position.row()][position.col()];
        if (disc == null) {
            return null;
        }
        if (Objects.equals(disc.getType(), "⭕")) {
            return new UnflippableDisc(disc.getOwner());
        }
        if (Objects.equals(disc.getType(), "💣")) {
            return new BombDisc(disc.getOwner());
        }
        return new SimpleDisc(disc.getOwner());
    }

    @Override
    public int getBoardSize() {
        return board.length;
    }

    @Override
    public List<Position> ValidMoves() {
        validMoves = new ArrayList<>();
        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                Position position = new Position(row, col);
                if (positionIsEmpty(position)) {
                    int flips = countFlips(position);
                    if (flips > 0) {
                        validMoves.add(position);
                    }
                }
            }
        }
        return validMoves;
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
        if (!validMoves.isEmpty()) {
            return false;
        } else {
            int player1Discs = 0;
            int player2Discs = 0;

            for (int i = 0; i < getBoardSize(); i++) {
                for (int j = 0; j < getBoardSize(); j++) {
                    if (board[i][j] != null) {
                        if (board[i][j].getOwner() == firstPlayer) {
                            player1Discs++;
                        } else {
                            player2Discs++;
                        }
                    }
                }
            }
            if (player1Discs >= player2Discs) {
                firstPlayer.addWin();
            } else {
                secondPlayer.addWin();
            }

            System.out.printf("Player %s wins with %d discs! Player %s had %d discs.\n\n",
                    player1Discs >= player2Discs ? "1" : "2",
                    Math.max(player1Discs, player2Discs),
                    player1Discs < player2Discs ? "1" : "2",
                    Math.min(player1Discs, player2Discs));

            return true;
        }
    }

    @Override
    public void reset() {
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                board[i][j] = null;
            }
        }
        int center = SIZE / 2;
        board[center][center] = new SimpleDisc(getFirstPlayer()); // [4][4]
        board[center - 1][center - 1] = new SimpleDisc(getFirstPlayer()); // [3][3]
        board[center][center - 1] = new SimpleDisc(getSecondPlayer()); // [4][3]
        board[center - 1][center] = new SimpleDisc(getSecondPlayer()); // [3][4]

        firstPlayer.reset_bombs_and_unflippedable();
        secondPlayer.reset_bombs_and_unflippedable();
        moveHistory.clear();
        boardHistory.clear();
        p1Turn = true;
    }

    @Override
    public void undoLastMove() {
        if (boardHistory.isEmpty() || moveHistory.isEmpty()) {
            System.out.println("\tNo previous move available to undo.\n");
            return;
        }
        System.out.println("Undoing last move:");
        Move lastMove = moveHistory.peek();

        System.out.println("\tUndo: removing " + lastMove.getDisc().getType() + " from " + "(" + lastMove.getPosition().row() + "," + lastMove.getPosition().col() + ")\n");

        List<Position> lastFlipped = flipedHistory.pop();
        for (Position pos : lastFlipped) {
            System.out.println("\tUndo: flipping back " + board[pos.row()][pos.col()].getType() +
                    " in (" + pos.row() + ", " + pos.col() + ")");
            board[pos.row()][pos.col()].setOwner(isFirstPlayerTurn() ? secondPlayer : firstPlayer);
        }
        p1Turn = !p1Turn;

        addDiscType(lastMove);

        moveHistory.pop();
        board = boardHistory.pop();
        validMoves = new ArrayList<>(ValidMoves());
        System.out.println();
    }
    private void addDiscType(Move lastMove) {
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;

        if ("💣".equals(lastMove.getDisc().getType())) {
            currentPlayer.number_of_bombs++;
        }

        if ("⭕".equals(lastMove.getDisc().getType())) {
            currentPlayer.number_of_unflippedable++;
        }
    }

    public boolean positionIsEmpty(Position position) {
        if (position == null)
            return true;
        return (board[position.row()][position.col()] == null);
    }

    public int flipInDirection(int row, int col, boolean toFlip) {
        int totalFlips = 0;
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;
        Set<Position> checkedPositions = new HashSet<>();

        for (int i = 0; i < rowDirections.length; i++) {
            int rowD = rowDirections[i];
            int colD = colDirections[i];
            int x = row + rowD;
            int y = col + colD;
            List<Position> canBeFlipped = new ArrayList<>();

            while (isInBounds(x, y) && board[x][y] != null) {
                Disc currentDisc = board[x][y];

                if (currentDisc.getOwner().equals(currentPlayer)) {
                    if (!canBeFlipped.isEmpty()) {
                        reallyFliped.addAll(canBeFlipped);
                        for (Position pos : canBeFlipped) {

                            if ("💣".equals(board[pos.row()][pos.col()].getType())) {
                                List<Position> bombPositions = new ArrayList<>();
                                bombPositions.add(pos);
                                flipBomb(bombPositions);
                            }
                        }
                        if (toFlip) {
                            flipOverNeighbor(reallyFliped);
                        }
                    }
                    break;
                }
                if (currentDisc instanceof UnflippableDisc) {
                    if (!currentDisc.getOwner().equals(currentPlayer)) {
                        x += rowD;
                        y += colD;
                        continue;
                    } else {
                        break;
                    }
                }
                canBeFlipped.add(new Position(x, y));
                x += rowD;
                y += colD;
            }
            checkedPositions.addAll(reallyFliped);
            reallyFliped.clear();
        }
        if (toFlip) {
            flipedHistory.push(new ArrayList<>(checkedPositions));
        }
        totalFlips = checkedPositions.size();
        return totalFlips;
    }

    private void flipBomb(List<Position> bombPositions) {
        int flipCount = 0;
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;
        List<Position> tempFlippableDiscs = new ArrayList<>();

        for (Position bombPos : bombPositions) {
            int row = bombPos.row();
            int col = bombPos.col();

            for (int i = 0; i < rowDirections.length; i++) {
                int neighborRow = row + rowDirections[i];
                int neighborCol = col + colDirections[i];

                if (isInBounds(neighborRow, neighborCol)) {
                    Position neighborPos = new Position(neighborRow, neighborCol);

                    if (!reallyFliped.contains(neighborPos)
                            && board[neighborRow][neighborCol] != null
                            && !board[neighborRow][neighborCol].getOwner().equals(currentPlayer)
                            && !"⭕".equals(board[neighborRow][neighborCol].getType())) {

                        if ("💣".equals(board[neighborRow][neighborCol].getType())) {
                            tempFlippableDiscs.add(neighborPos);
                        }
                        reallyFliped.add(neighborPos);
                    }
                }
            }
            flipBomb(tempFlippableDiscs);
        }
    }

    private void flipOverNeighbor(List<Position> reallyFliped) {
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;
        for (Position p : reallyFliped) {
            board[p.row()][p.col()].setOwner(currentPlayer);
            System.out.println("Player " + (isFirstPlayerTurn() ? "1 " : "2 ") + "flipped the " + board[p.row()][p.col()].getType() + " (" + p.row() + "," + p.col() + ")");
        }
    }

    public boolean isInBounds(int row, int col) {
        return ((row >= 0) && (row < getBoardSize()) && (col >= 0) && (col < getBoardSize()));
    }

    private Disc[][] copyBoard() {
        Disc[][] copy = new Disc[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Disc disc = board[i][j];
                if (disc == null) {
                    copy[i][j] = null;
                } else if ("⭕".equals(disc.getType())) {
                    copy[i][j] = new UnflippableDisc(disc.getOwner());
                } else if ("💣".equals(disc.getType())) {
                    copy[i][j] = new BombDisc(disc.getOwner());
                } else {
                    copy[i][j] = new SimpleDisc(disc.getOwner());
                }
            }
        }
        return copy;
    }
    private void ReduceDiscType(Disc disc, Player currentPlayer) {
        if ("💣".equals(disc.getType())) {
            currentPlayer.reduce_bomb();
        }
        if ("⭕".equals(disc.getType())) {
            currentPlayer.reduce_unflippedable();
        }
    }


}