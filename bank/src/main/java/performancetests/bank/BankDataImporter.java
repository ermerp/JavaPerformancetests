package performancetests.bank;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BankDataImporter {

    public static List<BankAccount> importAccounts(String accountsFilename) {
        List<BankAccount> accounts = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(accountsFilename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String accountId = parts[0];
                double balance = Double.parseDouble(parts[1].replace(",", "."));
                accounts.add(new BankAccount(accountId, balance));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return accounts;
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