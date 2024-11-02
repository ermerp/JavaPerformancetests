package performancetests.bank;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class BankDataGenerator {
    public static File generateAccountFile(int numberOfAccounts){
        File data = new File("BankAccounts"+numberOfAccounts+".txt");
        try {
            if (data.createNewFile()) {
                writeAccountsToFile(data.getName(), numberOfAccounts);
                System.out.println("Bank File created: " + data.getName());
            } else {
                System.out.println("Bank File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }
        return data;
    }

    private static void writeAccountsToFile(String filename, int numberOfAccounts) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);

        for(int i=1;i<=numberOfAccounts-1;i++){
            fileWriter.write(generateAccount(i) + System.lineSeparator());
        }
        fileWriter.write(generateAccount(numberOfAccounts));

        fileWriter.close();
    }

    private static String generateAccount(int i){
        StringBuilder accountId = new StringBuilder();
        //Bank ID
        accountId.append("0".repeat(9 - (int) Math.log10(i)));
        accountId.append(i);

        //Balance
        Random random = new Random();
        double min = -1000.0;
        double max = 1000.0;
        double randomBalance = min + (max - min) * random.nextDouble();
        accountId.append(", ").append(String.format("%.2f", randomBalance));

        return accountId.toString();
    }

    public static File generateTransactionFile(int numberOfTransactions, int numberOfAccounts){
        File data = new File("BankTransactions"+numberOfTransactions+"-"+numberOfAccounts+".txt");
        try {
            if (data.createNewFile()) {
                writeTransactionsToFile(data.getName(), numberOfTransactions, numberOfAccounts);
                System.out.println("Transaction File created: " + data.getName());
            } else {
                System.out.println("Transaction File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
        }

        return data;
    }

    private static void writeTransactionsToFile(String filename, int numberOfTransactions, int numberOfAccounts) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);

        for(int i=1;i<=numberOfTransactions-1;i++){
            fileWriter.write(generateTransaction(i, numberOfAccounts) + System.lineSeparator());
        }
        fileWriter.write(generateTransaction(numberOfTransactions, numberOfAccounts));

        fileWriter.close();
    }

    private static String generateTransaction(int i, int numberOfAccounts){
        StringBuilder transaction = new StringBuilder();
        //From Account
        Random random = new Random();
        int from = random.nextInt(numberOfAccounts) + 1;
        transaction.append("0".repeat(9 - (int) Math.log10(from))).append(from);

        //To Account
        int to = random.nextInt(numberOfAccounts) + 1;
        while(to == from){
            to = random.nextInt(numberOfAccounts) + 1;
        }
        transaction.append(", ").append("0".repeat(9 - (int) Math.log10(to))).append(to);

        //Amount
        double min = 0.0;
        double max = 1000.0;
        double randomAmount = min + (max - min) * random.nextDouble();
        transaction.append(", ").append(String.format("%.2f", randomAmount));

        return transaction.toString();
    }
}
