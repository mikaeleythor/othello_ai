public class OthelloAIEvaluation implements IOthelloAI {

    private int player;
    private int depth;
    private LookUpTable weightedTable;

    @Override
    public Position decideMove(GameState state) {
        System.out.println("DECIDE MOVE - START!\n" + printBoard(state) + "\n\n");
        
        player = state.getPlayerInTurn();
        weightedTable = new LookUpTable(state.getBoard().length);
        depth = 0; //count from 0

        Value value = maxValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        System.out.println("DECIDE MOVE - END!\n" + value.toString() + ", DEPTH: " + depth + "\n" +
                printBoard(state) + "\n\n");
        return value.getMove();
    }

    private Value maxValue(GameState state, double alpha, double beta) {
        depth++; //when maxValue is called, we go down the tree
        System.out.println("MAX VALUE - START: EVAL: " + eval(state) + ", BOARD:\n" + printBoard(state) + 
        "DEPTH: " + depth + "\n");

        if (state.isFinished()) {
            depth--; //we go one step up the tree
            Value value = new Value(utility(state), null); //UTILITY FUNCTION --> EVALUATION FUNCTION
            System.out.println("IS FINISH: " + value.toString() + ", DEPTH: " + depth);

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
                    System.out.println("MIN VALUE CALCULATED: " + value2.toString() + ", DEPTH: " + depth);

                    if (value2.getUtility() > resultValue.getUtility()) {
                        resultValue.setUtility(value2.getUtility());
                        resultValue.setMove(action);
                        alpha = Double.max(alpha, resultValue.getUtility());
                    }
                    if (resultValue.getUtility() >= beta) {
                        depth--; //go up the tree
                        return resultValue;
                    }
                }
            }
        } else {
            state.changePlayer();
            Value value2 = minValue(state, alpha, beta);
            System.out.println("MIN VALUE CALCULATED: " + value2.toString() + ", DEPTH: " + depth);

            if (value2.getUtility() > resultValue.getUtility()) {
                resultValue.setUtility(value2.getUtility());
                resultValue.setMove(null); //no move is set!
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
        depth++; //when minValue is called, we go down the tree
        System.out.println("MIN VALUE - START: EVAL: " + eval(state) + ", BOARD:\n" + printBoard(state) + 
        ", DEPTH: " + depth + "\n");

        if (state.isFinished()) {
            depth--; //we go one step up the tree
            Value value = new Value(utility(state), null); //UTILITY FUNCTION --> EVALUATION FUNCTION
            System.out.println("IS FINISH: " + value.toString() + ", DEPTH: " + depth);
            
            return value;
        }
        Value resultValue = new Value((int) Double.POSITIVE_INFINITY, null);

        boolean nextPlayerCanMove = !state.legalMoves().isEmpty();
        if (nextPlayerCanMove) {
            for (Position action : state.legalMoves()) {

                GameState newState = new GameState(state.getBoard(), state.getPlayerInTurn());
                if (newState.insertToken(action)) {

                    Value value2 = maxValue(newState, alpha, beta);
                    System.out.println("MAX VALUE CALCULATED: " + value2.toString() + ", DEPTH: " + depth);

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
            System.out.println("MAX VALUE CALCULATED: " + value2.toString() + ", DEPTH: " + depth);

            if (value2.getUtility() > resultValue.getUtility()) {
                resultValue.setUtility(value2.getUtility());
                resultValue.setMove(null); //no move is set!
            }
        }
        depth--;
        return resultValue;
    }

    // HELP METHODS:
    public int utility(GameState state) {
        return state.countTokens()[player - 1];
    }

    private int[] points(GameState state) {
        int[][] board = state.getBoard();
        int size = state.getBoard().length;
        

        int tokens1 = 0;
    	int tokens2 = 0;
    	for (int i = 0; i < size; i++){
    		for (int j = 0; j < size; j++){
    			if ( board[i][j] == 1 )
    				tokens1 += weightedTable.getBoard()[i][j];
    			else if ( board[i][j] == 2 )
                    tokens2 += weightedTable.getBoard()[i][j];
    		}
    	}
    	return new int[]{tokens1, tokens2};
    }

    private int eval(GameState state) {
        return points(state)[player-1];
    }

    private boolean cutOff () {
        return depth == 7; //this can be changed!
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