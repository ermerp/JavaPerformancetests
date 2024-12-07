package performancetests.mergesort;

public class Mergesort {

    public static void runMergeSort(int[] array) {
        int[] tempArray = new int[array.length]; // Temporary array for the merge step
        mergeSort(array, tempArray, 0, array.length - 1);
    }

    private static void mergeSort(int[] array, int[] tempArray, int left, int right) {
        if (left >= right) return;

        int mid = (left + right) / 2;
        mergeSort(array, tempArray, left, mid);
        mergeSort(array, tempArray, mid + 1, right);
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