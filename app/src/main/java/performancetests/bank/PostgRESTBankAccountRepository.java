package performancetests.bank;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostgRESTBankAccountRepository implements BankAccountRepository {

    public static final String URL = "http://localhost:3000/rpc/";

    @Override
    public int createAccount(String accountId, double balance) throws Exception {
        URI uri = new URI(URL + "create_account");
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            var jsonData = "{ \"account_id\": \"" + accountId + "\", \"balance\": " + balance + " }";
            //System.out.println("Request JSON: " + jsonData);
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        //System.out.println("Response code: " + responseCode);

        if (responseCode != HttpURLConnection.HTTP_OK) {
            try (InputStream is = connection.getErrorStream()) {
                String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Error response: " + response);
            }
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }

        return responseCode;
    }

    @Override
    public int book(String from, String to, double amount) throws Exception {
        URI uri = new URI(URL+"transfer_balance");
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setDoOutput(true);
        try (OutputStream os = connection.getOutputStream()) {
            var jsonData = " { \"from_id\": \"" + from + "\", \"to_id\": \"" + to + "\", \"amount\": " + amount + "}";
            System.out.println(jsonData);
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        //System.out.println("Response code: " + responseCode);

        if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
            try (InputStream is = connection.getErrorStream()) {
                String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Error response: " + response);
            }
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }

        return responseCode;
    }
}