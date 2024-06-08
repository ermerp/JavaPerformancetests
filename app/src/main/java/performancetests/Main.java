package performancetests;

import performancetests.mergesort.*;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String[] input = new String[3];

        input[0] = "platform";
        input[1] = "100";
        input[2] = "100";

        System.out.println(input[0]+", "+input[1]+", "+input[2]);

        long time = 0;

        if(input.length == 3){
            File data = DataGenerator.generateData(Integer.parseInt(input[1]), Integer.parseInt(input[2]));
            List<int[]> list = null;
            if (input[0].equals("single")){
                time = System.currentTimeMillis();
                list = Mergesort.runAllMergeSort(data.getName());
                time = time - System.currentTimeMillis();
            } else if (input[0].equals("virtual")) {
                time = System.currentTimeMillis();
                list = MergesortVirtualThreads.runAllMergeSort(data.getName());
                time = time - System.currentTimeMillis();
            } else if (input[0].equals("platform")) {
                time = System.currentTimeMillis();
                list = MergesortPlatformThreads.runAllMergeSort(data.getName());
                time = time - System.currentTimeMillis();
            }else{
                System.out.println("Unknown algorithm, fallback: virtual");
                input[0] = "virtual";
                time = System.currentTimeMillis();
                list = MergesortVirtualThreads.runAllMergeSort(data.getName());
                time = time - System.currentTimeMillis();
            }

//            for (int[] array : list) {
//                for (int num : array) {
//                    System.out.print(num + " ");
//                }
//                System.out.println();
//            }

            System.out.println(input[0] + ", Time: " + Duration.ofMillis(time));
        }else {
            System.out.println("Wrong Arguments");
        }
    }
}