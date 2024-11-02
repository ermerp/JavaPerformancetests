package performancetests.mergesort;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {
    public static File generateData(int listLength){
        File data = new File("List"+listLength+".txt");
        try {
            if (data.createNewFile()) {
                writeToFile(data.getName(), listLength);
                System.out.println("File created: " + data.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
        return data;
    }

    private static void writeToFile(String filename, int listLength) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);

        Random random = new Random();
        for(int j=0;j<listLength-1;j++){
            fileWriter.write(random.nextInt(Integer.MAX_VALUE)+",");
        }
        fileWriter.write(random.nextInt(Integer.MAX_VALUE)+"");

        fileWriter.close();
    }
}
