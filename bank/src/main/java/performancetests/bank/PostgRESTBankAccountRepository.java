package performancetests.bank;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgRESTBankAccountRepository implements BankAccountRepository {

    private static final Logger LOGGER = Logger.getLogger(PostgRESTBankAccountRepository.class.getName());
    public static final String URL = "http://localhost:3000/rpc/";
    private static final int MAX_RETRIES = 100;
    private static final long RETRY_DELAY_MS = 1000;

    @Override
    public int createAccount(String accountId, double balance) throws Exception {
        HttpURLConnection connection = setupConnection("create_account");
        sendRequest(connection, "{ \"account_id\": \"" + accountId + "\", \"balance\": " + balance + " }");
        try {
            boolean success = handleResponse(connection);
            if (success) {
                LOGGER.info(String.format("Account created successfully: %s, initial balance: %f", accountId, balance));
                return HttpURLConnection.HTTP_OK;
            } else {
                LOGGER.warning(String.format("Failed to create account: %s, initial balance: %f", accountId, balance));
                return HttpURLConnection.HTTP_INTERNAL_ERROR;
            }
        } catch (Exception e) {
            LOGGER.severe(String.format("Error creating account: %s, initial balance: %f. Error: %s",
                    accountId, balance, e.getMessage()));
            throw e;
        }
    }

    //sendRequest(connection, "{ \"from_id\": \"" + from + "\", \"to_id\": \"" + to + "\", \"amount\": " + amount + " }");
    @Override
    public int book(String from, String to, double amount) throws Exception {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                HttpURLConnection connection = setupConnection("transfer_balance");
                sendRequest(connection, "{ \"from_id\": \"" + from + "\", \"to_id\": \"" + to + "\", \"amount\": " + amount + " }");
                boolean success = handleResponse(connection);
                if (success) {
                    LOGGER.info(String.format("Transfer successful: %s -> %s, amount: %f", from, to, amount));
                    return HttpURLConnection.HTTP_OK;
                } else {
                    LOGGER.warning(String.format("Transfer failed: %s -> %s, amount: %f", from, to, amount));
                    return HttpURLConnection.HTTP_INTERNAL_ERROR;
                }
            } catch (DeadlockException e) {
                attempt++;
                LOGGER.warning(String.format("Deadlock detected. Transfer attempt %d failed: %s -> %s, amount: %f. Retrying...",
                        attempt, from, to, amount));
                if (attempt >= MAX_RETRIES) {
                    LOGGER.severe(String.format("Transfer failed after %d attempts due to persistent deadlocks: %s -> %s, amount: %f",
                            MAX_RETRIES, from, to, amount));
                    return HttpURLConnection.HTTP_INTERNAL_ERROR;
                }
                Thread.sleep(calculateRetryDelay(attempt));
            } catch (Exception e) {
                LOGGER.severe(String.format("Transfer failed due to non-deadlock error: %s -> %s, amount: %f. Error: %s",
                        from, to, amount, e.getMessage()));
                throw e;
            }
        }
        return HttpURLConnection.HTTP_INTERNAL_ERROR;
    }


    private long calculateRetryDelay(int attempt) {
        return RETRY_DELAY_MS * attempt + (long) (Math.random() * 1000);
    }

    private HttpURLConnection setupConnection(String endpoint) throws Exception {
        URI uri = new URI(URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        return connection;
    }

    private void sendRequest(HttpURLConnection connection, String jsonData) throws Exception {
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send request", e);
            throw e;
        }
    }

    private boolean handleResponse(HttpURLConnection connection) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream is = connection.getInputStream()) {
                String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                return Boolean.parseBoolean(response.trim());
            }
        } else {
            LOGGER.log(Level.SEVERE, "Failed : HTTP error code : " + responseCode);
            try (InputStream errorStream = connection.getErrorStream()) {
                if (errorStream != null) {
                    String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    LOGGER.log(Level.SEVERE, "Error response: " + errorResponse);
                    if (isDeadlockError(errorResponse)) {
                        throw new DeadlockException("Deadlock detected: " + errorResponse);
                    }
                }
            }
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }
    }

    private boolean isDeadlockError(String errorResponse) {
        return errorResponse.contains("deadlock detected") || errorResponse.contains("40P01");
    }

}