package performancetests.mergesort;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataImporter {

    public static List<int[]> importData(String fileName){

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<int[]> arrays = new ArrayList<>();
        for (int i = 2; i < lines.size(); i++) {
            String[] splitLine = lines.get(i).split(",");
            int[] intArray = new int[splitLine.length];
            for (int j = 0; j < splitLine.length; j++) {
                intArray[j] = Integer.parseInt(splitLine[j]);
            }
            arrays.add(intArray);
        }

        return arrays;
    }
}
