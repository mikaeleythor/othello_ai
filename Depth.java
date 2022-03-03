public class Depth {
    private int max;
    private int current;

    public Depth(int max){
        this.max = max;
        this.current = 0;
    }

    public Boolean isMax(){
        return this.current == this.max;
    }

    public void increment(){
        this.current++;
    }
    
    
    
}
