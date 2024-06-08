package performancetests.mergesort;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MergesortVirtualThreads {

    public static List<int[]> runAllMergeSort(String fileName){

        ArrayList<Future<?>> futureList = new ArrayList<>();;
        List<int[]> sortedLists = new ArrayList<>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String line;
                reader.readLine();
                reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] splitLine = line.split(",");
                    int[] intArray = new int[splitLine.length];
                    for (int i = 0; i < splitLine.length; i++) {
                        intArray[i] = Integer.parseInt(splitLine[i]);
                    }
                    futureList.add(executor.submit(() -> mergeSort(intArray)));
                }
                reader.close();
                sortedLists = futureList.stream().map(future -> {
                    try {
                        return (int[]) future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sortedLists;
    }

    public static int[] mergeSort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }

        // Break the array in two halves
        int mid = array.length / 2;
        int[] leftArray = new int[mid];
        int[] rightArray = new int[array.length - mid];

        System.arraycopy(array, 0, leftArray, 0, mid);

        if (array.length - mid >= 0)
            System.arraycopy(array, mid, rightArray,
                    0, array.length - mid);

//        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
//            var future1 = executor.submit(() -> mergeSort(leftArray));
//            var future2 = executor.submit(() -> mergeSort(rightArray));
//
//            return merge(future1.get(), future2.get());
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        mergeSort(leftArray);
        mergeSort(rightArray);
        return merge(leftArray, rightArray);
    }

    private static int[] merge(
            int[] leftArray,
            int[] rightArray
    ) {
        int[] result = new int[leftArray.length + rightArray.length];
        int i = 0, j = 0, k = 0;

        // Effectively sorts left and right array
        while (i < leftArray.length && j < rightArray.length) {
            if (leftArray[i] <= rightArray[j]) {
                result[k++] = leftArray[i++];
            } else {
                result[k++] = rightArray[j++];
            }
        }

        while (i < leftArray.length) {
            result[k++] = leftArray[i++];
        }
        while (j < rightArray.length) {
            result[k++] = rightArray[j++];
        }

        return result;
    }
}
