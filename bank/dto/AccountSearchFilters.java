package bank.dto;

import lombok.Getter;
import lombok.Setter;

/*
 * These are the Search Filters with AND logic
 * All the non-null/non-blank filters are combined with logical AND
 */

@Getter
@Setter
public class AccountSearchFilters {
    private String accountNumber;
    private String customerName;
    private AccountType accountType;

    // The follwing checks if the account number filter is provided
    public boolean hasAccountNumber() {
        return accountNumber != null && !accountNumber.isBlank();
    }

    // The following checks if the customer name filter is provided
    public boolean hasCustomerName() {
        return customerName != null && !customerName.isBlank();
    }

    // The following checks if the account type filter is provided
    public boolean hasAccountType() {
        return accountType != null;
    }
}
