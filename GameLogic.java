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

    List<Position> reallyFliped = new ArrayList<>();
    List<Position> bombNeighborsToFlip;
    private Set<Position> allFlippedDiscs = new HashSet<>();


    @Override
    public boolean locate_disc(Position a, Disc disc) {
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;

        if (!positionIsEmpty(a) || !validMoves.contains(a)) {
            return false;
        }

        if (currentPlayer.getNumber_of_bombs() <= 0) {
            if ("💣".equals(disc.getType())) {
                return false;
            }
        }
        if (currentPlayer.getNumber_of_unflippedable() <= 0) {
            if ("⭕".equals(disc.getType())) {
                return false;
            }
        }
        boardHistory.push(copyBoard());

        board[a.row()][a.col()] = disc;

        System.out.println("Player " + (isFirstPlayerTurn() ? "1 " : "2 ") + "placed a " + disc.getType() + " in " + "(" + a.row() + "," + a.col() + ")");

        flipInDirection(a.row(), a.col(), true);

        moveHistory.push(new Move(a, disc));
        if ("💣".equals(disc.getType())) {
            currentPlayer.reduce_bomb();
        }
        if ("⭕".equals(disc.getType())) {
            currentPlayer.reduce_unflippedable();
        }
        p1Turn = !p1Turn; // Switch turn
        System.out.println();
        return true;
    }

    @Override
    public Disc getDiscAtPosition(Position position) {
        if (board[position.row()][position.col()] == null) {
            return null;
        }
        if (Objects.equals(board[position.row()][position.col()].getType(), "⭕")) {
            return new UnflippableDisc(board[position.row()][position.col()].getOwner());
        }
        if (Objects.equals(board[position.row()][position.col()].getType(), "💣")) {
            return new BombDisc(board[position.row()][position.col()].getOwner());
        }
        return new SimpleDisc(board[position.row()][position.col()].getOwner());
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
        System.out.printf("Player %s wins with %d discs! Player %s had %d discs.\n\n",
                player_1_discs >= player_2_discs ? "1" : "2",
                Math.max(player_1_discs, player_2_discs),
                player_1_discs < player_2_discs ? "1" : "2",
                Math.min(player_1_discs, player_2_discs));

        if (player_1_discs >= player_2_discs) {
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
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;
//        if (boardHistory.isEmpty() || moveHistory.isEmpty()) {
           // System.out.println("\tNo previous move available to undo.\n");
            //return;}
        if(boardHistory.isEmpty()){
            System.out.println("bordhistory");
            return;
        }if(moveHistory.isEmpty()){
            System.out.println("movehistory");
            return;
        }
        System.out.println("Undoing last move:");
        System.out.println("\tUndo: removing " + moveHistory.peek().getDisc().getType() + " from " + "(" + moveHistory.peek().getPosition().row() + "," + moveHistory.peek().getPosition().col() + ")\n");

        for (Position pos : reallyFliped) {
            if (pos.equals("💣")){
                System.out.println("bomb");
                currentPlayer.number_of_bombs++;
            }
        }
        if (moveHistory.peek().getDisc().getType() == "⭕"){
            System.out.println("unflip");
            currentPlayer.number_of_unflippedable++;
        }
        moveHistory.pop();
        board = boardHistory.pop();
        p1Turn = !p1Turn;
        validMoves = new ArrayList<>(ValidMoves());
        System.out.println();
    }

    public boolean positionIsEmpty(Position position) {
        if (position == null)
            return true;
        return (board[position.row()][position.col()] == null);
    }

    public int flipInDirection(int row, int col, boolean toFlip) {
        int totalFlips = 0;
        Player currentPlayer = isFirstPlayerTurn() ? firstPlayer : secondPlayer;
        bombNeighborsToFlip = new ArrayList<>();

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
                        if (toFlip) {
                            for (Position pos : canBeFlipped) {
                                board[pos.row()][pos.col()].setOwner(currentPlayer);
                                System.out.println("Player " + (isFirstPlayerTurn() ? "1 " : "2 ") + "flipped the disc in " + "(" + pos.row() + "," + pos.col() + ")");

                                if ("💣".equals(board[pos.row()][pos.col()].getType())) {
                                    List<Position> bombPositions = new ArrayList<>();
                                    bombPositions.add(pos);
                                    totalFlips += flipBomb(bombPositions);
                                }
                                reallyFliped.add(pos);
                            }
                        }
                        totalFlips += canBeFlipped.size();
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
        }
        return totalFlips;
    }


    private int flipBomb(List<Position> bombPositions) {
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

                    if (!allFlippedDiscs.contains(neighborPos)
                            && board[neighborRow][neighborCol] != null
                            && !board[neighborRow][neighborCol].getOwner().equals(currentPlayer)
                            && !"⭕".equals(board[neighborRow][neighborCol].getType())) {

                        if ("💣".equals(board[neighborRow][neighborCol].getType())) {
                            tempFlippableDiscs.add(neighborPos);
                        }
                        if (allFlippedDiscs.add(neighborPos)) {
                            board[neighborRow][neighborCol].setOwner(currentPlayer);
                            flipCount++;
                        }
                        reallyFliped.add(neighborPos);
                    }
                }
            }
            flipBomb(tempFlippableDiscs);
        }
        return flipCount;
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



