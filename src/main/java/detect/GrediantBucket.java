package detect;

public class GrediantBucket implements Comparable{
    public int grediant;
    public int col;

    public GrediantBucket(int grediant, int col){
        this.grediant = grediant;
        this.col = col;
    }

    public int compareTo(Object o){
        GrediantBucket g = (GrediantBucket)o;

        if(this.grediant > g.grediant){
            return 1;
        }
        else if(this.grediant < g.grediant){
            return -1;
        }
        else return 0;
    }
}