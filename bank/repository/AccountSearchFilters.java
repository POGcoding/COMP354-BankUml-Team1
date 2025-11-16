package bank.repository;

public final class AccountSearchFilters {

    private String accountType;
    private String customerNameLike;
    private String accountId;
    private String ownerCustomerId;

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCustomerNameLike() {
        return customerNameLike;
    }

    public void setCustomerNameLike(String customerNameLike) {
        this.customerNameLike = customerNameLike;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOwnerCustomerId() {
        return ownerCustomerId;
    }

    public void setOwnerCustomerId(String ownerCustomerId) {
        this.ownerCustomerId = ownerCustomerId;
    }
}
