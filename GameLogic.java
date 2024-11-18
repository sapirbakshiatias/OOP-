import java.util.*;

public class GameLogic implements PlayableLogic {
    private static final int SIZE = 8;
    private Disc[][] board = new Disc[SIZE][SIZE];
    private Player firstPlayer;
    private Player secondPlayer;
    private boolean p1Turn;
    private ArrayList<Position> validMoves = new ArrayList<>();
    int[] rowDirections = {-1, -1, -1, 0, 0, 1, 1, 1};
    int[] colDirections = {-1, 0, 1, -1, 1, -1, 0, 1};
    private Map<Position, Integer> flipCounts = new HashMap<>();
    private Stack<Move> moveHistory = new Stack<>();


    @Override
    public boolean locate_disc(Position a, Disc disc) {
        //TODO wirte string
        System.out.println("Trying to locate disc at: " + a + " for player: " + disc.getOwner());
        if (positionIsEmpty(a) || !validMoves.contains(a)) {
            System.out.println("Invalid move: position is not empty or not valid.");
            return false;
        }

        board[a.row()][a.col()] = disc;
        System.out.println("Disc placed. Flipping in direction...");
        flipInDirection(a.row(), a.col(), true);

        moveHistory.push(new Move(a, disc));
        p1Turn = !p1Turn; // Switch turn
        System.out.println("Move completed. Turn switched to: " + (p1Turn ? "Player 1" : "Player 2"));
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
        flipCounts.clear();
        validMoves.clear();
        System.out.println("Calculating valid moves...");
        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                Position position = new Position(row, col);
                if (positionIsEmpty(position)) {
                    int flips = countFlips(position);
                    if (flips > 0) {
                        validMoves.add(position);
                        flipCounts.put(position, flips);
                        System.out.println("Valid move found: " + position + " with " + flips + " flips.");
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
        validMoves = (ArrayList<Position>) ValidMoves();
        if (!validMoves.isEmpty()) {
            validMoves = null;
            return false;
        }
        int player_1_discs = 0;
        int player_2_discs = 0;
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                if (board[i][j] != null) {
                    if (board[i][j].getOwner() == firstPlayer) {
                        player_1_discs++;
                    } else {
                        player_2_discs++;
                    }
                }
            }
        }
        int win_disc = Math.max(player_1_discs, player_2_discs);
        int loser_disc = Math.min(player_1_discs, player_2_discs);
        String winner = player_1_discs >= player_2_discs ? "1" : "2";
        String loser = player_1_discs < player_2_discs ? "1" : "2";
        System.out.printf("Player %s wins with %d discs! Player %s had %d discs.\n\n", winner, win_disc, loser, loser_disc);
        if (winner.equals("1")) {
            firstPlayer.addWin();
        } else {
            secondPlayer.addWin();
        }
        return true;
    }

    @Override
    public void reset() {
        //TODO bombs + unflappable + turn
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++)
                board[i][j] = null;
            // setup
            board[(SIZE + 1) / 2][(SIZE + 1) / 2] = new SimpleDisc(getFirstPlayer()); //[4][4]
            board[(SIZE - 1) / 2][(SIZE - 1) / 2] = new SimpleDisc(getFirstPlayer()); //[3][3]
            board[(SIZE + 1) / 2][(SIZE - 1) / 2] = new SimpleDisc(getSecondPlayer()); //[4][3]
            board[(SIZE - 1) / 2][(SIZE + 1) / 2] = new SimpleDisc(getSecondPlayer()); //[3][4]
            firstPlayer.reset_bombs_and_unflippedable();
            secondPlayer.reset_bombs_and_unflippedable();
            p1Turn = true;
        }
    }

    @Override
    public void undoLastMove() {
    }

//    public List<Position> getNeighbors(int row, int col) {
//        List<Position> neighbors = new ArrayList<>();
//        Disc currentDisc = board[row][col];
//
//        if (currentDisc == null) return neighbors;
//        for (int i = 0; i < 8; i++) {
//            int x = row + rowDirections[i];
//            int y = col + colDirections[i];
//
//            if (isInBounds(x, y)) continue;
//            Disc neighborDisc = board[x][y];
//            if (neighborDisc == null || neighborDisc.getOwner().equals(currentDisc.getOwner())) continue;
//            {
//                neighbors.add(new Position(x, y));
//            }
//        }
//        return neighbors;
//    }

    //    public boolean positionIsEmpty(Position position) {
//        return (board[position.row()][position.col()] == null);
//    }
    public boolean positionIsEmpty(Position position) {
        boolean empty = (board[position.row()][position.col()] == null);
        System.out.println("Position " + position + " is " + (empty ? "empty" : "occupied"));
        return empty;
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

//                // If we find a BombDisc, we handle it separately
//                if (currentDisc instanceof BombDisc) {
//                    // Check the 8 neighboring positions around the BombDisc
//                    for (int j = 0; j < rowDirections.length; j++) {
//                        int neighborX = x + rowDirections[j];
//                        int neighborY = y + colDirections[j];
//
//                        if (isInBounds(neighborX, neighborY) && board[neighborX][neighborY] != null) {
//                            Disc neighborDisc = board[neighborX][neighborY];
//
//                            // If the neighbor has the same owner and is not an UnflippableDisc, flip it
//                            if (neighborDisc.getOwner().equals(currentPlayer) && !(neighborDisc instanceof UnflippableDisc)) {
//                                if (toFlip) {
//                                    board[neighborX][neighborY].setOwner(currentPlayer);
//                                }
//                                totalFlips++;
//                            }
//                        }
//                    }
//                }

                // If it's the current player's disc, we flip the captured discs in this direction
                if (currentDisc.getOwner().equals(currentPlayer)) {
                    if (toFlip) {
                        for (Position pos : canBeFlipped) {
                            board[pos.row()][pos.col()].setOwner(currentPlayer);
                        }
                    }
                    totalFlips += canBeFlipped.size();
                    break;
                }

                // If we hit an UnflippableDisc, stop processing this direction
                if (currentDisc instanceof UnflippableDisc) {
                    break;
                }

                // If the disc is not the current player's, add it to the list of possible flips
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

//    public void undoLastMove() {
//        if (moveHistory.isEmpty()) {
//            System.out.println("No moves to undo.");
//            return;
//        }

//        // Retrieve the last move from the history
//        Move lastMove = moveHistory.pop();
//        Position lastPosition = lastMove.getPosition();
//        Disc lastDisc = lastMove.getDisc();
//
//        // Revert the board state by removing the last placed disc
//        board[lastPosition.row()][lastPosition.col()] = null;
//
//        // If the last move was a BombDisc, we also need to undo flips made by the BombDisc
//        if (lastDisc instanceof BombDisc) {
//            // Revert the flips made by the bomb's neighbors
//            for (int i = 0; i < rowDirections.length; i++) {
//                int x = lastPosition.row() + rowDirections[i];
//                int y = lastPosition.col() + colDirections[i];
//                if (isInBounds(x, y) && board[x][y] != null && board[x][y].getOwner() != lastDisc.getOwner()) {
//                    // Revert the flip of the neighbor
//                    board[x][y].setOwner(null);  // Assuming null means no owner, i.e., the disk is flipped back to empty
//                }
//            }
//        }
//
//        // Revert turn order (if needed)
//        p1Turn = !p1Turn;
//
//        // Optionally, you could add other logic to undo any other game state changes (like score, special moves, etc.)


}

