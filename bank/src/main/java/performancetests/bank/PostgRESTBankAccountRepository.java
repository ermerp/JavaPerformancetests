package performancetests.bank;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class PostgRESTBankAccountRepository implements BankAccountRepository {

    public static final String URL = "http://localhost:3000/rpc/";
    private static final int MAX_RETRIES = 100;
    private static final long RETRY_DELAY_MS = 1000;

    @Override
    public int createAccount(String accountId, double balance) throws Exception {
        HttpURLConnection connection = setupConnection("create_account");
        sendRequest(connection, "{ \"account_id\": \"" + accountId + "\", \"balance\": " + balance + " }");
        return connection.getResponseCode();
    }

    @Override
    public int deleteAllAccounts() throws Exception {
        HttpURLConnection connection = setupConnection("delete_all_accounts");
        sendRequest(connection, "{}");
        return connection.getResponseCode();
    }

    @Override
    public int book(String from, String to, double amount) throws Exception {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            HttpURLConnection connection = setupConnection("transfer_balance");
            sendRequest(connection, "{ \"from_id\": \"" + from + "\", \"to_id\": \"" + to + "\", \"amount\": " + amount + " }");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //System.out.println(String.format("Transfer successful: %s -> %s, amount: %f", from, to, amount));
                return responseCode;
            } else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                attempt++;
                long sleepTime = calculateRetryDelay(attempt);
                System.out.println(String.format(String.format("Deadlock detected. Transfer attempt %d failed: %s -> %s, amount: %f. Retrying after %d ms...",
                        attempt, from, to, amount, sleepTime)));
                Thread.sleep(sleepTime);
            } else if (responseCode == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                attempt++;
                long sleepTime = calculateRetryDelay(attempt);
                System.out.println(String.format(String.format("Gateway Timeout. Transfer attempt %d failed: %s -> %s, amount: %f. Retrying after %d ms...",
                        attempt, from, to, amount, sleepTime)));
                Thread.sleep(sleepTime);
            } else {
                long sleepTime = calculateRetryDelay(attempt);
                System.out.println(String.format("Error!!! Transfer failed - Code: %d Retrying after %d ms ...", responseCode, sleepTime));
                Thread.sleep(sleepTime);
            }
        }
        System.out.println(String.format("Error!!! Transfer failed after %d attempts due to persistent deadlocks: %s -> %s, amount: %f",
                MAX_RETRIES, from, to, amount));
        return HttpURLConnection.HTTP_INTERNAL_ERROR;
    }


    private long calculateRetryDelay(int attempt) {
        return RETRY_DELAY_MS * attempt + (long) (Math.random() * 5000);
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
        }
    }
}