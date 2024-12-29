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
                repository.book(transaction.from(), transaction.to(), transaction.amount(), 0.0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void executeTransactionsVirtual(List<Transaction> transactions, double delay) {
        int maxConnections = System.getenv("MAX_CONNECTIONS") != null ? Integer.parseInt(System.getenv("MAX_CONNECTIONS")) : 80;
        Semaphore semaphore = (repository instanceof PostgRESTBankAccountRepository)
                ? new Semaphore(maxConnections)
                : new Semaphore(maxConnections*2);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (Transaction transaction : transactions) {
                //start a new virtual thread
                executor.submit(() -> {
                    try {
                        semaphore.acquire();
                        repository.book(transaction.from(), transaction.to(), transaction.amount(), delay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        semaphore.release();
                    }
                });
            }
        }
    }

    public void executeTransactionsPlatform(List<Transaction> transactions, double delay) {

        int maxConnections = System.getenv("MAX_CONNECTIONS") != null ? Integer.parseInt(System.getenv("MAX_CONNECTIONS")) : 80;
        Semaphore semaphore = (repository instanceof PostgRESTBankAccountRepository)
                ? new Semaphore(maxConnections)
                : null;

        try (ExecutorService executor = Executors.newFixedThreadPool(maxConnections*2)) {

            for (Transaction transaction : transactions) {
                // start a new platform thread
                executor.submit(() -> {
                    try {
                        if (semaphore != null) semaphore.acquire();
                        repository.book(transaction.from(), transaction.to(), transaction.amount(), delay);
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