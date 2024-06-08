package performancetests.mergesort;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataGenerator {
    public static File generateData(int amountOfLists, int lengthOfLists){
        File data = new File("Data"+amountOfLists+"x"+lengthOfLists+".txt");
        try {
            if (data.createNewFile()) {
                wirteToFile(data.getName(), amountOfLists, lengthOfLists);
                System.out.println("File created: " + data.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
        return data;
    }

    private static void wirteToFile(String filename, int amountOfLists, int lengthOfLists) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        fileWriter.write(amountOfLists + System.lineSeparator());
        fileWriter.write(lengthOfLists + System.lineSeparator());

        Random random = new Random();
        for(int i=0;i<amountOfLists;i++){
            for(int j=0;j<lengthOfLists-1;j++){
                fileWriter.write(random.nextInt(lengthOfLists)+",");
            }
            fileWriter.write(random.nextInt(lengthOfLists)+"");
            if(i<amountOfLists-1) {
                fileWriter.write(System.lineSeparator());
            }
        }
        fileWriter.close();
    }
}
