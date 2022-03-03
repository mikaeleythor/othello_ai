public class OthelloAI13 implements IOthelloAI {

    private int player;
    // private Stack<Position> currentPath = new Stack<>();

    @Override
    public Position decideMove(GameState state) {
        System.out.println("DECIDE MOVE - START!\n" + printBoard(state) + "\n\n");

        player = state.getPlayerInTurn();
        Value value = maxValue(state);

        System.out.println("DECIDE MOVE - END!\n" + value.toString() + "\n" +
                printBoard(state) + "\n\n");
        return value.getMove();
    }

    private Value maxValue(GameState state) {
        // System.out.println("MAX VALUE - START: Legal moves: " + state.legalMoves() +
        // ", DEPTH: " + depth + "\n");

        if (state.isFinished()) {
            Value value = new Value(utility(state), null);
            // System.out.println("IS FINISH: " + value.toString() + ", DEPTH: " + depth--);
            // state.removeToken(currentPath.pop()); //not sure about this, but it seems
            // like, the board is filled up, and the tokens aren't removed

            return value;
        }
        
        Value resultValue = new Value((int) Double.NEGATIVE_INFINITY, null);

        boolean nextPlayerCanMove = !state.legalMoves().isEmpty();
        if (nextPlayerCanMove) {
            for (Position action : state.legalMoves()) {
                GameState newState = new GameState(state.getBoard(), state.getPlayerInTurn());
                if (newState.insertToken(action)) {
                    newState.changePlayer();

                    Value value2 = minValue(newState);

                    if (value2.getUtility() > resultValue.getUtility()) {
                        resultValue.setUtility(value2.getUtility());
                        resultValue.setMove(action);
                    }
                }
            }
        } else {
            state.changePlayer();
            Value value2 = minValue(state);

            if (value2.getUtility() > resultValue.getUtility()) {
                resultValue.setUtility(value2.getUtility());
                resultValue.setMove(null); //no move is set!
            }
        }

        // System.out.println("MAX VALUE - END: " + resultValue + ", DEPTH: " + depth--
        // + ", LEGAL MOVES: "
        // + state.legalMoves() + ", PLAYER: " + state.getPlayerInTurn() + "\n\n");
        // state.removeToken(currentPath.pop());
        return resultValue;
    }

    private Value minValue(GameState state) {
        // System.out.println("MIN VALUE - START: Legal moves: " + state.legalMoves() +
        // ", DEPTH: " + depth + "\n");
        if (state.isFinished()) {
            Value value = new Value(utility(state), null);
            // System.out.println("IS FINISH: " + value.toString() + ", DEPTH: " + depth--);
            // state.removeToken(currentPath.pop());

            return value;
        }
        Value resultValue = new Value((int) Double.POSITIVE_INFINITY, null);

        boolean nextPlayerCanMove = !state.legalMoves().isEmpty();
        if (nextPlayerCanMove) {
            for (Position action : state.legalMoves()) {

                GameState newState = new GameState(state.getBoard(), state.getPlayerInTurn());
                if (newState.insertToken(action)) {
                    Value value2 = maxValue(newState);
                    if (value2.getUtility() < resultValue.getUtility()) {
                        resultValue.setUtility(value2.getUtility());
                        resultValue.setMove(action);
                    }
                }
            }
        } else {
            state.changePlayer();
            Value value2 = maxValue(state);

            if (value2.getUtility() > resultValue.getUtility()) {
                resultValue.setUtility(value2.getUtility());
                resultValue.setMove(null); //no move is set!
            }
        }
        // System.out.println("MIN VALUE - END: " + resultValue + ", DEPTH: " + depth--
        // + ", LEGAL MOVES: "
        // + state.legalMoves() + ", PLAYER: " + state.getPlayerInTurn() + "\n\n");
        // state.removeToken(currentPath.pop());
        return resultValue;
    }

    // HELP METHODS:
    private boolean isEqual(Position p1, Position p2) {
        return p1.col == p2.col && p1.row == p2.row;
    }

    public int utility(GameState state) {
        return state.countTokens()[player - 1];
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

    private int depth = 0;
}