public class OthelloAIEvaluation implements IOthelloAI {

    /** https://kartikkukreja.wordpress.com/2013/03/30/heuristic-function-for-reversiothello/#more-33 */
    private int player;
    private int opponentPlayer;
    private int depth;
    private LookUpTable weightedTable;

    @Override
    public Position decideMove(GameState state) {
        System.out.println("DECIDE MOVE - START!\n" + printBoard(state) + "\n\n");
        
        player = state.getPlayerInTurn();
        opponentPlayer = player == 1 ? 2 : 1;

        weightedTable = new LookUpTable(state.getBoard().length);
        depth = 0; //count from 0

        Value value = maxValue(state, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        System.out.println("DECIDE MOVE - END!\n" + value.toString() + ", DEPTH: " + depth + "\n" +
                printBoard(state) + "\n\n");
        return value.getMove();
    }

    private Value maxValue(GameState state, double alpha, double beta) {
        depth++; //when maxValue is called, we go down the tree
        // System.out.println("MAX VALUE - START: EVAL: " + eval(state) + ", BOARD:\n" + printBoard(state) + 
        // "DEPTH: " + depth + "\n");

        if (cutOff()) {
            depth--; //we go one step up the tree
            Value value = new Value(eval(state), null); //UTILITY FUNCTION --> EVALUATION FUNCTION
            // System.out.println("IS CUTOFF: " + value.toString() + ", DEPTH: " + depth);

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
                    // System.out.println("MIN VALUE CALCULATED: " + value2.toString() + ", DEPTH: " + depth);

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
            // System.out.println("MIN VALUE CALCULATED: " + value2.toString() + ", DEPTH: " + depth);

            if (value2.getUtility() > resultValue.getUtility()) {
                resultValue.setUtility(value2.getUtility());
                resultValue.setMove(null); //no move is set!
            }
        }
        depth--;
        return resultValue;
    }

    private Value minValue(GameState state, double alpha, double beta) {
        depth++; //when minValue is called, we go down the tree
        // System.out.println("MIN VALUE - START: EVAL: " + eval(state) + ", BOARD:\n" + printBoard(state) + 
        // ", DEPTH: " + depth + "\n");

        if (cutOff()) {
            depth--; //we go one step up the tree
            Value value = new Value(eval(state), null); //UTILITY FUNCTION --> EVALUATION FUNCTION
            // System.out.println("IS CUT OFF: " + value.toString() + ", DEPTH: " + depth);
            
            return value;
        }
        Value resultValue = new Value((int) Double.POSITIVE_INFINITY, null);

        boolean nextPlayerCanMove = !state.legalMoves().isEmpty();
        if (nextPlayerCanMove) {
            for (Position action : state.legalMoves()) {

                GameState newState = new GameState(state.getBoard(), state.getPlayerInTurn());
                if (newState.insertToken(action)) {

                    Value value2 = maxValue(newState, alpha, beta);
                    // System.out.println("MAX VALUE CALCULATED: " + value2.toString() + ", DEPTH: " + depth);

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
            // System.out.println("MAX VALUE CALCULATED: " + value2.toString() + ", DEPTH: " + depth);

            if (value2.getUtility() > resultValue.getUtility()) {
                resultValue.setUtility(value2.getUtility());
                resultValue.setMove(null); //no move is set!
            }
        }
        depth--;
        return resultValue;
    }

    // HELP METHODS:
    private boolean cutOff () {
        return depth == 10; //this can be changed!
    }

    private int utility(GameState state) {
        return state.countTokens()[player - 1] - state.countTokens()[opponentPlayer-1];
    }

    // HEURISTIC METHODS:
    /** The ratio of the number of the players moves to the number of moves in total (the legal moves for the player + the opponent)  */
    private int mobility(GameState state) {
        state.changePlayer();
        int minLegalMoves = state.legalMoves().size();
        state.changePlayer();
        int maxLegalMoves = state.legalMoves().size();
        if (minLegalMoves + maxLegalMoves != 0) return (100*(maxLegalMoves-minLegalMoves))/(maxLegalMoves+minLegalMoves);
        else return 0;
    }

    /** Returns the positions in the corners  */
    private Position[] getCorners(GameState state) {
        int size = state.getBoard().length;
        Position[] corners = new Position[] {new Position(0, 0), new Position(0, size-1), new Position(size-1, 0), new Position(size-1, size-1)};
        return corners;
    }

    private int cornersCaptured(GameState state) {
        int maxCorners = 0;
        int minCorners = 0;

        for (Position corner : getCorners(state)) {
            if (state.getBoard()[corner.col][corner.row] == player) maxCorners++;
            else if (state.getBoard()[corner.col][corner.row] == opponentPlayer) minCorners++;
        }

        if (maxCorners + minCorners != 0) return (100 * (maxCorners - minCorners) / (maxCorners + minCorners));
        else return 0;
    }

    private Position[] getAroundCornerPositions(GameState state, Position corner) {
        
        int size = state.getBoard().length;

        if (corner.col == 0 && corner.row == 0) {
            return new Position[] {new Position(0,1), new Position(1, 1), new Position(1, 0)};
        }

        if (corner.col == 0 && corner.row == size-1) {
            return new Position[] {new Position(0,size-2), new Position(1, size-2), new Position(1, size-1)};
        }

        if (corner.col == size-1 && corner.row == 0) {
            return new Position[] {new Position(size-2,0), new Position(size-2,1), new Position(size-1, 1)};
        }

        if (corner.col == size-1 && corner.row == size-1) {
            return new Position[] {new Position(size-2,size-1), new Position(size-2,size-2), new Position(size-1, size-2)};
        }

        return null;
    }

    /** http://www.soongsky.com/othello/en/strategy/notation.php */
    private Position[] getXSquares(GameState state) {
        int size = state.getBoard().length;
        return new Position[] {new Position(1, 1), new Position(1, size-2), new Position(size-2,1), new Position(size-2,size-2)};
    }

    private Position[] getCSquares(GameState state) {
        int size = state.getBoard().length;
        return new Position[] {
            new Position(0,1), new Position(1, 0),
            new Position(0,size-2), new Position(1, size-1),
            new Position(size-2,0), new Position(size-1, 1),
            new Position(size-2,size-1), new Position(size-1, size-2)
        };
       
    }

    private int potentialMobility(GameState state) {
        return 0; //TODO!!!
    }

    /**
     * 
     * @param state 
     * @return the difference in coins between the max and min player
     */
    private int coinPartyHeuristicValue(GameState state) {
        int maxPlayerCoin = state.countTokens()[player-1];
        int minPlayerCoin = state.countTokens()[opponentPlayer-1];
        
        return 100*(maxPlayerCoin-minPlayerCoin)/(maxPlayerCoin+minPlayerCoin);

    }

    /* private int stabilityValue(GameState state) {
        //https://kartikkukreja.wordpress.com/2013/03/30/heuristic-function-for-reversiothello/#more-33
        //untable: can be captured in the next move
        //stable: can not be captured: CORNERS!
        //semi-stabel: can be captured in the future
        //Typical weights could be 1 for stable coins, -1 for unstable coins and 0 for semi-stable coins.
    }
 */

    //Look up table points:
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
        if (state.isFinished()) return utility(state);
        return points(state)[player-1];
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