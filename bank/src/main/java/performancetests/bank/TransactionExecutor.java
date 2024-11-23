package performancetests.bank;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class TransactionExecutor {

    private final BankAccountRepository repository;
    private final int maxConnections = 10000; // Set the maximum number of connections

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
        Semaphore semaphore = new Semaphore(maxConnections);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            for (Transaction transaction : transactions) {
                //startet neuen Virtual Thread
                executor.submit(() -> {
                    try {
                        semaphore.acquire();
                        try {
                            repository.book(transaction.from(), transaction.to(), transaction.amount());
                        } finally {
                            semaphore.release();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    public void executeTransactionsPlatform(List<Transaction> transactions) {
        try (ExecutorService executor = Executors.newFixedThreadPool(maxConnections)) {
            for (Transaction transaction : transactions) {
                // Start a new platform thread
                executor.submit(() -> {
                    try {
                        repository.book(transaction.from(), transaction.to(), transaction.amount());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}