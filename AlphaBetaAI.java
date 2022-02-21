import java.util.*;


public class AlphaBetaAI implements IOthelloAI{
    
    // Player id is either 1 or 2
    private int playerID;
    private int playerIndex;
    private int otherPlayerID;
    private int otherPlayerIndex;

    @Override
    public Position decideMove(GameState state){

        // Initialize attributes
        this.playerID = state.getPlayerInTurn();
        this.otherPlayerID = this.playerID == 1 ? 2 : 1;
        this.playerIndex = playerID - 1;
        this.otherPlayerIndex = this.otherPlayerID - 1;

        // For readability, minimax-search is
        // separated from inherited method decideMove
        return miniMaxSearch(state);
    }

    public Position miniMaxSearch(GameState state){
        AlphaBeta extrema = new AlphaBeta();
        Value value = this.maxValue(state, extrema);
        return value.getMove();
    } 

    public Value maxValue(GameState state, AlphaBeta extrema) {
        // Initialize return variable
        Value vMax = new Value((int) Double.NEGATIVE_INFINITY, null);
        
        // End condition
        if (state.legalMoves().isEmpty()) {

           int utility = state.countTokens()[this.playerIndex];
           vMax.setUtility(utility);
           return vMax;

        } else {

           for (Position action : state.legalMoves() ) {

               // Initialize child state
               GameState minState = new GameState(state.getBoard(), this.otherPlayerID);

               // Test move on child state
               minState.insertToken(action);
               minState.changePlayer();

               // Pass child state to minValue()
               Value vMin = minValue(minState, extrema);

               // If action provides better utility, choose action
               if (vMin.getUtility() > vMax.getUtility()) {
                   vMax.setUtility(vMin.getUtility());
                   vMax.setMove(action);
                   extrema.setAlpha(maximum(new int[] {extrema.getAlpha(), vMax.getUtility()}));
               }
               if (vMax.getUtility() >= extrema.getBeta()) {
                    return vMax;
               }
           }
           return vMax;
       }
    }

    public Value minValue(GameState state, AlphaBeta extrema) {
        // Initialize return variable
        Value vMin = new Value((int) Double.POSITIVE_INFINITY, null);
        // End condition
        if (state.legalMoves().isEmpty()) {

           int utility = state.countTokens()[this.otherPlayerIndex];
           vMin.setUtility(utility);
           return vMin;

        } else {

           for (Position action : state.legalMoves() ) {

               // Initialize child state
               GameState maxState = new GameState(state.getBoard(), this.playerID);

               // Test move on child state
               maxState.insertToken(action);
               maxState.changePlayer();

               // Pass child state to minValue()
               Value vMax = maxValue(maxState, extrema);

               // If action provides better utility, choose action
               if (vMax.getUtility() < vMin.getUtility()) {
                   vMin.setUtility(vMax.getUtility());
                   vMin.setMove(action);
                   extrema.setBeta(minimum(new int[] {extrema.getBeta(), vMin.getUtility()}));
               }
               if (vMin.getUtility() <= extrema.getBeta()) {
                    return vMin;
               }
           }
           return vMin;
       }
    }

    public int minimum(int[] arr) {
        // Returns the minimum value of an int array
        int min = (int) Double.POSITIVE_INFINITY;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        return min;
    }
    
    public int maximum(int[] arr) {
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
