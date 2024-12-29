package performancetests;

import performancetests.bankDataGenerator.BankDataGenerator;

public class Main {

    public static void main(String[] args) {

        // Retrieve the number of accounts and transactions from the command line
        int numberOfAccounts = (args.length > 0 && args[0] != null && !args[0].isEmpty())
                ? Integer.parseInt(args[0]) : 10;
        int numberOfTransactions = (args.length > 1 && args[1] != null && !args[1].isEmpty())
                ? Integer.parseInt(args[1]) : 100;

        System.out.println("Data Generator:Bank - Number of accounts: " + numberOfAccounts
                + ", Number of transactions: " + numberOfTransactions);

        // Generate the account file
        BankDataGenerator.generateAccountFile(numberOfAccounts);
        // Generate the transaction file
        BankDataGenerator.generateTransactionFile(numberOfTransactions, numberOfAccounts);

        System.out.println("Data Generator:Bank - done ");
    }

}