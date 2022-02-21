public class Value {
    private int utility;
    private Position move;

    public Value(int utility, Position move){
        this.utility = utility;
        this.move = move;
    }

    public int getUtility() {
        return utility;
    }
    public Position getMove() {
        return move;
    }
    public void setMove(Position move) {
        this.move = move;
    }
    public void setUtility(int utility) {
        this.utility = utility;
    }

    @Override
    public String toString(){
        return "(" + this.utility + "," + move + ")";
    }

    
    
}
