package performancetests.mergesort;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataImporter {

    public static List<int[]> importData(String filename){
        try {
            File data = new File(filename);
            Scanner scanner = new Scanner(data);
            ArrayList<int[]> arrayList= new ArrayList<>();
            String line;
            int amountOfLists = 0;
            int lengthOfLists = 0;

            if(scanner.hasNextLine()){
                line = scanner.nextLine();
                amountOfLists = Integer.parseInt(line);
            }
            if(scanner.hasNextLine()){
                line = scanner.nextLine();
                lengthOfLists = Integer.parseInt(line);
            }

            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                String[] arrayString = null;
                arrayString = line.split(",");

                int[] arrayInt = new int[arrayString.length];
                for (int i = 0; i < arrayString.length; i++) {
                    arrayInt[i] = Integer.parseInt(arrayString[i]);
                }
                arrayList.add(arrayInt);
            }

            scanner.close();
            return arrayList;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            return null;
        }
    }
}
