package performancetests.bank;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class PostgRESTBankAccountRepository implements BankAccountRepository {

    public static final String URL = "http://localhost:3000/rpc/";

    @Override
    public int createAccount(String accountId, double balance) throws Exception {
        HttpURLConnection connection = setupConnection("create_account");
        sendRequest(connection, "{ \"account_id\": \"" + accountId + "\", \"balance\": " + balance + " }");
        return handleResponse(connection, HttpURLConnection.HTTP_OK);
    }

    @Override
    public int book(String from, String to, double amount) throws Exception {
        HttpURLConnection connection = setupConnection("transfer_balance");
        sendRequest(connection, "{ \"from_id\": \"" + from + "\", \"to_id\": \"" + to + "\", \"amount\": " + amount + " }");
        return handleResponse(connection, HttpURLConnection.HTTP_NO_CONTENT);
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

    private int handleResponse(HttpURLConnection connection, int expectedResponseCode) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode != expectedResponseCode) {
            try (InputStream is = connection.getErrorStream()) {
                String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Error response: " + response);
            }
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }
        return responseCode;
    }
}