package detect;

import javafx.util.Pair;
import pcapreader.*;
import java.util.*;

public class Main {
    public static int slide_diff = 1000;
    public static double beta = 0.7;
    public static double lambda = 0.4;

    public static int detectInterval = 10;
    public static int min_atk_num = 4;
    public static int height = 50;

    public static boolean train_state = false;
    public static ArrayList<Pair<Integer, Integer>> train_time = new ArrayList<Pair<Integer, Integer>>();

    public static ArrayList<Integer> train_slide_diffs = new ArrayList<Integer>();

    public static long pkt_cnt = 0;

    public static void main(String[] args) throws Exception {
        //The input pcap file
        String pcapFile = "/data/test.pcap";
        PcapReader.setPcapReader(pcapFile);

        //Detection time interval
        detectInterval = 10;

        //Damping coefficient
        beta=0.7;

        //Threshold damping coefficient
        lambda=0.4;

        //Threshold of slide distance
        min_atk_num = 4;

        //Training time (second of day)
        //e.g. Training at 00:00:00 - 00:10:00
        train_time.add(new Pair<Integer, Integer>(0, 600));

        //Slider start to train and detect on the input pcap file
        DetectCore.slideDetect();
    }
}
