package performancetests;

import performancetests.bank.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {

//        // Set the working directory
//        File workingDir = new File("C:\\Users\\phili\\IdeaProjects\\JavaPerformancetests");
//
//        // Execute docker-compose up
//        executeCommand("docker-compose up -d", workingDir);
//        Thread.sleep(4000);

        int numberOfAccounts = 1000;
        int numberOfTransactions = 10000;

        //PostgRESTBankAccountRepository repository = new PostgRESTBankAccountRepository();
        JDBCBankAccountRepository repository = new JDBCBankAccountRepository();

        repository.deleteAllAccounts();

        createAccountsInDB(repository, numberOfAccounts);

        File transactionFile = BankDataGenerator.generateTransactionFile(numberOfTransactions, numberOfAccounts);
        List<Transaction> transactions = BankDataImporter.importTransactions(transactionFile.getName());

        System.out.println("File imported.");

        long time = 0;
        time = System.currentTimeMillis();

        TransactionExecutor transactionExecutor = new TransactionExecutor(repository);
        transactionExecutor.executeTransactionsVirtual(transactions);

        time = time - System.currentTimeMillis();

        System.out.println("Java:Bank -  Time: " + Duration.ofMillis(time));
    }

    private static void createAccountsInDB(BankAccountRepository repository, int numberOfAccounts) {
        File accountFile = BankDataGenerator.generateAccountFile(numberOfAccounts);
        List<BankAccount> accounts = BankDataImporter.importAccounts(accountFile.getName());

        try {
            for (BankAccount account : accounts) {
                repository.createAccount(account.id(), account.balance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeCommand(String command, File workingDir) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("sh", "-c", command);
        }
        processBuilder.directory(workingDir);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code: " + exitCode);
        }
    }
}