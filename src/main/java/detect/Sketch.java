package detect;

import java.util.ArrayList;
import java.util.Collections;

public class Sketch {
    public int H;
    public int K;
    public int p;
    private int a[] = {34,96,5,13,442,17,987,33};
    private int b[] = {26,77,344,563,2,91,237,8};

    public ArrayList<ArrayList<SketchBucket>> matrix;
    public ArrayList<Integer> hashSums;

    public Sketch(){
        this.H = 8;
        this.K = 4096;
        this.p = 4093;

        this.matrix = new ArrayList<ArrayList<SketchBucket>>(this.H);
        for(int i = 0;i < this.H;i++){
            this.matrix.add(i, new ArrayList<SketchBucket>(this.K));
            for(int j = 0;j < this.K;j++){
                this.matrix.get(i).add(new SketchBucket());
            }
        }

        this.hashSums = new ArrayList<Integer>(this.H);
        for(int i = 0;i < this.H;i++){
            this.hashSums.add(0);
        }
    }

    public static long ipToLong(String ip) {
        String[] tmp = ip.split("\\.");
        if (tmp.length != 4) {
            System.out.println("ipToLong error");
            return 0;
        }
        return Long.parseLong(tmp[0]) * 16777216 + Long.parseLong(tmp[1]) * 65536
                + Long.parseLong(tmp[2]) * 256 + Long.parseLong(tmp[3]);
    }

    public static String longToIP(long ip) {
        long[] weights = {16777216, 65536, 256, 1};

        String res = "";
        for (long l : weights) {
            res += ip / l + ".";
            ip = ip % l;
        }
        return res.substring(0, res.length() - 1);
    }

    public int getMinCounter(String sip){
        long key = ipToLong(sip);
        int res = 10000;

        for(int i = 0;i < this.H;i++){
            int hashed = (int)((key * this.a[i] + this.b[i]) % this.p);
            if(this.matrix.get(i).get(hashed).counter < res){
                res = this.matrix.get(i).get(hashed).counter;
            }
        }

        return res;
    }

    public void process(String sip, int value){
        long key = ipToLong(sip);

        for(int i = 0;i < this.H;i++){
            int hashed = (int)((key * this.a[i] + this.b[i]) % this.p);
            this.matrix.get(i).get(hashed).counter += value;
            this.hashSums.set(i, this.hashSums.get(i) + value);
        }
    }

    public double computeHDdistance(ArrayList<Double> probVec1, ArrayList<Double> probVec2){
        double distance = 0;
        for(int j = 0;j < this.K;j++){
            double tmp = Math.pow(probVec1.get(j), 0.5) - Math.pow(probVec2.get(j), 0.5);
            distance += tmp * tmp;
        }

        distance = 0.70710678 * Math.pow(distance, 0.5);
        return distance;
    }


    /*
    public boolean getInsideSlideArea(ArrayList<SlideBucket> arr, int col, ArrayList<Integer> area){
        double height = Main.height;
        int upper_bound = arr.get(col).counter + (int)(0.5 * height);
        int lower_bound = arr.get(col).counter - (int)(0.5 * height);

        int left = col, right = col;
        while(left - 1 > 0 && arr.get(left - 1).counter < upper_bound){
            left--;
        }
        while(right + 1 < this.K && arr.get(right + 1).counter > lower_bound){
            right++;
        }

        if(right - left + 1 > Main.min_atk_num){
            area.add(left);
            area.add(right);
            return true;
        }
        else {
            return false;
        }
    }

    public int insideSlide2(ArrayList<SlideBucket> pre, ArrayList<SlideBucket> cur, int sum1, int sum2, int row, ArrayList<Double> probVec1, ArrayList<Double> probVec2){
        //compute the gradient of cur
        int height = Main.height;
        ArrayList<GrediantBucket> gres = new ArrayList<GrediantBucket>();

        for(int pos = 1;pos < this.K;pos++){
            if(cur.get(pos).counter < 3 * height){
                break;
            }

            GrediantBucket g = new GrediantBucket(cur.get(pos - 1).counter - cur.get(pos).counter, pos);
            if(g.grediant < height) {
                gres.add(g);
            }
        }

        Collections.sort(gres);

        boolean flag = false;
        int left = 0, right = 0;
        for(GrediantBucket g: gres){
            ArrayList<Integer> cur_area = new ArrayList<Integer>();
            ArrayList<Integer> pre_area = new ArrayList<Integer>();

            boolean cur_flag = getInsideSlideArea(cur, g.col, cur_area);
            boolean pre_flag = getInsideSlideArea(pre, g.col, pre_area);

            if(!pre_flag && cur_flag){
                flag = true;
                left = cur_area.get(0);
                right = cur_area.get(1);
                break;
            }
            else if(pre_flag && cur_flag){
                int cur_width = cur_area.get(1) - cur_area.get(0);
                int pre_width = pre_area.get(1) - pre_area.get(0);

                if(cur_width - pre_width > Main.min_atk_num){
                    flag = true;
                    left = cur_area.get(0);
                    right = cur_area.get(1);
                    break;
                }
            }
        }

        if(flag){
            //find the gentle slope [left:right]
            ArrayList<SlideBucket> slide_cur = new ArrayList<SlideBucket>(this.K);

            for(int j = 0; j < left;j++){
                slide_cur.add(cur.get(j));
            }
            for(int j = right + 1;j < cur.size();j++){
                slide_cur.add(cur.get(j));
            }
            for(int j = left;j < right + 1;j++){
                slide_cur.add(cur.get(j));
            }

            for(int j = 0;j < this.K;j++){
                probVec1.add((double)pre.get(j).counter / sum1);
                probVec2.add((double)slide_cur.get(j).counter / sum2);
            }

            //mark abnormal bucket
            for(int i = left; i <= right;i++){
                int col = cur.get(i).col;
                this.matrix.get(row).get(col).abnormal = true;
            }
            return right - left + 1;
        }
        else {
            return 0;
        }

    }
    */

    public int outsideSlide(ArrayList<SlideBucket> pre, ArrayList<SlideBucket> cur, int sum1, int sum2, int row, ArrayList<Double> probVec1, ArrayList<Double> probVec2){
        int pos = 0;
        while (pos < cur.size() && pre.get(0).counter + Main.slide_diff < cur.get(pos).counter) {
            pos += 1;
        }

        if(pos == 0){
            return 0;
        }

        ArrayList<SlideBucket> slide_cur = new ArrayList<SlideBucket>();
        slide_cur.addAll(cur.subList(pos, cur.size()));
        slide_cur.addAll(cur.subList(0, pos));

        for(int j = 0;j < this.K;j++){
            probVec1.add((double)pre.get(j).counter / sum1);
            probVec2.add((double)slide_cur.get(j).counter / sum2);
        }

        //mark abnormal
        for(int i = 0; i < pos;i++){
            int col = cur.get(i).col;
            this.matrix.get(row).get(col).abnormal = true;
        }

        return pos;
    }


    public int slide(ArrayList<SketchBucket> datapre, ArrayList<SketchBucket> datacur, int row, ArrayList<Double> probVec1, ArrayList<Double> probVec2){
        ArrayList<SlideBucket> arr1 = new ArrayList<SlideBucket>(this.K);
        ArrayList<SlideBucket> arr2 = new ArrayList<SlideBucket>(this.K);

        //make sum
        int sum1 = 0, sum2 = 0;
        for(int col = 0;col < datapre.size();col++){
            sum1 += datapre.get(col).counter;
            arr1.add(new SlideBucket(datapre.get(col).counter, row, col));
        }
        for(int col = 0;col < datacur.size();col++){
            sum2 += datacur.get(col).counter;
            arr2.add(new SlideBucket(datacur.get(col).counter, row, col));
        }

        Collections.sort(arr1, Collections.<SlideBucket>reverseOrder());
        Collections.sort(arr2, Collections.<SlideBucket>reverseOrder());

        int slide_distance = outsideSlide(arr1, arr2, sum1, sum2, row, probVec1, probVec2);
        if(slide_distance != 0){
            //outside slide
            return slide_distance;
        }
        else {
            //not slide
            for(int j = 0;j < this.K;j++){
                probVec1.add((double)arr1.get(j).counter / sum1);
                probVec2.add((double)arr2.get(j).counter / sum2);
            }
            return 0;
        }
    }

    public int slideTrain(ArrayList<SketchBucket> datapre, ArrayList<SketchBucket> datacur, int row, ArrayList<Double> probVec1, ArrayList<Double> probVec2){
        ArrayList<SlideBucket> arr1 = new ArrayList<SlideBucket>(this.K);
        ArrayList<SlideBucket> arr2 = new ArrayList<SlideBucket>(this.K);

        //make sum
        int sum1 = 0, sum2 = 0;
        for(int col = 0;col < datapre.size();col++){
            sum1 += datapre.get(col).counter;
            arr1.add(new SlideBucket(datapre.get(col).counter, row, col));
        }
        for(int col = 0;col < datacur.size();col++){
            sum2 += datacur.get(col).counter;
            arr2.add(new SlideBucket(datacur.get(col).counter, row, col));
        }

        Collections.sort(arr1, Collections.<SlideBucket>reverseOrder());
        Collections.sort(arr2, Collections.<SlideBucket>reverseOrder());

        for(int j = 0;j < this.K;j++){
            probVec1.add((double)arr1.get(j).counter / sum1);
            probVec2.add((double)arr2.get(j).counter / sum2);
        }

        int min_slide_diff = Integer.MAX_VALUE;
        for(int i = 0;i < Main.min_atk_num;i++){
            int diff = arr2.get(i).counter - arr1.get(0).counter;
            if(diff > 0 && diff < min_slide_diff){
                min_slide_diff = diff;
            }
        }

        return min_slide_diff;
    }

    public SlideDistance computeSlideDistance(Sketch s){
        if(s == null) return new SlideDistance(0, 0.0);

        int min_slide_distance = this.K;

        ArrayList<Double> min_slide_probVec1 = new ArrayList<Double>(this.K);
        ArrayList<Double> min_slide_probVec2 = new ArrayList<Double>(this.K);

        for(int i = 0;i < this.H;i++){
            ArrayList<Double> probVec1 = new ArrayList<Double>(this.K);
            ArrayList<Double> probVec2 = new ArrayList<Double>(this.K);

            int slide_distance;
            if(Main.train_state){
                //update slide_diff
                slide_distance = 0;
                int inc_value = slideTrain(s.matrix.get(i), this.matrix.get(i), i, probVec1, probVec2);
                if(inc_value != Integer.MAX_VALUE) {
                    Main.train_slide_diffs.add(inc_value);
                }
            }
            else {
                slide_distance = slide(s.matrix.get(i), this.matrix.get(i), i, probVec1, probVec2);
            }

            if(slide_distance < min_slide_distance){
                min_slide_distance = slide_distance;
                min_slide_probVec1 = probVec1;
                min_slide_probVec2 = probVec2;
            }
        }

        //compute distance
        double distance = computeHDdistance(min_slide_probVec1, min_slide_probVec2);

        return new SlideDistance(min_slide_distance, distance);
    }

    public boolean identifyMalicious(String ip){
        long key = ipToLong(ip);

        for(int i = 0;i < this.H;i++){
            int hashed = (int)((key * this.a[i] + this.b[i]) % this.p);
            if(!this.matrix.get(i).get(hashed).abnormal){
                return false;
            }
        }
        return true;
    }
}
