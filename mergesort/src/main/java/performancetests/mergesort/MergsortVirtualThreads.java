package performancetests.mergesort;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MergsortVirtualThreads {

    public static void runAllMergeSort(List<int[]> arrays){
        ArrayList<Future<?>> futureList;
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            futureList = new ArrayList<>();
            arrays.forEach(a -> futureList.add(
                    executor.submit(() -> mergeSort(a))));
        }

        futureList.forEach(f -> {
                try {
                    f.get();
                } catch (ExecutionException | InterruptedException e) {
                    System.out.println("Fail1");
                }
            });

    }

    public static void mergeSort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        // Break the array in two halves
        int mid = array.length / 2;
        int[] leftArray = new int[mid];
        int[] rightArray = new int[array.length - mid];

        System.arraycopy(array, 0, leftArray, 0, mid);

        if (array.length - mid >= 0)
            System.arraycopy(array, mid, rightArray,
                    0, array.length - mid);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var future1 = executor.submit(() -> mergeSort(leftArray));
            var future2 = executor.submit(() -> mergeSort(rightArray));
            future1.get();
            future2.get();
            merge(leftArray, rightArray, array);
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Fail2");
        }
    }

    private static void merge(
            int[] leftArray,
            int[] rightArray,
            int[] array
    ) {
        int i = 0, j = 0, k = 0;

        // Effectively sorts left and right array
        while (i < leftArray.length && j < rightArray.length) {
            if (leftArray[i] <= rightArray[j]) {
                array[k++] = leftArray[i++];
            } else {
                array[k++] = rightArray[j++];
            }
        }
        while (i < leftArray.length) {
            array[k++] = leftArray[i++];
        }
        while (j < rightArray.length) {
            array[k++] = rightArray[j++];
        }
    }
}
