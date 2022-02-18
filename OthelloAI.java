public class OthelloAI implements IOthelloAI {

    @Override
    public Position decideMove(GameState s) {
        return maxValue(s).getMove();
    }

    private Value maxValue(GameState s) {
        if (s.isFinished())
            return new Value(utility(s), null);
        double utility = Double.NEGATIVE_INFINITY;// i'm not sure about this one
        Value resultValue = new Value((int) utility, null);

        for (Position move : s.legalMoves()) {
            GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
            if (newState.insertToken(move)) {
                newState.changePlayer();
                Value tempValue = minValue(newState);
                if (resultValue.getUtility() < tempValue.getUtility())
                    resultValue = new Value(tempValue.getUtility(), move);
            }
        }

        System.out.println("move utility: " + resultValue.getUtility());
        return resultValue;
    }

    private Value minValue(GameState s) {
        if (s.isFinished())
            return new Value(utility(s), null);
        double utility = Double.POSITIVE_INFINITY;// i'm not sure about this one
        Value resultValue = new Value((int) utility, null);

        for (Position move : s.legalMoves()) {
            GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
            if (newState.insertToken(move)) {
                newState.changePlayer();
                Value tempValue = maxValue(newState);
                if (resultValue.getUtility() > tempValue.getUtility())
                    resultValue = new Value(tempValue.getUtility(), move);
            }
        }
        return resultValue;
    }

    private GameState result(GameState state, Position action) {
        return null;
    }

    private int utility(GameState s) {
        return s.countTokens()[0];
    }

}
