package bank.dto;

/**
 * Detailed account summary for customer view.
 */
public class AccountSummary {
    private final String accountId;
    private final String accountNumber;
    private final AccountType accountType;
    private final String customerName;

    public AccountSummary(String accountId, String accountNumber, AccountType accountType, String customerName) {
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.customerName = customerName;
    }

    public AccountSummary withMaskedNumber(String masked) {
        return new AccountSummary(accountId, masked, accountType, customerName);
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getCustomerName() {
        return customerName;
    }
}
