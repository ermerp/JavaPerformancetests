package performancetests.bank;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class TransactionExecutor {

    private final BankAccountRepository repository;

    public TransactionExecutor(BankAccountRepository repository) {
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

    public void executeTransactionsVirtual(List<Transaction> transactions) {
        Semaphore semaphore = (repository instanceof PostgRESTBankAccountRepository)
                ? new Semaphore(System.getenv("MAX_CONNECTIONS") != null ? Integer.parseInt(System.getenv("MAX_CONNECTIONS")) : 80)
                : null;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (Transaction transaction : transactions) {
                //startet neuen Virtual Thread
                executor.submit(() -> {
                    try {
                        if (semaphore != null) semaphore.acquire();
                        repository.book(transaction.from(), transaction.to(), transaction.amount());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (semaphore != null) semaphore.release();
                    }
                });
            }
        }
    }

    public void executeTransactionsPlatform(List<Transaction> transactions) {

        Semaphore semaphore = (repository instanceof PostgRESTBankAccountRepository)
                ? new Semaphore(System.getenv("MAX_CONNECTIONS") != null ? Integer.parseInt(System.getenv("MAX_CONNECTIONS")) : 80)
                : null;

        try (ExecutorService executor = Executors.newCachedThreadPool()) {

            for (Transaction transaction : transactions) {
                //startet neuen Virtual Thread
                executor.submit(() -> {
                    try {
                        if (semaphore != null) semaphore.acquire();
                        repository.book(transaction.from(), transaction.to(), transaction.amount());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (semaphore != null) semaphore.release();
                    }
                });
            }
        }
    }
}