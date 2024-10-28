package performancetests.bank;

public class DeadlockException extends Exception {
    public DeadlockException(String message) {
        super(message);
    }

    public DeadlockException(String message, Throwable cause) {
        super(message, cause);
    }
}