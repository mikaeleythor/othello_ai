import java.util.*;


public class AdversarialSearchAI implements IOthelloAI{
    
    // Player id is either 1 or 2
    private int playerID;
    private int playerIndex;

    // Relative index is ID - 1
    private int otherPlayerID;
    private int otherPlayerIndex;

    // Max recursion depth
    private int maxDepth = 10;

    // Main goal is winning not acquiring tokens, so winning-utility is hardcoded
    private float maxUtil = 100f;

    // Heuristics are not perfect, utility of possible win is higher
    private float confidence = 0.7f;

    // Heuristic names and weight specified
    private String[] heuristicNames = {"parity", "mobility", "corners", "divergence"};
    private float[] heuristicWeights = {0.3f, 0.1f, 0.3f, 0.3f};

    // HashMap for heuristic names and weights for ease of use
    private HashMap<String, Integer> weightMap = new HashMap<String, Integer>();

    // This method scales the weights of the heuristic utilities such that
    // their values do not superceed values of solid utility functions
    private int[] scaleHeuristics(float[] heuristicWeights){

        // Initialize weights
        int[] scaledWeights = {0, 0, 0, 0};
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

    // Corner capture is the ratio of difference of captured corners
    // to the total number of corners
    private int corners(GameState state){
        int[][] board = state.getBoard();
        int end = board.length;
        
        int playerCorners = 0;
        int otherPlayerCorners = 0;
        for (int i = 0; i < end; i++){
            for (int j = 0; j < end; j++){
                if ( board[i][j] == this.playerID ){
                    playerCorners ++;
                } else if ( board[i][j] == this.otherPlayerID ){
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

    private int divergence(GameState state){
        int[][] board = state.getBoard();
        int length = board.length;
        int shift = length/2;
        int start = 0 - shift;
        int end = shift;

        int playerDivergence = 0;
        int otherPlayerDivergence = 0;
        for (int i = start; i < end; i++){
            int x = i+shift;
            for (int j = start; j < end; j++){
                int y = j+shift;
                if (board[x][y] == this.playerID){
                    playerDivergence += i*i + j*j;
                } else if (board[x][y] == this.otherPlayerID){
                    otherPlayerDivergence += i*i + j*j;
                }
            }
        }
        return ( playerDivergence - otherPlayerDivergence ) / (playerDivergence + otherPlayerDivergence );
    }

    private int evaluateBoard(int playerIndex, GameState state){
        int evaluation = 0;
        evaluation += this.weightMap.get("parity")*this.parity(state);
        evaluation += this.weightMap.get("mobility")*this.mobility(state);
        evaluation += this.weightMap.get("corners")*this.corners(state);
        evaluation += this.weightMap.get("divergence")*this.divergence(state);

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
