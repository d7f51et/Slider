package pcapreader;

public class PacketInfo implements Comparable{
    public long timestamp;
    public String sip;
    public String dip;

    public PacketInfo(long timestamp, String sip, String dip){
        this.timestamp = timestamp;
        this.sip = sip;
        this.dip = dip;
    }

    public int compareTo(Object o){
        PacketInfo p = (PacketInfo)o;
        if(this.timestamp > p.timestamp){
            return 1;
        }
        else if(this.timestamp == p.timestamp){
            return 0;
        }
        else return -1;
    }
}
