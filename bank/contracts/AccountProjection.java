package bank.contracts;

import bank.dto.AccountType;

/*
 * Account projection interface
 * IMPORTANT -> The implementation is provided by Armen, not me (this is for testing purposes only)
 */
public interface AccountProjection {
    String getAccountId();
    String getAccountNumber();
    AccountType getAccountType();
    String getCustomerName();
}
