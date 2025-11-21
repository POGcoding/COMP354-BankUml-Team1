package bank.ui;

public class AccountRow {

    private final String accountId;
    private final String type;
    private final String balanceDisplay;

    public AccountRow(String accountId, String type, String balanceDisplay) {
        this.accountId = accountId;
        this.type = type;
        this.balanceDisplay = balanceDisplay;
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
}
