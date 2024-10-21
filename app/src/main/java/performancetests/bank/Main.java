package performancetests.bank;

public class Main {

    public static void main(String[] args) {
        PostgRESTBankAccountRepository repository = new PostgRESTBankAccountRepository();
        try {
            for (int i = 1; i <= 5; i++) {

                repository.createAccount("000000000"+i,100*i);
            }
            repository.book("0000000001", "0000000002", 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}