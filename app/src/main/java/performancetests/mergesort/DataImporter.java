package performancetests.mergesort;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataImporter {

    public static int[] importData(String filename) {

        String numberPart = filename.replace("List", "").replace(".txt", "");
        int size = Integer.parseInt(numberPart);

        int[] dataArray = new int[size];
        int index = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (String value : values) {
                    dataArray[index++] = Integer.parseInt(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataArray;
    }
}