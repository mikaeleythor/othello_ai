public class Depth {
    private int max;
    private int current;

    public Depth(int max, int start){
        this.max = max;
        this.current = start;
    }

    public Boolean isMax(){
        return this.current == this.max;
    }

    public void increment(){
        this.current++;
    }

    public int getCurrent(){
        return this.current;
    }

    public int getMax(){
        return this.max;
    }
   
    
    
}
