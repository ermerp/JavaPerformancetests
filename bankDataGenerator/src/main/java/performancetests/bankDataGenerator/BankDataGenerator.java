package performancetests.bankDataGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class BankDataGenerator {
    public static void generateAccountFile(int numberOfAccounts){
        File directory = new File("bankData");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File accountFile = new File(directory, "BankAccounts"+numberOfAccounts+".txt");
        try {
            if (accountFile.createNewFile()) {
                writeAccountsToFile(accountFile, numberOfAccounts);
                System.out.println("Account File created: " + accountFile.getName());
            } else {
                System.out.println("Account File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred:" + e);
        }
    }

    private static void writeAccountsToFile(File accountFile, int numberOfAccounts) throws IOException {
        FileWriter fileWriter = new FileWriter(accountFile);

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

    public static void generateTransactionFile(int numberOfTransactions, int numberOfAccounts){
        File directory = new File("bankData");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File transactionFile = new File(directory,"BankTransactions"+numberOfTransactions+"-"+numberOfAccounts+".txt");
        try {
            if (transactionFile.createNewFile()) {
                writeTransactionsToFile(transactionFile, numberOfTransactions, numberOfAccounts);
                System.out.println("Transaction File created: " + transactionFile.getName());
            } else {
                System.out.println("Transaction File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred:" + e);
        }
    }

    private static void writeTransactionsToFile(File transactionFile, int numberOfTransactions, int numberOfAccounts) throws IOException {
        FileWriter fileWriter = new FileWriter(transactionFile);

        for(int i=1;i<=numberOfTransactions-1;i++){
            fileWriter.write(generateTransaction(numberOfAccounts) + System.lineSeparator());
        }
        fileWriter.write(generateTransaction(numberOfAccounts));

        fileWriter.close();
    }

    private static String generateTransaction(int numberOfAccounts){
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
