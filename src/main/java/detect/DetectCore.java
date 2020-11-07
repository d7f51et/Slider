package detect;

import javafx.util.Pair;
import pcapreader.PacketInfo;
import pcapreader.PcapReader;

import java.io.IOException;
import java.util.*;


public class DetectCore {
    public static double estimate_distance = 0.0;

    public static int getDeltaTime(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        return hour * 3600 + minute * 60 + second;
    }

    public static boolean checkDuringTrainingTime(long timestamp){
        int delta_time = getDeltaTime(timestamp);

        for(Pair<Integer, Integer> p: Main.train_time){
            if(delta_time >= p.getKey() && delta_time < p.getValue()){
                return true;
            }
        }
        return false;
    }


    public static double computeThreshold(double distance, double beta, double lambda){
        estimate_distance = beta * estimate_distance + (1 - beta) * distance;
        return estimate_distance + lambda * (1 - estimate_distance);
    }

    public static ArrayList<Integer> iqr(ArrayList<Integer> arr){
        Collections.sort(arr);
        int size = arr.size();
        int q1_index = (int)(size * 0.25);
        int q3_index = (int)(size * 0.75);
        int iqr = arr.get(q3_index) - arr.get(q1_index);
        double upper_bound = (double)arr.get(q3_index) + 1.5 * iqr;
        double lower_bound = (double)arr.get(q1_index) - 1.5 * iqr;

        ArrayList<Integer> res = new ArrayList<Integer>();
        for(int i: arr){
            if(i >= lower_bound && i <= upper_bound){
                res.add(i);
            }
        }
        return res;
    }

    public static void slideDetect() throws IOException{
        System.out.println("Detection Starts...");
        System.out.println("Timestamp\t\tHD distance\t\tDynamic threshold\t\tSlide distance\t\tDDoS state");


        long interval_cnt = 0;
        long last_time = 0;
        double threshold = 0.0;
        boolean ddos_state = false;
        boolean frozen_state = false;

        Sketch cur_sketch = null;
        Sketch last_sketch = null;
        Sketch last_normal_sketch = null;

        PacketInfo packetInfo = null;
        long begin_timestamp = 0;

        while((packetInfo = PcapReader.getNextPacket()) != null){
            long timestamp = packetInfo.timestamp;
            String sip = packetInfo.sip;

            Main.pkt_cnt += 1;

            if(interval_cnt == 0){
                begin_timestamp = timestamp;
            }

            if(!ddos_state && !Main.train_state && checkDuringTrainingTime(timestamp)){
                Main.train_state = true;
                System.out.println("Train Start...");
            }

            if(Main.train_state && !checkDuringTrainingTime(timestamp)){
                System.out.println("Train Over. Exec Mode");
                Main.train_state = false;

                Main.train_slide_diffs = iqr(Main.train_slide_diffs);

                int index = (int)(Main.train_slide_diffs.size() * 0.9);
                Main.slide_diff = Main.train_slide_diffs.get(index);
                System.out.println("[Adaptive parameter learning] slide_off: " + Main.slide_diff);
            }

            if(interval_cnt == 0 || timestamp >= last_time + Main.detectInterval){
                //enter into a new detect time interval
                last_time = timestamp;

                //compute HD distance
                double distance = 0.0;
                int min_slide_distance = 0;
                if(cur_sketch != null){
                    SlideDistance slideDistance =  cur_sketch.computeSlideDistance(last_normal_sketch);
                    distance = slideDistance.distance;
                    min_slide_distance = slideDistance.min_slide_distance;
                }

                if(threshold < 0.000001){
                    estimate_distance = distance;
                }

                if(threshold < 0.000001 || distance < threshold){
                    //HD distance < threshold
                    frozen_state  = false;

                    threshold = computeThreshold(distance, Main.beta, Main.lambda);
                }
                else{
                    //HD distance > threshold
                    frozen_state = true;
                }

                //new sketch
                last_sketch = cur_sketch;
                cur_sketch = new Sketch();
                if(!frozen_state){
                    last_normal_sketch = last_sketch;
                }

                //update ddos state
                if(frozen_state && min_slide_distance >= Main.min_atk_num && !Main.train_state){
                    ddos_state = true;
                }
                else {
                    ddos_state = false;
                }

                System.out.println(timestamp + "\t\t" + distance + "\t\t" + threshold + "\t\t" + min_slide_distance + "\t\t" + ddos_state);

                //process current packet
                cur_sketch.process(sip, 1);
                if(ddos_state && last_sketch != null && last_sketch.identifyMalicious(sip)){
                    //alarm
                    //System.out.println("[Alarm]: " + sip);
                }

                interval_cnt += 1;
            }
            else{
                //during the detect time interval
                //process current packet
                cur_sketch.process(sip, 1);
                if(ddos_state && last_sketch != null && last_sketch.identifyMalicious(sip)){
                    //alarm
                    //System.out.println("[Alarm]: " + sip);
                }
            }
        }
    }
}
