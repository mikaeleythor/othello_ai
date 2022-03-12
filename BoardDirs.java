import java.util.ArrayList;

public class BoardDirs {
    
    private ArrayList<Integer> indices;
    private int dir;
    private int focus = 0;

    public BoardDirs(int size, int dir){
        this.dir = dir;
        this.focus = focus;
        this.indices = new ArrayList<>();

        if (this.dir < 0){
            for (int i = 0; i < size; i++){
                this.indices.add(size-i-1);
            }
        } else if (this.dir > 0){
            for (int i = 0; i < size; i++){
                this.indices.add(i);
            }
        }
    }

    public void resetFocus(){
        this.focus = 0;
    }

    public int get(){
        return this.indices.get(this.focus);
    }

    public void increment(){
        this.focus++;
    }

    public int getIncrement(){
        // Get value in focus
        int value = this.indices.get(this.focus);
        
        // Increment
        this.focus ++;
        return value;
    }

    public int getNextIncrement(){
        // Get value in focus
        int value = this.indices.get(this.focus+1);
        
        // Increment
        this.focus ++;
        return value;
    }

}
