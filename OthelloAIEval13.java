public class OthelloAIEval13 implements IOthelloAI {

    /** Thanks to https://kartikkukreja.wordpress.com/2013/03/30/heuristic-function-for-reversiothello/#more-33 */
    private int player;
    private LookUpTable weightedTable;
    private int depth = 0;
    int opponentPlayer;

    @Override
    public Position decideMove(GameState state) {
        System.out.println("DECIDE MOVE - START!\n" + printBoard(state) + "\n\n");

        weightedTable = new LookUpTable(state.getBoard().length);
        depth = 0; // count from 0

        player = state.getPlayerInTurn();
        opponentPlayer = (player == 1) ? 2 : 1;

        Value value = maxValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        System.out.println("DECIDE MOVE - END!\n" + value.toString() + "\n" +
                printBoard(state) + "\n\n");
        return value.getMove();
    }

    private Value maxValue(GameState state, double alpha, double beta) {
        depth++;
        // System.out.println("MAX VALUE - START: Legal moves: " + state.legalMoves() +
        // ", DEPTH: " + depth + "\n");

        if (cutOff(state)) {
            depth--;
            return new Value(eval(state), null);
        }

        Value resultValue = new Value((int) Double.NEGATIVE_INFINITY, null);

        boolean nextPlayerCanMove = !state.legalMoves().isEmpty();
        if (nextPlayerCanMove) {
            for (Position action : state.legalMoves()) {
                GameState newState = new GameState(state.getBoard(), state.getPlayerInTurn());
                if (newState.insertToken(action)) {
                    newState.changePlayer();

                    Value value2 = minValue(newState, alpha, beta);

                    if (value2.getUtility() > resultValue.getUtility()) {
                        resultValue.setUtility(value2.getUtility());
                        resultValue.setMove(action);
                        alpha = Double.max(alpha, resultValue.getUtility());
                    }
                    if (resultValue.getUtility() >= beta) {
                        depth--;
                        return resultValue;
                    }
                }
            }
        } else {
            state.changePlayer();
            Value value2 = minValue(state, alpha, beta);

            if (value2.getUtility() > resultValue.getUtility()) {
                resultValue.setUtility(value2.getUtility());
                resultValue.setMove(null); // no move is set!
            }
        }

        // System.out.println("MAX VALUE - END: " + resultValue + ", DEPTH: " + depth--
        // + ", LEGAL MOVES: "
        // + state.legalMoves() + ", PLAYER: " + state.getPlayerInTurn() + "\n\n");
        // state.removeToken(currentPath.pop());
        depth--;
        return resultValue;
    }

    private Value minValue(GameState state, double alpha, double beta) {
        depth++;
        // System.out.println("MIN VALUE - START: Legal moves: " + state.legalMoves() +
        // ", DEPTH: " + depth + "\n");
        if (cutOff(state)) {
            depth--;
            return new Value(eval(state), null);
        }

        Value resultValue = new Value((int) Double.POSITIVE_INFINITY, null);

        boolean nextPlayerCanMove = !state.legalMoves().isEmpty();
        if (nextPlayerCanMove) {
            for (Position action : state.legalMoves()) {

                GameState newState = new GameState(state.getBoard(), state.getPlayerInTurn());
                if (newState.insertToken(action)) {
                    Value value2 = maxValue(newState, alpha, beta);
                    if (value2.getUtility() < resultValue.getUtility()) {
                        resultValue.setUtility(value2.getUtility());
                        resultValue.setMove(action);
                        beta = Double.min(beta, resultValue.getUtility());
                    }
                    if (resultValue.getUtility() <= alpha) {
                        depth--;
                        return resultValue;
                    }
                }
            }
        } else {
            state.changePlayer();
            Value value2 = maxValue(state, alpha, beta);

            if (value2.getUtility() > resultValue.getUtility()) {
                resultValue.setUtility(value2.getUtility());
                resultValue.setMove(null); // no move is set!
            }
        }
        // System.out.println("MIN VALUE - END: " + resultValue + ", DEPTH: " + depth--
        // + ", LEGAL MOVES: "
        // + state.legalMoves() + ", PLAYER: " + state.getPlayerInTurn() + "\n\n");
        // state.removeToken(currentPath.pop());
        depth--;
        return resultValue;
    }

    // HELP METHODS:
    public int utility(GameState state) { // p: piece difference
        int opponentPlayer = (player == 1) ? 2 : 1;
        return state.countTokens()[player - 1] - state.countTokens()[opponentPlayer - 1];
    }

    // DEBUG METHODS
    private String printBoard(GameState state) {
        String s = "";
        int[][] board = state.getBoard();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                s += " " + board[r][c] + " ";
            }
            s += "\n";
        }
        return s;
    }

    // HEURISTIC METHODS:
    private int[] points(GameState state) {
        int[][] board = state.getBoard();
        int size = state.getBoard().length;

        int tokens1 = 0;
        int tokens2 = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 1)
                    tokens1 += weightedTable.getBoard()[i][j];
                else if (board[i][j] == 2)
                    tokens2 += weightedTable.getBoard()[i][j];
            }
        }
        return new int[] { tokens1, tokens2 };
    }

    private int eval(GameState state) {
        int pieceDifference = utility(state);
        int cornerOccupancy = cornerOccupancy(state);
        float cornerCloseness = cornerCloseness(state);
        int mobility = mobility(state);
        int frontierDisks = frontierDisks(state);
        int lookUpTablePoints = points(state)[player - 1] - points(state)[opponentPlayer - 1];
        
        
	    return (int)((10 * pieceDifference) + (801.724 * cornerOccupancy) + (382.026 * cornerCloseness) + (78.922 * mobility) + (74.396 * frontierDisks) + (10 * lookUpTablePoints));
        //return points(state)[player - 1] - points(state)[opponentPlayer - 1];
    }

    private boolean cutOff(GameState state) {
        // return false;
        return depth == 5 || state.isFinished(); // this can be changed!
    }

    // ----------------------------------------------------------------------------------------

    // HEURISTIC FUNCTIONS:
    /**
     * The ratio of the number of the players moves to the number of moves in total
     * (the legal moves for the player + the opponent)
     */
    private int mobility(GameState state) { // m: mobility
        state.changePlayer();
        int minLegalMoves = state.legalMoves().size();
        state.changePlayer();
        int maxLegalMoves = state.legalMoves().size();
        if (minLegalMoves + maxLegalMoves != 0)
            return (100 * (maxLegalMoves - minLegalMoves)) / (maxLegalMoves + minLegalMoves);
        else
            return 0;
    }

    /**
     * 
     * @param state
     * @return the difference in coins between the max and min player
     */
    private int differenceCoins(GameState state) { // d: difference coins
        int maxPlayerCoin = state.countTokens()[player - 1];
        int minPlayerCoin = state.countTokens()[opponentPlayer - 1];

        return 100 * (maxPlayerCoin - minPlayerCoin) / (maxPlayerCoin + minPlayerCoin);
    }

    private int potentialMobility(GameState state) {
        return 0; // TODO!!!
    }

    private float cornerCloseness(GameState state) { // l: corner closesness
        int playerCC = 0;
        int opponentCC = 0;

        int[][] board = state.getBoard();
        for (Position corner : getCorners(state)) {
            if (board[corner.col][corner.row] == 0) {
                for (Position p : getAroundCornerPositions(state, corner)) {
                    if (board[p.col][p.row] == player)
                        playerCC++;
                    else if (board[p.col][p.row] == opponentPlayer)
                        opponentCC++;
                }
            }
        }
        return -12.5f * (playerCC - opponentCC);
    }

    private int cornerOccupancy(GameState state) { // c: corner occupancy
        int playerCO = 0;
        int opponentCO = 0;

        int[][] board = state.getBoard();
        for (Position corner : getCorners(state)) {
            if (board[corner.col][corner.row] == player) {
                playerCO++;
            } else if (board[corner.col][corner.row] == opponentCO) {
                opponentCO++;
            }
        }
        return 25 * (playerCO - opponentCO);
    }

    /** returns the difference between the frontier disks (the disks that have an empty positions besides) */
    private int frontierDisks(GameState state) { // f
        int[][] board = state.getBoard();

        int playerFD = 0;
        int opponentFD = 0;

        for (int col = 0; col < board.length; col++) {
            for (int row = 0; row < board.length; row++) {
                if (board[col][row] == player && hasOneEmptyPositionAround(state, new Position(col, row))) playerFD++;
                else if (board[col][row] == opponentPlayer && hasOneEmptyPositionAround(state, new Position(col, row))) opponentFD++;
            } 
        }

        if (playerFD + opponentFD != 0) return 100 * (playerFD- opponentFD) / (playerFD + opponentFD);
        else return 0;
        
    }

    // GET SQUARES ON SPECIFIC PLACES:
    /** Returns the positions in the corners */
    private Position[] getCorners(GameState state) {
        int size = state.getBoard().length;
        Position[] corners = new Position[] { new Position(0, 0), new Position(0, size - 1), new Position(size - 1, 0),
                new Position(size - 1, size - 1) };
        return corners;
    }

    private int cornersCaptured(GameState state) {
        int maxCorners = 0;
        int minCorners = 0;

        for (Position corner : getCorners(state)) {
            if (state.getBoard()[corner.col][corner.row] == player)
                maxCorners++;
            else if (state.getBoard()[corner.col][corner.row] == opponentPlayer)
                minCorners++;
        }

        if (maxCorners + minCorners != 0)
            return (100 * (maxCorners - minCorners) / (maxCorners + minCorners));
        else
            return 0;
    }

    private Position[] getAroundCornerPositions(GameState state, Position corner) {

        int size = state.getBoard().length;

        if (corner.col == 0 && corner.row == 0) {
            return new Position[] { new Position(0, 1), new Position(1, 1), new Position(1, 0) };
        }

        if (corner.col == 0 && corner.row == size - 1) {
            return new Position[] { new Position(0, size - 2), new Position(1, size - 2), new Position(1, size - 1) };
        }

        if (corner.col == size - 1 && corner.row == 0) {
            return new Position[] { new Position(size - 2, 0), new Position(size - 2, 1), new Position(size - 1, 1) };
        }

        if (corner.col == size - 1 && corner.row == size - 1) {
            return new Position[] { new Position(size - 2, size - 1), new Position(size - 2, size - 2),
                    new Position(size - 1, size - 2) };
        }

        return null;
    }

    private Position[] getXSquares(GameState state) {
        int size = state.getBoard().length;
        return new Position[] { new Position(1, 1), new Position(1, size - 2), new Position(size - 2, 1),
                new Position(size - 2, size - 2) };
    }

    private Position[] getCSquares(GameState state) {
        int size = state.getBoard().length;
        return new Position[] {
                new Position(0, 1), new Position(1, 0),
                new Position(0, size - 2), new Position(1, size - 1),
                new Position(size - 2, 0), new Position(size - 1, 1),
                new Position(size - 2, size - 1), new Position(size - 1, size - 2)
        };
    }

    /** returns true if there is an empty position around a position  */
    private boolean hasOneEmptyPositionAround(GameState state, Position position) {
        int X1[] = { -1, -1, 0, 1, 1, 1, 0, -1 };
        int Y1[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

        int col = position.col;
        int row = position.row;

        int positions = 0;
        int[][] board = state.getBoard();

        for (int i = 0; i < Y1.length; i++) {
            int x = col+ X1[i];
            int y = row + Y1[i];

            boolean xIsOnTheBoard = 0 <= x && x < board.length;
            boolean yIsOnTheBoard = 0 <= y && y < board.length;

            if (xIsOnTheBoard && yIsOnTheBoard) {
                if (board[x][y] == 0) {
                    return true;
                }
            }
        }
        return false;        
    }

}