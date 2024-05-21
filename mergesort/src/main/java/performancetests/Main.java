package performancetests;

import performancetests.mergesort.DataGenerater;
import performancetests.mergesort.DataImporter;
import performancetests.mergesort.Mergesort;
import performancetests.mergesort.MergesortVirtualThreads;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        String[] input = new String[3];

        input[0] = "virtual";
        input[1] = "10000";
        input[2] = "10000";

        System.out.println(input[0]+", "+input[1]+", "+input[2]);

        long time;

        if(input.length == 3){
            File data = DataGenerater.generateData(Integer.parseInt(input[1]), Integer.parseInt(input[2]));
            List<int[]> list = DataImporter.importData(data.getName());
            System.out.println("Data Import Done");
            if (input[0].equals("single")){
                time = System.currentTimeMillis();
                Mergesort.runAllMergeSort(list);
                time = time - System.currentTimeMillis();
            } else if (input[0].equals("virtual")) {
                time = System.currentTimeMillis();
                MergesortVirtualThreads.runAllMergeSort(list);
                time = time - System.currentTimeMillis();
            }else{
                System.out.println("Unknown algorithm, fallback: virtual");
                input[0] = "virtual";
                time = System.currentTimeMillis();
                MergesortVirtualThreads.runAllMergeSort(list);
                time = time - System.currentTimeMillis();
            }

            System.out.println(input[0] + ", Time: " + Duration.ofMillis(time));
        }else {
            System.out.println("Wrong Arguments");
        }
    }
}