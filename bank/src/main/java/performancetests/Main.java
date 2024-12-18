package performancetests;

import performancetests.bank.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws Exception {

        // Retrieve the environment variables
        String interfaceType = System.getenv("INTERFACE_TYPE") != null ? System.getenv("INTERFACE_TYPE") : "JDBC";
        String algorithm = System.getenv("ALGORITHM") != null ? System.getenv("ALGORITHM") : "VIRTUAL";
        String maxConnectionsStr = System.getenv("MAX_CONNECTIONS");
        int maxConnections = maxConnectionsStr != null ? Integer.parseInt(maxConnectionsStr) : 80;

        String numberOfAccountsStr = System.getenv("NUMBER_OF_ACCOUNTS");
        int numberOfAccounts = numberOfAccountsStr != null ? Integer.parseInt(numberOfAccountsStr) : 10;

        String numberOfTransactionsStr = System.getenv("NUMBER_OF_TRANSACTIONS");
        int numberOfTransactions = numberOfTransactionsStr != null ? Integer.parseInt(numberOfTransactionsStr) : 100;

        String delayTransactionStr = System.getenv("DELAY_TRANSACTION");
        double delayTransaction = delayTransactionStr != null ? Double.parseDouble(delayTransactionStr) : 0.0;

        System.out.println("Java:Bank - Interface: " + interfaceType
                + ", Algorithm: " + algorithm
                + ", Max connections: " + maxConnections
                + ", Number of accounts: " + numberOfAccounts
                + ", Number of transactions: " + numberOfTransactions
                + ", Delay transaction: " + delayTransaction);

        BankAccountRepository repository = switch (interfaceType) {
            case "JDBC" -> new JDBCBankAccountRepository();
            case "REST" -> new PostgRESTBankAccountRepository();
            default -> throw new IllegalArgumentException("Unknown interface type: " + interfaceType);
        };

        // Clean up the database
        repository.deleteAllAccounts();

        // Create accounts in the database
        importAccounts(repository, numberOfAccounts);

        // import transactions
        List<Transaction> transactions = importTransactions("/bankData/BankTransactions" + numberOfTransactions + "-" + numberOfAccounts + ".txt");

        System.out.println("File imported.");

        // Execute transactions
        long time = 0;
        time = System.currentTimeMillis();

        TransactionExecutor transactionExecutor = new TransactionExecutor(repository);
        switch (algorithm) {
            case "VIRTUAL" -> transactionExecutor.executeTransactionsVirtual(transactions, delayTransaction);
            case "PLATFORM" -> transactionExecutor.executeTransactionsPlatform(transactions, delayTransaction);
            case "SINGLE" -> transactionExecutor.executeTransactionsSingle(transactions);
            default -> throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }

        time = time - System.currentTimeMillis();

        System.out.println("Java:Bank - Time: " + Duration.ofMillis(time));
    }

    public static void importAccounts(BankAccountRepository repository, int numberOfAccounts) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("/bankData/BankAccounts" + numberOfAccounts + ".txt"));
        for (String line : lines) {
            String[] parts = line.split(", ");
            String accountId = parts[0].trim();
            double balance = Double.parseDouble(parts[1].trim().replace(",", "."));
            repository.createAccount(accountId, balance);
        }
    }

    public static List<Transaction> importTransactions(String transactionsFilename) {
        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(transactionsFilename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String fromAccountId = parts[0];
                String toAccountId = parts[1];
                double amount = Double.parseDouble(parts[2].replace(",", "."));
                transactions.add(new Transaction(fromAccountId, toAccountId, amount));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactions;
    }
}