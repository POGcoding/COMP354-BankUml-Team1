package bank.ui;

public class AccountRow {

    private final String accountId; // used for masked/unmasked account number
    private final String type;
    private final String balanceDisplay;
    private final String customerName;

    public AccountRow(String accountId, String type, String balanceDisplay, String customerName) {
        this.accountId = accountId;
        this.type = type;
        this.balanceDisplay = balanceDisplay;
        this.customerName = customerName;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getType() {
        return type;
    }

    public String getBalanceDisplay() {
        return balanceDisplay;
    }

    public String getCustomerName() {
        return customerName;
    }
}
