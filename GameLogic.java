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
    private Stack<Move> moveHistory = new Stack<>();
    private Stack<Disc[][]> boardHistory = new Stack<>();


    @Override
    public boolean locate_disc(Position a, Disc disc) {
        //TODO wirte string
        if (!positionIsEmpty(a) || !validMoves.contains(a)) {
            return false;
        }
        boardHistory.push(copyBoard());

        board[a.row()][a.col()] = disc;
        flipInDirection(a.row(), a.col(), true);

        moveHistory.push(new Move(a, disc));
        p1Turn = !p1Turn; // Switch turn
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
        validMoves = new ArrayList<>();
        for (int row = 0; row < getBoardSize(); row++) {
            for (int col = 0; col < getBoardSize(); col++) {
                Position position = new Position(row, col);
                if (positionIsEmpty(position)) {
                    int flips = countFlips(position);
                    if (flips > 0) {
                        validMoves.add(position);
                        //     flipCounts.put(position, flips);
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
        for (int i = 0; i < getBoardSize(); i++) {
            for (int j = 0; j < getBoardSize(); j++) {
                board[i][j] = null;
            }
        }
        // setup
        board[(SIZE + 1) / 2][(SIZE + 1) / 2] = new SimpleDisc(getFirstPlayer()); //[4][4]
        board[(SIZE - 1) / 2][(SIZE - 1) / 2] = new SimpleDisc(getFirstPlayer()); //[3][3]
        board[(SIZE + 1) / 2][(SIZE - 1) / 2] = new SimpleDisc(getSecondPlayer()); //[4][3]
        board[(SIZE - 1) / 2][(SIZE + 1) / 2] = new SimpleDisc(getSecondPlayer()); //[3][4]
        firstPlayer.reset_bombs_and_unflippedable();
        secondPlayer.reset_bombs_and_unflippedable();
        p1Turn = true;
    }

    @Override
    public void undoLastMove() {
        if (boardHistory.isEmpty() || moveHistory.isEmpty()) {
            System.out.println("No moves to undo.");
            return;
        }
        board = boardHistory.pop();
        moveHistory.pop();
        p1Turn = !p1Turn;
        validMoves = new ArrayList<>(ValidMoves());
    }

    public boolean positionIsEmpty(Position position) {
        boolean empty = (board[position.row()][position.col()] == null);
        System.out.println("Position " + position + " is " + (empty ? "empty" : "occupied"));
        return empty;
    }

    public int flipInDirection(int row, int col, boolean toFlip) {
        int totalFlips = 0;
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;
        List<Position> allFlippableDiscs = new ArrayList<>(); // רשימה מרכזית


        for (int i = 0; i < rowDirections.length; i++) {
            int rowD = rowDirections[i];
            int colD = colDirections[i];
            int x = row + rowD;
            int y = col + colD;
            List<Position> canBeFlipped = new ArrayList<>();

            while (isInBounds(x, y) && board[x][y] != null) {
                Disc currentDisc = board[x][y];

                if (currentDisc instanceof BombDisc) {
                    allFlippableDiscs.add(new Position(x, y));
                    addBombNeighborsToFlipList(x, y, currentPlayer, allFlippableDiscs);
                    x += rowD;
                    y += colD;
                    continue;
                }

                if (currentDisc.getOwner().equals(currentPlayer)) {
                    allFlippableDiscs.addAll(canBeFlipped);
                    break;
                }

                if (currentDisc instanceof UnflippableDisc) {
                    x += rowD;
                    y += colD;
                    continue;
                }

                canBeFlipped.add(new Position(x, y));
                x += rowD;
                y += colD;
            }
        }
        if (toFlip) {
            for (Position pos : allFlippableDiscs) {
                board[pos.row()][pos.col()].setOwner(currentPlayer);
            }
        }
        totalFlips = allFlippableDiscs.size();
        return totalFlips;
    }

    private void addBombNeighborsToFlipList(int bombRow, int bombCol, Player currentPlayer, List<Position> allFlippableDiscs) {
        for (int j = 0; j < rowDirections.length; j++) {
            int x = bombRow + rowDirections[j];
            int y = bombCol + colDirections[j];

            if (isInBounds(x, y) && board[x][y] != null) {
                Disc neighborDisc = board[x][y];

                    if (neighborDisc instanceof UnflippableDisc) {
                        continue;
                    }

                    if (neighborDisc instanceof BombDisc) {
                        addBombNeighborsToFlipList(x, y, currentPlayer, allFlippableDiscs);
                        continue;
                    }

                    if (!neighborDisc.getOwner().equals(currentPlayer)) {
                        allFlippableDiscs.add(new Position(x, y));
                    }
                }
            }
        }


    public boolean isInBounds(int row, int col) {
        return ((row >= 0) && (row < getBoardSize()) && (col >= 0) && (col < getBoardSize()));
    }

    private Disc[][] copyBoard() {
        Disc[][] copy = new Disc[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == null) {
                    copy[i][j] = null;
                } else {
                    Disc disc = board[i][j];
                    if (disc instanceof UnflippableDisc) {
                        copy[i][j] = new UnflippableDisc(disc.getOwner());
                    } else if (disc instanceof BombDisc) {
                        copy[i][j] = new BombDisc(disc.getOwner());
                    } else {
                        copy[i][j] = new SimpleDisc(disc.getOwner());
                    }
                }
            }
        }
        return copy;
    }

}

