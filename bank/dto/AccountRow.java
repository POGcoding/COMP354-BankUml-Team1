package bank.dto;

public class AccountRow {
    private final String accountId;
    private final String maskedAccountNumber;
    private final AccountType accountType;
    private final String customerName;
    private final double balance;

    public AccountRow(String accountId, String maskedAccountNumber, AccountType accountType, String customerName, double balance) {
        this.accountId = accountId;
        this.maskedAccountNumber = maskedAccountNumber;
        this.accountType = accountType;
        this.customerName = customerName;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getMaskedAccountNumber() {
        return maskedAccountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getBalance() {
        return balance;
    }
}
