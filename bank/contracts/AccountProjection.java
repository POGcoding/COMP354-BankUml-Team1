package bank.contracts;

import bank.dto.AccountType;

/**
 * Account projection used by search/list operations.
 */
public interface AccountProjection {
    String getAccountId();

    String getAccountNumber();

    double getBalance();

    AccountType getAccountType();

    String getCustomerName();

    /**
     * UserId of the account owner (for OWNED_ONLY scoping).
     */
    String getOwnerUserId();

    /**
     * Optional place of birth filter support.
     */
    default String getPlaceOfBirth() {
        return "";
    }
}
