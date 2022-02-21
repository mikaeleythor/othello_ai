import java.util.Stack;

public class OthelloAIAlphaBeta implements IOthelloAI {

    private int player;
    private Stack<Position> currentPath = new Stack<>();

    @Override
    public Position decideMove(GameState state) {
        System.out.println("DECIDE MOVE - START!\n" + printBoard(state) + "\n\n");

        player = state.getPlayerInTurn();
        Value value = maxValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        System.out.println("DECIDE MOVE - END!\n" + value.toString() + "\n" + printBoard(state) + "\n\n");
        return value.getMove();
    }

    private Value maxValue(GameState state, double alpha, double beta) {
        System.out.println("MAX VALUE - START: Legal moves: " + state.legalMoves() + ", DEPTH: " + depth + "\n");

        if (state.isFinished()) {
            Value value = new Value(state.countTokens()[player - 1], null);
            System.out.println("IS FINISH: " + value.toString() + ", DEPTH: " + depth--);
            // state.removeToken(currentPath.pop()); //not sure about this, but it seems like, the board is filled up, and the tokens aren't removed

            return value;
        }

        GameState originState = state;
        Value resultValue = new Value((int) Double.NEGATIVE_INFINITY, null);

        var legalMoves = state.legalMoves();
        boolean nextPlayerCanMove = !state.legalMoves().isEmpty();
        if (nextPlayerCanMove) {
            for (Position action : legalMoves) {
                state = originState;
                Value value2 = minValue(result(state, action), alpha, beta);
                if (value2.getUtility() > resultValue.getUtility()) {
                    resultValue.setUtility(value2.getUtility());
                    resultValue.setMove(action);
                    alpha = Math.max(alpha, resultValue.getUtility());
                }
                if (resultValue.getUtility() >= beta) return resultValue;
            }
        } else {
            state.changePlayer();
            maxValue(state, alpha, beta);
        }

        System.out.println("MAX VALUE - END: " + resultValue + ", DEPTH: " + depth-- + ", LEGAL MOVES: "
                + state.legalMoves() + ", PLAYER: " + state.getPlayerInTurn() + "\n\n");
        // state.removeToken(currentPath.pop());
        return resultValue;
    }

    private Value minValue(GameState state, double alpha, double beta) {
        System.out.println("MIN VALUE - START: Legal moves: " + state.legalMoves() + ", DEPTH: " + depth + "\n");
        if (state.isFinished()) {
            Value value = new Value(state.countTokens()[player], null);
            System.out.println("IS FINISH: " + value.toString() + ", DEPTH: " + depth--);
            // state.removeToken(currentPath.pop());

            return value;
        }
        GameState originState = state;
        Value resultValue = new Value((int) Double.POSITIVE_INFINITY, null);

        var legalMoves = state.legalMoves();
        boolean nextPlayerCanMove = !legalMoves.isEmpty();
        if (nextPlayerCanMove) {
            for (Position action : legalMoves) {
                state = originState;
                Value value2 = maxValue(result(state, action), alpha, beta);
                if (value2.getUtility() < resultValue.getUtility()) {
                    resultValue.setUtility(value2.getUtility());
                    resultValue.setMove(action);
                    beta = Math.max(beta, resultValue.getUtility());
                }
                if (resultValue.getUtility()<= alpha) return resultValue;
            }
        } else {
            state.changePlayer();
            minValue(state, alpha, beta);
        }
        System.out.println("MIN VALUE - END: " + resultValue + ", DEPTH: " + depth-- + ", LEGAL MOVES: "
                + state.legalMoves() + ", PLAYER: " + state.getPlayerInTurn() + "\n\n");
        // state.removeToken(currentPath.pop());
        return resultValue;
    }

    private GameState result(GameState state, Position action) {
        System.out.println("BEFORE: " + action.toString() + ", DEPTH: " + depth++ + ", PLAYER: "
                + state.getPlayerInTurn() + " \n" + printBoard(state) + "\n");
        if (state.insertToken(action)) {
            currentPath.add(action);
            System.out.println("CURRENT PATH: " + currentPath); // THIS HAS TO BE DELEATED AGAIN, AND THE LEGAL MOVES
                                                                // ARE NOT IDENTICAL
        }

        System.out.println("AFTER: \n" + printBoard(state) + "\n");
        return state;
    }

    // HELP METHODS:
    private boolean isEqual(Position p1, Position p2) {
        return p1.col == p2.col && p1.row == p2.row;
    }

    public int[][] getNewBoardAfterMove(int[][] board, Position position, int player){
        //get clone of old board
        int[][] newboard = new int[board.length][board.length];
        for (int col = 0; col < board.length; col++) {
            for (int row = 0; row < board.length; row++) {
                newboard[col][row] = board[col][row];
            }
        }

        //place piece
        newboard[position.col][position.row] = player;

        return newboard;
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
