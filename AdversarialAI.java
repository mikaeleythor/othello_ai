import java.util.*;
import java.lang.*;


public class AdversarialAI implements IOthelloAI{
    
    // Player id is either 1 or 2
    private int playerID;
    private int playerIndex;

    // Relative index is ID - 1
    private int otherPlayerID;
    private int otherPlayerIndex;

    // Max recursion depth
    private int maxDepth = 8;

    // Main goal is winning not acquiring tokens, so winning-utility is hardcoded
    private float maxUtil = 50f;

    // Heuristics are not perfect, utility of possible win is higher
    private float confidence = 1.0f;

    // Heuristic names and weight specified
    private String[] heuristicNames = {"cornerNeighbors","mobility","corners","parity","stableArea"};
    private float[] heuristicWeights = {.661f,.000331f,.331f,.000661f,.0073f};

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
        Depth depth = new Depth(this.maxDepth, 0);
        System.out.println("Initializing new depth object with current: " + depth.getCurrent());

        // Begin recursion
        Value value = this.maxValue(state, extrema, depth);

        System.out.println("---------------------------------------");
        System.out.println("Return final value: "+value.getUtility());
        System.out.println("---------------------------------------");

        // Return move
        return value.getMove();
    } 

    public Value maxValue(GameState state, AlphaBeta extrema, Depth depth) {
        
        // Initialize new instance of Value class
        Value vMax = new Value((int) Double.NEGATIVE_INFINITY, null);
        
        // Stop recursion if no moves are available
        if (state.legalMoves().isEmpty()) {
            
            // Calculate utility based on player and state
            //int utility = this.utilityValue(state);
            int utility = this.evaluateBoard(state);
            vMax.setUtility(utility);

            System.out.println("No moves for max at depth: " + depth.getCurrent());
            return vMax;

        } else if ( depth.isMax() ){

            // Evaluate utility based on heuristics
            int evaluation = this.evaluateBoard(state);
            vMax.setUtility(evaluation);
            System.out.println("Maximum depth reached at max: " + depth.getCurrent());
            System.out.println("return evaluation: " + vMax.getUtility());
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
                if (vMax.getUtility() > extrema.getBeta() && vMax.getUtility() > 0){
                    System.out.println("Beta cut");
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
            //int utility = this.utilityValue(state);
            int utility = this.evaluateBoard(state);
            vMin.setUtility(utility);
            System.out.println("No moves for min at depth: " + depth.getCurrent());
            System.out.println("return evaluation: " + vMin.getUtility());
            return vMin;

        } else if ( depth.isMax() ){
            int evaluation = this.evaluateBoard(state);
            vMin.setUtility(evaluation);
            System.out.println("Maximum depth reached at min: " + depth.getCurrent());
            System.out.println("return evaluation: " + vMin.getUtility());
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
                if (vMin.getUtility() < extrema.getAlpha() && vMin.getUtility() < 0) {
                    System.out.println("Alpha cut");
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
            } else if (next == 0){
                return 0;
            }
        } 
        System.out.println("Unexpected option in rowRecursionIncrement");
        System.out.println("Board recursion depth: " + depth);
        return 0;
    }


    private int stableArea(GameState state){
        
        // Initialize parameters
        int[][] board = state.getBoard();
        int size = board.length;

        int[][] board90 = new int[size][size]; 
        int[][] board180 = new int[size][size];
        int[][] board270 = new int[size][size];

        int area = 0;
        int area90 = 0;
        int area180 = 0;
        int area270 = 0;


        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                board90[i][j] = board[size-j-1][i];
                board180[i][j] = board[size-i-1][size-j-1];
                board270[i][j] = board[j][size-i-1];
            }
        }

        int depth = 0;

        // Find the reference value
        int start = board[0][0]; // PlayerID
        int ref = 0;

        if (start != 0){
            while ( ref+1 < size && board[0][ref+1] == start ) {ref++;}
            area = rowRecursionDecrement(board, ref, depth+1, start)*(start == this.playerID? 1 : -1);
        }

        ref = 0;
        start = board90[0][0];
        if (start != 0){
            while ( ref+1 < size && board90[0][ref+1] == start ) {ref++;}
            area90 = rowRecursionDecrement(board90, ref, depth+1, start)*(start == this.playerID? 1 : -1);
        }

        ref = 0;
        start = board180[0][0];
        if (start != 0){
            while ( ref+1 < size && board180[0][ref+1] == start ) {ref++;}
            area180 = rowRecursionDecrement(board180, ref, depth+1, start)*(start == this.playerID? 1 : -1);
        }

        ref = 0;
        start = board270[0][0];
        if (start != 0){
            while ( ref+1 < size && board270[0][ref+1] == start ) {ref++;}
            area270 = rowRecursionDecrement(board270, ref, depth+1, start)*(start == this.playerID? 1 : -1);
        }

        int absTotal = Math.abs(area) + Math.abs(area90) + Math.abs(area180) + Math.abs(area270);
        int total = area + area90 + area180 + area270;

        if (absTotal != 0){
            System.out.println("predicted net area: " + total);
            System.out.println("predicted total area: " + absTotal);

            return total/absTotal;
        }
        return 0;

    }

    private int corners(GameState state){
        int[][] board = state.getBoard();
        
        int playerCorners = 0;
        int otherPlayerCorners = 0;
        
        int[] indices = cornerIndices(state);

        for (int i = 0; i < 2; i++){
            int x = indices[i];
            for (int j = 0; j < 2; j++){
                int y = indices[j];
                if ( board[x][y] == this.playerID ){
                    playerCorners ++;
                } else if ( board[x][y] == this.otherPlayerID ){
                    otherPlayerCorners ++;
                }
            }
        }
        int capturedCorners = playerCorners + otherPlayerCorners;
        if ( capturedCorners > 0 ){
            return ( playerCorners - otherPlayerCorners ) / capturedCorners;
        } else {
            return 0;
        }
    }

    private int cornerNeighbors(GameState state){
        int[][] board = state.getBoard();
        int playerCornerNeighbors = 0;
        int otherPlayerCornerNeighbors = 0;
        
        int[] indices = cornerIndices(state);

        for (int i = 0; i < 2; i++){
            int x = indices[i];
            for (int j = 0; j < 2; j++){
                int y = indices[j];
                if (board[x][y] == 0){
                    playerCornerNeighbors += this.countCornerNeighbors(x,y,board,this.playerID);
                    otherPlayerCornerNeighbors += this.countCornerNeighbors(x,y,board,this.otherPlayerID);
                } else if (board[x][y] == this.playerID){
                    otherPlayerCornerNeighbors += this.countCornerNeighbors(x,y,board,this.otherPlayerID);
                } else if (board[x][y] == this.otherPlayerID){
                    playerCornerNeighbors += this.countCornerNeighbors(x,y,board,this.playerID);
                }
            }
        }
        int total = playerCornerNeighbors + otherPlayerCornerNeighbors;
        if (total > 0){
            // Important to return a negative here
            return (otherPlayerCornerNeighbors - playerCornerNeighbors )/total;
        }
        return 0;
    }

    private int countCornerNeighbors(int x,int y,int[][] board,int player){

        int playerCornerNeighbor = 0;

        int xx = x == 0 ? 1 : board.length-2;
        int yy = y == 0 ? 1 : board.length-2;

        int[] xIndices = {x, xx, xx};
        int[] yIndices = {yy, y, yy};

        for (int i = 0; i < 3; i++){
            if (board[xIndices[i]][yIndices[i]] == player){
                playerCornerNeighbor++;
            }
        }
        return playerCornerNeighbor;
    }
        

    private int[] cornerIndices(GameState state){
        int[][] board = state.getBoard();
        int end = board.length;
        int[] indices = {0, end-1};
        return indices;
    }

    // Parity is the ratio of difference of tokens
    // to the total number of tokens
    private int parity(GameState state){
        int[] tokenArray = state.countTokens();
        return (tokenArray[this.playerIndex] - tokenArray[otherPlayerIndex]) / (tokenArray[0] + tokenArray[1]);
    }

    // Mobility is the ratio of diffence of legal moves
    // to the total number of legalMoves
    private int mobility(GameState state){
        ArrayList<Position> playerLegalMoves = state.legalMoves();
        state.changePlayer();
        ArrayList<Position> otherPlayerLegalMoves = state.legalMoves();
        state.changePlayer();
        int mobility = (playerLegalMoves.size() - otherPlayerLegalMoves.size()) / (playerLegalMoves.size() + otherPlayerLegalMoves.size());
        return state.getPlayerInTurn() == this.playerID ? mobility : -mobility;
    }

    private int evaluateBoard(GameState state){
        int evaluation = 0;
        evaluation += this.weightMap.get("parity")*this.parity(state);
        evaluation += this.weightMap.get("cornerNeighbors")*this.cornerNeighbors(state);
        //evaluation += this.weightMap.get("mobility")*this.mobility(state);
        evaluation += this.weightMap.get("corners")*this.corners(state);
        evaluation += this.weightMap.get("stableArea")*this.stableArea(state);
        return evaluation;
    }

    private int utilityValue(GameState state){
        if ( state.isFinished() ){
            int[] tokens = state.countTokens();
            if ( tokens[this.playerIndex] > tokens[this.otherPlayerIndex] ){
                System.out.println("Win! Returing maxUtil: "+this.maxUtil);
                return (int) this.maxUtil;
            } else {
                System.out.println("Defeat! Returing minUtil: "+(-this.maxUtil));
                return (int) -this.maxUtil;
            }
        } else {
            return this.evaluateBoard(state);
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
