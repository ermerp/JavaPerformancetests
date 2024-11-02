package performancetests.mergesort;

public class Mergesort {

    public static void mergeSort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }

        // Break the array in two halves
        int mid = array.length / 2;
        int[] leftArray = new int[mid];
        int[] rightArray = new int[array.length - mid];

        System.arraycopy(array, 0, leftArray, 0, mid);
        System.arraycopy(array, mid, rightArray, 0, array.length - mid);

        mergeSort(leftArray);
        mergeSort(rightArray);
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