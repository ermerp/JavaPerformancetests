package performancetests;

import performancetests.mergesort.DataGenerater;
import performancetests.mergesort.DataImporter;
import performancetests.mergesort.MergsortVirtualThreads;

import java.io.File;
import java.time.Duration;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        File data = DataGenerater.generateData(200, 200);
        List<int[]> list = DataImporter.importData(data.getName());

        long time = System.currentTimeMillis();

        MergsortVirtualThreads.runAllMergeSort(list);
        //list.forEach(a -> System.out.println(Arrays.toString(a)));
        time = time - System.currentTimeMillis();
        System.out.println("Zeit: " + Duration.ofMillis(time));
    }
}