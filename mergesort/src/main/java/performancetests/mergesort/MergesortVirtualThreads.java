package performancetests.mergesort;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MergesortVirtualThreads {

    private final ExecutorService executor ;

    public MergesortVirtualThreads() {
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void runMergeSort(int[] array, int maxDepth) {
        int[] tempArray = new int[array.length]; // Temporary array for the merge step
        mergeSort(array, tempArray, 0, array.length - 1,0, maxDepth);
    }

    private void mergeSort(int[] array, int[] tempArray, int left, int right, int currentDepth, int maxDepth){
        if (left >= right) return;

        int mid = (left + right) / 2;

        if(currentDepth < maxDepth){
            Future<?> leftFuture = executor.submit(() ->
                    mergeSort(array, tempArray, left, mid, currentDepth + 1, maxDepth));
            Future<?> rightFuture = executor.submit(() ->
                    mergeSort(array, tempArray, mid + 1, right, currentDepth + 1, maxDepth));
            try {
                leftFuture.get();
                rightFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else {
            mergeSort(array, tempArray, left, mid, currentDepth + 1, maxDepth);
            mergeSort(array, tempArray, mid + 1, right, currentDepth + 1, maxDepth);
        }

        merge(array, tempArray, left, mid, right);
    }

    private static void merge(int[] array, int[] tempArray, int left, int mid, int right) {
        System.arraycopy(array, left, tempArray, left, right - left + 1);

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (tempArray[i] <= tempArray[j]) {
                array[k++] = tempArray[i++];
            } else {
                array[k++] = tempArray[j++];
            }
        }

        while (i <= mid) {
            array[k++] = tempArray[i++];
        }
    }
}
