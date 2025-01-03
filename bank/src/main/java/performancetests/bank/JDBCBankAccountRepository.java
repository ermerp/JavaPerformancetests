package performancetests.bank;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCBankAccountRepository implements BankAccountRepository {

    private static final String DB_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    private static final String URL = "jdbc:postgresql://" + DB_HOST + ":5432/mydatabase";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);

        // Set the maximum pool size
        String maxConnectionsStr = System.getenv("MAX_CONNECTIONS");
        int maxConnections = maxConnectionsStr != null ? Integer.parseInt(maxConnectionsStr) : 80;
        config.setMaximumPoolSize(maxConnections);

        // Set the connection timeout
        config.setConnectionTimeout(3600000); // 1 hour

        dataSource = new HikariDataSource(config);
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // creates a new account in the database
    @Override
    public int createAccount(String accountId, double balance) throws Exception {
        String sql = "INSERT INTO account (id, balance) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountId);
            pstmt.setDouble(2, balance);
            return pstmt.executeUpdate();
        }
    }

    // deletes all accounts from the database
    @Override
    public int deleteAllAccounts() throws Exception {
        String sql = "DELETE FROM account";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            return pstmt.executeUpdate();
        }
    }

    // transfers the balance from one account to another
    @Override
    public int book(String from, String to, double amount, double delay) throws Exception {
        String withdrawSql = "UPDATE account SET balance = balance - ? WHERE id = ?";
        String depositSql = "UPDATE account SET balance = balance + ? WHERE id = ?";
        String lockSql = "SELECT 1 FROM account WHERE id = ? FOR UPDATE";
        String sleepSql = "SELECT pg_sleep(?)";
        int maxRetries = 100;
        long retryDelayMs = 1000;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);

                // Lock rows in the correct order
                try (PreparedStatement lockStmt1 = conn.prepareStatement(lockSql);
                     PreparedStatement lockStmt2 = conn.prepareStatement(lockSql)) {
                    if (from.compareTo(to) < 0) {
                        lockStmt1.setString(1, from);
                        lockStmt2.setString(1, to);
                    } else {
                        lockStmt1.setString(1, to);
                        lockStmt2.setString(1, from);
                    }
                    lockStmt1.executeQuery();
                    lockStmt2.executeQuery();
                }

                // Perform the delay if specified
                if (delay > 0) {
                    try (PreparedStatement sleepStmt = conn.prepareStatement(sleepSql)) {
                        sleepStmt.setDouble(1, delay);
                        sleepStmt.executeQuery();
                    }
                }

                // Perform the balance update
                try (PreparedStatement withdrawStmt = conn.prepareStatement(withdrawSql);
                     PreparedStatement depositStmt = conn.prepareStatement(depositSql)) {
                    withdrawStmt.setDouble(1, amount);
                    withdrawStmt.setString(2, from);
                    withdrawStmt.executeUpdate();

                    depositStmt.setDouble(1, amount);
                    depositStmt.setString(2, to);
                    depositStmt.executeUpdate();

                    conn.commit();
                    return 1;
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                if ("40001".equals(e.getSQLState())) { // SQLState for deadlock
                    long sleepTime = retryDelayMs * (attempt + 1) + (long) (Math.random() * 500);
                    System.out.printf("Deadlock detected. Transfer attempt %d failed: %s -> %s, amount: %f. Retrying after %d ms...%n",
                            attempt + 1, from, to, amount, sleepTime);
                    Thread.sleep(sleepTime);
                } else {
                    throw e;
                }
            }
        }
        System.out.printf("Error!!! Transfer failed after %d attempts due to persistent deadlocks: %s -> %s, amount: %f%n",
                maxRetries, from, to, amount);
        return 0;
    }
}