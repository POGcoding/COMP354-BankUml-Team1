package bank.dto;

import lombok.Getter;

/*
 * This is the search result row to display in the GUI
 * The Account numbers are already masked according to the user policy
 */

 @Getter
public class AccountRow {
    private final String accountId;
    private final String maskedAccountNumber;
    private final AccountType accountType;
    private final String customerName;

    public AccountRow(String accountId, String maskedAccountNumber, AccountType accountType, String customerName) {
        this.accountId = accountId;
        this.maskedAccountNumber = maskedAccountNumber;
        this.accountType = accountType;
        this.customerName = customerName;
    }
}
