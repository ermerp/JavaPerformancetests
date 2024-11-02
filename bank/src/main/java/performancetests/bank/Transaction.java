package performancetests.bank;

public record Transaction(
        String from,
        String to,
        double amount
) {}
