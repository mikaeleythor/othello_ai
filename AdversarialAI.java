import java.util.*;


public class AdversarialAI implements IOthelloAI{
    
    // Player id is either 1 or 2
    private int playerID;
    private int playerIndex;

    // Relative index is ID - 1
    private int otherPlayerID;
    private int otherPlayerIndex;

    // Max recursion depth
    private int maxDepth = 40;

    // Main goal is winning not acquiring tokens, so winning-utility is hardcoded
    private float maxUtil = 100f;

    // Heuristics are not perfect, utility of possible win is higher
    private float confidence = 0.7f;

    // Heuristic names and weight specified
    private String[] heuristicNames = {"stableArea"};
    private float[] heuristicWeights = {1.0f};

    // HashMap for heuristic names and weights for ease of use
    private HashMap<String, Integer> weightMap = new HashMap<String, Integer>();

    // This method scales the weights of the heuristic utilities such that
    // their values do not superceed values of solid utility functions
    private int[] scaleHeuristics(float[] heuristicWeights){

        // Initialize weights
        int[] scaledWeights = new int[heuristicWeights.length];
        for (int i = 0; i < heuristicWeights.length; i++){

            // Weights multiplied by hardcoded utility and confidence in heuristics
            scaledWeights[i] = (int) (heuristicWeights[i] * this.confidence * this.maxUtil);
        }
        return scaledWeights;
    }

    // Create hashmap for heuristics
    private void mapWeights(String[] names, int[] weights){
        for (int i = 0; i < weights.length; i++){
            this.weightMap.put(names[i], weights[i]);
        }
    }

    @Override
    public Position decideMove(GameState state){

        // Initialize attributes
        this.playerID = state.getPlayerInTurn();
        this.otherPlayerID = this.playerID == 1 ? 2 : 1;
        this.playerIndex = playerID - 1;
        this.otherPlayerIndex = this.otherPlayerID - 1;

        this.mapWeights(this.heuristicNames, this.scaleHeuristics(this.heuristicWeights));

        // For the sake of readability and modularity, minimax-search is
        // separated from inherited method decideMove
        return miniMaxSearch(state);
    }

    public Position miniMaxSearch(GameState state){

        // Initialize extrema constants
        AlphaBeta extrema = new AlphaBeta();

        // Initilize depth object
        Depth depth = new Depth(100, 0);

        // Begin recursion
        Value value = this.maxValue(state, extrema, depth);

        // Return move
        return value.getMove();
    } 

    public Value maxValue(GameState state, AlphaBeta extrema, Depth depth) {
        
        // Initialize new instance of Value class
        Value vMax = new Value((int) Double.NEGATIVE_INFINITY, null);
        
        // Stop recursion if no moves are available
        if (state.legalMoves().isEmpty()) {
            
            // Calculate utility based on player and state
            int utility = this.utilityValue(state);
            vMax.setUtility(utility);
            return vMax;

        } else if ( depth.isMax() ){

            // Evaluate utility based on heuristics
            int evaluation = this.evaluateBoard(this.playerIndex, state);
            vMax.setUtility(evaluation);
            return vMax;


        } else {

            // If moves are available, increment recursion depth
            depth.increment();

            // Iterate through all legal moves
            for (Position action : state.legalMoves() ) {

                // Initialize child state
                GameState minState = new GameState(state.getBoard(), this.otherPlayerID);

                // Test move on child state
                minState.insertToken(action);
                minState.changePlayer();

                // Create new instance of depth tracker
                Depth newDepth = new Depth(this.maxDepth, depth.getCurrent());

                // Pass child state to minValue()
                Value vMin = minValue(minState, extrema, newDepth);

                // If action provides better utility, choose action
                if (vMin.getUtility() > vMax.getUtility()) {
                    vMax.setUtility(vMin.getUtility());
                    vMax.setMove(action);
                    extrema.setAlpha(maximum(new int[] {extrema.getAlpha(), vMax.getUtility()}));
                }

                // Beta cut
                if (vMax.getUtility() >= extrema.getBeta()) {
                    return vMax;
                }
           }
           return vMax;
       }
    }

    public Value minValue(GameState state, AlphaBeta extrema, Depth depth) {

        // Initialize return variable
        Value vMin = new Value((int) Double.POSITIVE_INFINITY, null);
        
        // End condition
        if (state.legalMoves().isEmpty()) {
            int utility = this.utilityValue(state);
            vMin.setUtility(utility);
            return vMin;

        } else if ( depth.isMax() ){
            int evaluation = this.evaluateBoard(this.otherPlayerIndex, state);
            vMin.setUtility(evaluation);
            return vMin;


        } else {

            depth.increment();
            for (Position action : state.legalMoves() ) {

                // Initialize child state
                GameState maxState = new GameState(state.getBoard(), this.playerID);

                // Test move on child state
                maxState.insertToken(action);
                maxState.changePlayer();

                Depth newDepth = new Depth(this.maxDepth, depth.getCurrent());

                // Pass child state to minValue()
                Value vMax = maxValue(maxState, extrema, newDepth);

                // If action provides better utility, choose action
                if (vMax.getUtility() < vMin.getUtility()) {

                    vMin.setUtility(vMax.getUtility());
                    vMin.setMove(action);
                    extrema.setBeta(minimum(new int[] {extrema.getBeta(), vMin.getUtility()}));
                }
                if (vMin.getUtility() <= extrema.getAlpha()) {
                    return vMin;
                }
            }
            return vMin;
       }
    }
    

    // HEURISTICS
    
    // Should always return a value between -1 and 1

    // Parity is the ratio of difference of tokens
    // to the total number of tokens

    private int rowRecursionDecrement(int[][] board, int ref, int depth, int playerID){
        int cell = 0;
        while (cell < ref){
            if (board[depth][cell] != playerID){
                return 0;
            }
            cell++;
        }
        if (depth == board.length-1){
            int adjacent = board.length - cell - 1;
            return board.length * board.length - adjacent*adjacent/2;
        } else if (cell > 0){
            return rowRecursionDecrement(board, cell-1, depth+1, playerID) + rowRecursionIncrement(board, cell, depth+1, playerID);
        } else {
            return ( (depth+1) * (depth+1) ) / 2;
        }
    }

    private int rowRecursionIncrement(int[][] board, int ref, int depth, int playerID){
        if (depth == board.length-1){
            return 1;
        }
        if (board[depth][ref] != playerID){
            return 0;
        } else if (board[depth][ref] == playerID){
            int next = rowRecursionIncrement(board, ref, depth+1, playerID);
            if (next > 0){
                return 1 + next;
            }
        } 
        System.out.println("Unexpected option in rowRecursionIncrement");
        return 0;
    }


    private int stableArea(GameState state){
        // Initialize parameters
        int[][] board = state.getBoard();
        int size = board.length;
        int[][] revBoard = new int[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                revBoard[i][j] = board[size-i-1][size-j-1];
            }
        }

        int ref = 0;
        int depth = 0;
        int start = board[0][0]; // PlayerID

        // Find the reference value
        while (board[0][ref+1] == start && ref+1 < size) {ref++;}
        int area = rowRecursionDecrement(board, ref, depth+1, start);

        start = revBoard[0][0];
        while (revBoard[0][ref+1] == start && ref+1 < size) {ref++;}
        int revArea = rowRecursionDecrement(board, ref, depth+1, start);


        return area + revArea;

    }


    private int evaluateBoard(int playerIndex, GameState state){
        int evaluation = 0;
        evaluation += this.weightMap.get("stableArea")*this.stableArea(state);

        return evaluation;
    }

    private int utilityValue(GameState state){
        if ( state.isFinished() ){
            int[] tokens = state.countTokens();
            if ( tokens[this.playerIndex] > tokens[this.otherPlayerIndex] ){
                return (int) this.maxUtil;
            } else {
                return (int) -this.maxUtil;
            }
        } else {
            return this.evaluateBoard(state.getPlayerInTurn() - 1, state);
        }
    }

    // HELPER FUNCTIONS
    private int minimum(int[] arr) {
        // Returns the minimum value of an int array
        int min = (int) Double.POSITIVE_INFINITY;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        return min;
    }
    
    private int maximum(int[] arr) {
        // Returns the maximum value of an int array
        int max = (int) Double.NEGATIVE_INFINITY;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }
}
