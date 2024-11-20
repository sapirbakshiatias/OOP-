import java.util.*;

import static java.lang.Math.*;

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
    List<Position> canBeFlipped;
    List<Position> allFlippableDiscs;
    List<Position> bombNeighborsToFlip;


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
                    //if (canBeFlipped.size >0) {
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
        int win_disc = max(player_1_discs, player_2_discs);
        int loser_disc = min(player_1_discs, player_2_discs);
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
        return empty;
    }

    public int flipInDirection(int row, int col, boolean toFlip) {
        int totalFlips = 0;
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;
        List<Position> tempFlippableDiscs = new ArrayList<>();
        bombNeighborsToFlip = new ArrayList<>();


        for (int i = 0; i < rowDirections.length; i++) {
            //  tempFlippableDiscs.clear();
            int rowD = rowDirections[i];
            int colD = colDirections[i];
            int x = row + rowD;
            int y = col + colD;
            List<Position> canBeFlipped = new ArrayList<>();

            while (isInBounds(x, y) && board[x][y] != null) {
                Disc currentDisc = board[x][y];

                if (currentDisc.getOwner().equals(currentPlayer)) {
                    if (!canBeFlipped.isEmpty()) {
                        if (currentDisc instanceof BombDisc) {
                            tempFlippableDiscs.add(new Position(x, y));
                            flipBomb(x, y, tempFlippableDiscs);
                        }
                        if (toFlip) {
                            List<Position> allFlippableDiscs = new ArrayList<>(canBeFlipped);  // התחלה עם דיסקים שנמצאים ב-canBeFlipped
                            allFlippableDiscs.addAll(bombNeighborsToFlip);

                            for (Position pos : allFlippableDiscs) {
                                board[pos.row()][pos.col()].setOwner(currentPlayer);
                            }
                        }
//                            for (Position pos : canBeFlipped) {
//                                board[pos.row()][pos.col()].setOwner(currentPlayer);
//                            }
//                            for (Position pos : bombNeighborsToFlip){
//                                board[pos.row()][pos.col()].setOwner(currentPlayer);
//                            }
//                        }
                        totalFlips += canBeFlipped.size();
                    }
                    break;
                }
                if (currentDisc instanceof UnflippableDisc) {
                    if (!currentDisc.getOwner().equals(currentPlayer)) {
                        x += rowD;
                        y += colD;
                        continue;
                    } else break;
                }
                canBeFlipped.add(new Position(x, y));
                x += rowD;
                y += colD;
            }
        }
        return totalFlips;
    }


    private int flipBomb(int x, int y, List<Position> flipped_disc) {
        int flip = 0;
        Player player = isFirstPlayerTurn() ? firstPlayer : secondPlayer;
        for (int i = 0; i < rowDirections.length; i++) {
            int rowD = rowDirections[i];
            int colD = colDirections[i];
            int b = x + rowD;
            int a = y + colD;
            Disc currentDisc = board[b][a];
            if (isInBounds(b, a) && currentDisc != null
                    && currentDisc.getOwner() != player
                    && !"⭕".equals(currentDisc.getType())
                    && !flipped_disc.contains(currentDisc)) {
                if ("💣".equals(currentDisc.getType())) { //bomb
                    bombNeighborsToFlip.add(new Position(b, a));
                    flipped_disc.add(new Position(b, a));
                    flip += flipBomb(x, y, flipped_disc) + 1;
                } else { //simple
                    bombNeighborsToFlip.add(new Position(b, a));
                    flip++;
                }
            }
        }
        return flip;
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

