import java.util.*;


public class MinimaxAI implements IOthelloAI{
    
    // Player id is either 1 or 2
    private int playerID;
    private int playerIndex;
    private int otherPlayerID;

    @Override
    public Position decideMove(GameState state){
        // Initialize attributes
        this.playerID = state.getPlayerInTurn();
        this.otherPlayerID = this.playerID == 1 ? 2 : 1;
        this.playerIndex = playerID - 1;

        // For readability, minimax-search is
        // separated from inherited method decideMove
        return miniMaxSearch(state);
    }

    public Position miniMaxSearch(GameState state){
        Value value = this.maxValue(state);
        return value.getMove();
    } 

    public Value maxValue(GameState state){
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
               Value vMin = minValue(minState);

               // If action provides better utility, choose action
               if (vMin.getUtility() > vMax.getUtility()) {
                   vMax.setUtility(vMin.getUtility());
                   vMax.setMove(action);
               }
           }
           return vMax;
       }
    }

    public Value minValue(GameState state){
        // Initialize return variable
        Value vMin = new Value((int) Double.POSITIVE_INFINITY, null);
        // End condition
        if (state.legalMoves().isEmpty()) {

           int utility = state.countTokens()[this.otherPlayerID-1];
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
               Value vMax = maxValue(maxState);

               // If action provides better utility, choose action
               if (vMax.getUtility() < vMin.getUtility()) {
                   vMin.setUtility(vMax.getUtility());
                   vMin.setMove(action);
               }
           }
           return vMin;
       }
    }
}
