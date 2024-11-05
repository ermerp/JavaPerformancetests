package performancetests.bank;

public interface BankAccountRepository {
    int createAccount(String accountId, double balance) throws Exception;
    int deleteAllAccounts() throws Exception;
    int book(String from, String to, double amount) throws Exception;
}