package bank.dto;

public class AccountSearchFilters {
    private String accountNumber;
    private String customerName;
    private AccountType accountType;
    private String placeOfBirth;

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

    public boolean hasPlaceOfBirth() {
        return placeOfBirth != null && !placeOfBirth.isBlank();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }
}
