import java.util.*;
/**
 * The GameLogic class implements the main game logic for a Reversi-like game.
 * It handles the game board, player actions, valid move calculation, and game rules.
 */
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


    /**
     * Places a disc on the board if the position is valid.
     *
     * @param a    the position where the disc is to be placed
     * @param disc the disc to be placed
     * @return true if the move is valid and executed; false otherwise
     */
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
    /**
     * Retrieves the disc located at the specified position.
     *
     * @param position the position to check
     * @return the disc at the position, or null if the position is empty
     */
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
    /**
     * Returns the size of the board.
     *
     * @return the board size
     */
    @Override
    public int getBoardSize() {
        return board.length;
    }
    /**
     * Calculates and returns the list of valid moves for the current player.
     *
     * @return a list of valid positions for the current player's move
     */
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
    /**
     * Counts the number of discs that would flip if a move is made at the given position.
     *
     * @param a the position to check
     * @return the number of discs that would flip
     */
    @Override
    public int countFlips(Position a) {
        return flipInDirection(a.row(), a.col(), false);
    }


    /**
     * Returns the first player.
     *
     * @return the first player
     */
    @Override
    public Player getFirstPlayer() {
        return firstPlayer;
    }
    /**
     * Returns the second player.
     *
     * @return the second player
     */
    @Override
    public Player getSecondPlayer() {
        return secondPlayer;
    }
    /**
     * Sets the players for the game.
     *
     * @param player1 the first player
     * @param player2 the second player
     */
    @Override
    public void setPlayers(Player player1, Player player2) {
        firstPlayer = player1;
        secondPlayer = player2;
    }

    /**
     * Checks if it's the first player's turn.
     *
     * @return true if it's the first player's turn; false otherwise
     */
    @Override
    public boolean isFirstPlayerTurn() {
        return p1Turn;
    }
    /**
     * Checks if the game has finished.
     *
     * @return true if the game is finished; false otherwise
     */
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
    /**
     * Resets the game to its initial state.
     */
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
    /**
     * Undoes the last move, restoring the board and game state.
     */
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


    /**
     * Checks if a given position is empty on the board.
     *
     * @param position the position to check
     * @return true if the position is empty; false otherwise
     */
    public boolean positionIsEmpty(Position position) {
        if (position == null)
            return true;
        return (board[position.row()][position.col()] == null);
    }
    /**
     * Flips discs in valid directions based on the current move.
     *
     * @param row     the row index of the move
     * @param col     the column index of the move
     * @param toFlip  whether to perform the flip or just count
     * @return the total number of discs flipped
     */
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
    /**
     * Handles the flipping of discs around a bomb disc.
     *
     * @param bombPositions the positions affected by the bomb
     */
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

    /**
     * Flips all discs in the provided list of positions.
     *
     * @param reallyFliped the list of positions to flip
     */
    private void flipOverNeighbor(List<Position> reallyFliped) {
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;
        for (Position p : reallyFliped) {
            board[p.row()][p.col()].setOwner(currentPlayer);
            System.out.println("Player " + (isFirstPlayerTurn() ? "1 " : "2 ") + "flipped the " + board[p.row()][p.col()].getType() + " (" + p.row() + "," + p.col() + ")");
        }
    }
    /**
     * Checks if a position is within the bounds of the board.
     *
     * @param row the row index
     * @param col the column index
     * @return true if the position is in bounds; false otherwise
     */
    public boolean isInBounds(int row, int col) {
        return ((row >= 0) && (row < getBoardSize()) && (col >= 0) && (col < getBoardSize()));
    }
    /**
     * Creates a copy of the current board state.
     *
     * @return a copy of the board
     */
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

    /**
     * Updates the current player's disc inventory based on the type of the disc
     * from the last move. If the disc is a bomb (💣), increments the player's bomb count.
     * If the disc is unflippable (⭕), increments the player's unflippable disc count.
     *
     * @param lastMove the most recent move performed in the game, which includes the
     *                 position and type of the disc placed.
     */
    private void addDiscType(Move lastMove) {
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;

        if ("💣".equals(lastMove.getDisc().getType())) {
            currentPlayer.number_of_bombs++;
        }

        if ("⭕".equals(lastMove.getDisc().getType())) {
            currentPlayer.number_of_unflippedable++;
        }
    }


    /**
     * Reduces the count of special discs (bomb or unflippable) for the current player.
     *
     * @param disc           the type of disc used
     * @param currentPlayer  the current player
     */
    private void ReduceDiscType(Disc disc, Player currentPlayer) {
        if ("💣".equals(disc.getType())) {
            currentPlayer.reduce_bomb();
        }
        if ("⭕".equals(disc.getType())) {
            currentPlayer.reduce_unflippedable();
        }
    }

}