package performancetests;

import performancetests.mergesort.*;

import java.io.File;
import java.time.Duration;

public class Main {
    public static void main(String[] args) {

        String[] input = new String[3];

        input[0] = "virtual";
        input[1] = "50000000";
        int chunkSize = Integer.parseInt(input[1])/8;

        System.out.println(input[0]+", "+input[1]);

        File data = DataGenerator.generateData(Integer.parseInt(input[1]));
        int[] list = DataImporter.importData(data.getName());

        long time = 0;

        switch (input[0]) {
            case "single" -> {
                time = System.currentTimeMillis();
                Mergesort.mergeSort(list);
                time = time - System.currentTimeMillis();
            }
            case "virtual" -> {
                time = System.currentTimeMillis();
                new MergesortVirtualThreads(chunkSize).mergeSort(list);
                time = time - System.currentTimeMillis();
            }
            case "platform" -> {
                time = System.currentTimeMillis();
                MergesortPlatformThreads pt = new MergesortPlatformThreads(chunkSize);
                pt.mergeSort(list);
                pt.shutdown();
                time = time - System.currentTimeMillis();
            }
            default -> {
                System.out.println("Unknown algorithm, fallback: single");
                input[0] = "single";
                time = System.currentTimeMillis();
                Mergesort.mergeSort(list);
                time = time - System.currentTimeMillis();
            }
        }

        System.out.println(input[0] + ", Time: " + Duration.ofMillis(time));

//        int i = 0;
//        for (int num : list) {
//            System.out.print(num + ", ");
//            i++;
//            if(i > 1000){
//                break;
//            }
//        }

    }
}