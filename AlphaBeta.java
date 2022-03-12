
public class AlphaBeta {
    private int alpha;
    private int beta;

    public AlphaBeta() {
        this.alpha = (int) Double.NEGATIVE_INFINITY;
        this.beta = (int) Double.POSITIVE_INFINITY;
    }

    public int getAlpha() {
        return this.alpha;
    }

    public void setAlpha(int newAlpha) {
        this.alpha = newAlpha;
    }

    public int getBeta() {
        return this.beta;
    }

    public void setBeta(int newBeta) {
        this.beta = newBeta;
    }
    
}
