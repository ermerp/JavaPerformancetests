package performancetests;

import performancetests.mergesort.*;

import java.io.File;
import java.time.Duration;

public class Main {

    public static void main(String[] args) {

        // Retrieve the algorithm, list length, max depth, runs and warm up runs from the command line arguments
        String algorithm = (args.length > 0 && args[0] != null && !args[0].isEmpty())
                ? args[0] : "virtual";
        int listLength = (args.length > 1 && args[1] != null && !args[1].isEmpty())
                ? Integer.parseInt(args[1]) : 10000000;
        int maxDepth = (args.length > 2 && args[2] != null && !args[2].isEmpty())
                ? Integer.parseInt(args[2]) : 8;
        int runs = (args.length > 3 && args[3] != null && !args[3].isEmpty())
                ? Integer.parseInt(args[3]) : 1;
        int warmUpRuns = (args.length > 4 && args[4] != null && !args[4].isEmpty())
                ? Integer.parseInt(args[4]) : 0;

        System.out.println("Java:Mergesort - Algorithm: " + algorithm
                + ", List length: " + listLength
                + ", Max depth: " + maxDepth
                + ", Runs: " + runs + ", Warm up runs: " + warmUpRuns);

        // Generate data
        File data = DataGenerator.generateData(listLength);
        // Import data
        int[] list = DataImporter.importAccounts(data.getName());

        System.out.println("File imported.");

        // Warm up runs
        for (int i = 0; i < warmUpRuns; i++) {
            int[] list2 = list.clone();
            runAlgorithm(algorithm, list2, maxDepth);
        }

        System.out.println("warum up runs finished");

        long time = 0;

        time = System.currentTimeMillis();

        // Runs the algorithm
        for (int i = 0; i < runs; i++) {
            int[] list2 = list.clone();
            runAlgorithm(algorithm, list2, maxDepth);
        }

        time = System.currentTimeMillis() - time;

        System.out.println("Java - " + algorithm + ", Time: " + Duration.ofMillis(time));

    }

    private static void runAlgorithm(String algorithm, int[] list, int maxDepth) {

        switch (algorithm) {
            case "single" -> {
                Mergesort.runMergeSort(list);
            }
            case "virtual" -> {
                new MergesortVirtualThreads().runMergeSort(list.clone(), maxDepth);
            }
            case "platform" -> {
                MergesortPlatformThreads pt2 = new MergesortPlatformThreads();
                pt2.runMergeSort(list, maxDepth);
                pt2.shutdown();
            }
            default -> {
                System.out.println("Unknown algorithm");
            }
        }
    }
}