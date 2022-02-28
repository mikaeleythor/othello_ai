public class OthelloAIEval implements IOthelloAI {

    private int player;
    private LookUpTable weightedTable;
    private int depth = 0;

    @Override
    public Position decideMove(GameState state) {
        System.out.println("DECIDE MOVE - START!\n" + printBoard(state) + "\n\n");

        weightedTable = new LookUpTable(state.getBoard().length);
        depth = 0; //count from 0
        
        player = state.getPlayerInTurn();
        Value value = maxValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        System.out.println("DECIDE MOVE - END!\n" + value.toString() + "\n" +
                printBoard(state) + "\n\n");
        return value.getMove();
    }

    private Value maxValue(GameState state, double alpha, double beta) {
        depth++; 
        // System.out.println("MAX VALUE - START: Legal moves: " + state.legalMoves() +
        // ", DEPTH: " + depth + "\n");

        if (state.isFinished()) {
            Value value = new Value(utility(state), null);
            depth--;
            // System.out.println("IS FINISH: " + value.toString() + ", DEPTH: " + depth--);
            // state.removeToken(currentPath.pop()); //not sure about this, but it seems
            // like, the board is filled up, and the tokens aren't removed

            return value;
        } else if (cutOff()) {
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
        depth++; 
        // System.out.println("MIN VALUE - START: Legal moves: " + state.legalMoves() +
        // ", DEPTH: " + depth + "\n");
        if (state.isFinished()) {
            Value value = new Value(utility(state), null);
            // System.out.println("IS FINISH: " + value.toString() + ", DEPTH: " + depth--);
            // state.removeToken(currentPath.pop());
            depth--;
            return value;
        } else if (cutOff()) {
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
                resultValue.setMove(null); //no move is set!
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

    public int utility(GameState state) {
        int opponentPlayer = (player == 1) ? 2 : 1;
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
        //if (state.isFinished()) return utility(state);
        int opponentPlayer = (player == 1) ? 2 : 1;
        return points(state)[player-1]; 
    } 

    private boolean cutOff () {
        return false;
        //return depth == 10; //this can be changed!
    }
    


}