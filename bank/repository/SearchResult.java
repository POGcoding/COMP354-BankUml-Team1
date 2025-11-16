package bank.repository;

import bank.Account;
import java.util.List;

public final class SearchResult {

    private final List<Account> accounts;
    private final int totalCount;
    private final int pageSize;
    private final int pageIndex;

    public SearchResult(List<Account> accounts, int totalCount, int pageSize, int pageIndex) {
        this.accounts = accounts;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }
}
