package performancetests;

import performancetests.bank.*;

import java.io.File;
import java.time.Duration;
import java.util.List;


public class Main {

    public static void main(String[] args) throws Exception {

        // Retrieve the environment variable
        String interfaceType = System.getenv("INTERFACE_TYPE") != null ? System.getenv("INTERFACE_TYPE") : "JDBC";
        String algorithm = System.getenv("ALGORITHM") != null ? System.getenv("ALGORITHM") : "VIRTUAL";

        String numberOfAccountsStr = System.getenv("NUMBER_Of_ACCOUNTS");
        int numberOfAccounts = numberOfAccountsStr != null ? Integer.parseInt(numberOfAccountsStr) : 10;

        String numberOfTransactionsStr = System.getenv("NUMBER_OF_TRANSACTIONS");
        int numberOfTransactions = numberOfTransactionsStr != null ? Integer.parseInt(numberOfTransactionsStr) : 100;

        System.out.println("Java:Bank - Interface: " + interfaceType
                + ", Algorithm: " + algorithm
                + ", Number of accounts: " + numberOfAccounts
                + ", Number of transactions: " + numberOfTransactions);

        BankAccountRepository repository = switch (interfaceType) {
            case "JDBC" -> new JDBCBankAccountRepository();
            case "REST" -> new PostgRESTBankAccountRepository();
            default -> throw new IllegalArgumentException("Unknown interface type: " + interfaceType);
        };

        // Clean up the database
        repository.deleteAllAccounts();

        // Create accounts in the database
        createAccountsInDB(repository, numberOfAccounts);

        // Generate transactions
        File transactionFile = BankDataGenerator.generateTransactionFile(numberOfTransactions, numberOfAccounts);
        List<Transaction> transactions = BankDataImporter.importTransactions(transactionFile.getName());

        System.out.println("File imported.");

        // Execute transactions
        long time = 0;
        time = System.currentTimeMillis();

        TransactionExecutor transactionExecutor = new TransactionExecutor(repository);
        switch (algorithm) {
            case "VIRTUAL" -> transactionExecutor.executeTransactionsVirtual(transactions);
            case "PLATFORM" -> transactionExecutor.executeTransactionsPlatform(transactions);
            case "SINGLE" -> transactionExecutor.executeTransactionsSingle(transactions);
            default -> throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }

        time = time - System.currentTimeMillis();

        System.out.println("Java:Bank - Time: " + Duration.ofMillis(time));
    }

    private static void createAccountsInDB(BankAccountRepository repository, int numberOfAccounts) {
        // Generate accounts
        File accountFile = BankDataGenerator.generateAccountFile(numberOfAccounts);
        List<BankAccount> accounts = BankDataImporter.importAccounts(accountFile.getName());

        // Create accounts in the database
        try {
            for (BankAccount account : accounts) {
                repository.createAccount(account.id(), account.balance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}