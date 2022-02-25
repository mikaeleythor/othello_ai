public class OthelloAIEvaluation implements IOthelloAI {

    private int player;
    // private Stack<Position> currentPath = new Stack<>();
    private int depth = 0;

    @Override
    public Position decideMove(GameState state) {
        System.out.println("DECIDE MOVE - START!\n" + printBoard(state) + "\n\n");

        player = state.getPlayerInTurn();
        Value value = maxValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        System.out.println("DECIDE MOVE - END!\n" + value.toString() + "\n" +
                printBoard(state) + "\n\n");
        return value.getMove();
    }

    private Value maxValue(GameState state, double alpha, double beta) {
        // System.out.println("MAX VALUE - START: Legal moves: " + state.legalMoves() +
        // ", DEPTH: " + depth + "\n");

        if (state.isFinished()) {
            Value value = new Value(state.countTokens()[player - 1], null); //UTILITY FUNCTION --> EVALUATION FUNCTION
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

                    Value value2 = minValue(newState, alpha, beta);

                    if (value2.getUtility() > resultValue.getUtility()) {
                        resultValue.setUtility(value2.getUtility());
                        resultValue.setMove(action);
                        alpha = Double.max(alpha, resultValue.getUtility());
                    }
                    if (resultValue.getUtility() >= beta) {
                        return resultValue;
                    }
                }
            }
        } else {
            state.changePlayer();
            Value value2 = minValue(state, alpha, beta);

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

    private Value minValue(GameState state, double alpha, double beta) {
        // System.out.println("MIN VALUE - START: Legal moves: " + state.legalMoves() +
        // ", DEPTH: " + depth + "\n");
        if (state.isFinished()) {
            Value value = new Value(state.countTokens()[player - 1], null); //UTILITY FUNCTION --> EVALUATION FUNCTION
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
                    Value value2 = maxValue(newState, alpha, beta);
                    if (value2.getUtility() < resultValue.getUtility()) {
                        resultValue.setUtility(value2.getUtility());
                        resultValue.setMove(action);
                        beta = Double.min(beta, resultValue.getUtility());
                    }
                    if (resultValue.getUtility() <= alpha) return resultValue;
                }
            }
        } else {
            state.changePlayer();
            Value value2 = maxValue(state, alpha, beta);

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
    private int[] eval(GameState state) {
        int[][] board = state.getBoard();
        int size = state.getBoard().length;
        LookUpTable pointTable = new LookUpTable(size);

        int tokens1 = 0;
    	int tokens2 = 0;
    	for (int i = 0; i < size; i++){
    		for (int j = 0; j < size; j++){
    			if ( board[i][j] == 1 )
    				tokens1 += pointTable.getBoard()[i][j];
    			else if ( board[i][j] == 2 )
                    tokens2 += pointTable.getBoard()[i][j];
    		}
    	}
    	return new int[]{tokens1, tokens2};
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

    
}