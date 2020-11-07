package detect;

public class SlideBucket implements Comparable{
    public int counter;
    public int row;
    public int col;

    public SlideBucket(int counter, int row, int col){
        this.counter = counter;
        this.row = row;
        this.col = col;
    }

    public int compareTo(Object o){
        SlideBucket s = (SlideBucket)o;

        if(counter > s.counter){
            return 1;
        }
        else if(counter < s.counter){
            return -1;
        }
        else return 0;
    }
}
