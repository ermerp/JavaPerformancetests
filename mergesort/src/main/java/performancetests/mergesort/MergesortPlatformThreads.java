package performancetests.mergesort;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MergesortPlatformThreads {

    ExecutorService cachedPool ;
    int chunkSize;

    public MergesortPlatformThreads(int chunkSize) {
        this.cachedPool = Executors.newCachedThreadPool();
        this.chunkSize = chunkSize;
    }

    public void shutdown() {
        cachedPool.shutdown();
    }

    public void mergeSort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        // Break the array in two halves
        int mid = array.length / 2;
        int[] leftArray = new int[mid];
        int[] rightArray = new int[array.length - mid];

        System.arraycopy(array, 0, leftArray, 0, mid);
        System.arraycopy(array, mid, rightArray, 0, array.length - mid);

        if(mid >= chunkSize) {
            Future<?> leftFuture = cachedPool.submit(() -> mergeSort(leftArray));
            Future<?> rightFuture = cachedPool.submit(() -> mergeSort(rightArray));

            try {
                leftFuture.get();
                rightFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else {
            mergeSort(leftArray);
            mergeSort(rightArray);
        }

        merge(leftArray, rightArray, array);
    }

    private static void merge(int[] leftArray, int[] rightArray, int[] result) {
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
    }
}
