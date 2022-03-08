import java.util.ArrayList;
import java.util.Arrays;

public class IncDirs {

    private ArrayList<Integer> dirs;

    private ArrayList<Integer> setDirs(){
        Integer[] dirs = new Integer[] {1,0,-1};
        return new ArrayList<>(Arrays.asList(dirs));
    }

    public IncDirs(){
        this.dirs = setDirs();
    }

    public int getDir(){
        return this.dirs.get(0);
    }

    public void popDir(){
        this.dirs.remove(0);
    }

    public Boolean noDirs(){
        return this.dirs.isEmpty();
    }
}
