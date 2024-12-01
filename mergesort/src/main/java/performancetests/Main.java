package performancetests;

import performancetests.mergesort.*;

import java.io.File;
import java.time.Duration;

public class Main {

    public static void main(String[] args) {

        String algorithm = (args.length > 0 && args[0] != null && !args[0].isEmpty())
                ? args[0] : "virtual";
        int listLength = (args.length > 1 && args[1] != null && !args[1].isEmpty())
                ? Integer.parseInt(args[1]) : 30000000;
        //Max: 60000000
        int chunkNumber = (args.length > 2 && args[2] != null && !args[2].isEmpty())
                ? Integer.parseInt(args[2]) : 16;
        int runs = (args.length > 3 && args[3] != null && !args[3].isEmpty())
                ? Integer.parseInt(args[3]) : 1;
        int warmUpRuns = (args.length > 4 && args[4] != null && !args[4].isEmpty())
                ? Integer.parseInt(args[4]) : 0;
        int chunkSize = listLength/chunkNumber;

        System.out.println("Java:Mergesort - Algorithm: " + algorithm
                + ", List length: " + listLength
                + ", Chunk number: " + chunkNumber
                + ", Runs: " + runs + ", Warm up runs: " + warmUpRuns);

        File data = DataGenerator.generateData(listLength);
        int[] list = DataImporter.importAccounts(data.getName());

        for (int i = 0; i < warmUpRuns; i++) {
            int[] list2 = list.clone();
            runAlgorithm(algorithm, list2, chunkSize);
        }

        System.out.println("File imported.");

        long time = 0;

        time = System.currentTimeMillis();

        for (int i = 0; i < runs; i++) {
            int[] list2 = list.clone();
            runAlgorithm(algorithm, list2, chunkSize);
        }

        time = System.currentTimeMillis() - time;

        System.out.println("Java - " + algorithm + ", Time: " + Duration.ofMillis(time));

    }

    private static void runAlgorithm(String algorithm, int[] list, int chunkSize) {

        switch (algorithm) {
            case "single" -> {
                Mergesort.mergeSort(list);
            }
            case "virtual" -> {
                new MergesortVirtualThreads(chunkSize).mergeSort(list);
            }
            case "platform" -> {
                MergesortPlatformThreads pt = new MergesortPlatformThreads(chunkSize);
                pt.mergeSort(list);
                pt.shutdown();
            }
            default -> {
                System.out.println("Unknown algorithm");
            }
        }

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