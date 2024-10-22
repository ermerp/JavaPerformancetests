package performancetests.bank;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        int numberOfAccounts = 1000;
        int numberOfTransactions = 10000;

        PostgRESTBankAccountRepository repository = new PostgRESTBankAccountRepository();

        createAccountsInDB(repository, numberOfAccounts);

        File transactionFile = BankDataGenerator.generateTransactionFile(numberOfTransactions, numberOfAccounts);
        List<Transaction> transactions = BankDataImporter.importTransactions(transactionFile.getName());

        System.out.println("File imported.");

        TransactionExecutor transactionExecutor = new TransactionExecutor(repository);
        transactionExecutor.executeTransactionsSingle(transactions);
    }

    private static void createAccountsInDB(PostgRESTBankAccountRepository repository, int numberOfAccounts) {
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
}