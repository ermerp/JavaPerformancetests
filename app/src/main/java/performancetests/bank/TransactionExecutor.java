package performancetests.bank;

import java.util.List;

public class TransactionExecutor {

    private final PostgRESTBankAccountRepository repository;

    public TransactionExecutor(PostgRESTBankAccountRepository repository) {
        this.repository = repository;
    }

    public void executeTransactionsSingle(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            try {
                repository.book(transaction.from(), transaction.to(), transaction.amount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}