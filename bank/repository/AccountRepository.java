package bank.repository;

import bank.Account;
import bank.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AccountRepository {

    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 50;

    private final List<Account> data = new ArrayList<>();

    public void seed(List<Account> accounts) {
        data.clear();
        if (accounts != null) {
            data.addAll(accounts);
        }
    }

    public int count() {
        return data.size();
    }

    public SearchResult search(AccountSearchFilters filters, PageRequest pageRequest) {
        List<Account> filtered = filterAccounts(filters);
        int total = filtered.size();
        PageRequest normalized = normalize(pageRequest);
        List<Account> page = applyPaging(filtered, normalized);
        return new SearchResult(page, total, normalized.getSize(), normalized.getIndex());
    }

    private List<Account> filterAccounts(AccountSearchFilters filters) {
        List<Account> result = new ArrayList<>();
        for (Account account : data) {
            if (matchesAll(account, filters)) {
                result.add(account);
            }
        }
        return result;
    }

    private boolean matchesAll(Account account, AccountSearchFilters filters) {
        return matchesType(account, filters)
                && matchesCustomerName(account, filters)
                && matchesAccountId(account, filters)
                && matchesOwnerCustomerId(account, filters);
    }

    private boolean matchesType(Account account, AccountSearchFilters filters) {
        String expectedType = trim(filters.getAccountType());
        if (expectedType == null) {
            return true;
        }
        String actualType = account.getClass().getSimpleName();
        return expectedType.equalsIgnoreCase(actualType);
    }

    private boolean matchesCustomerName(Account account, AccountSearchFilters filters) {
        String pattern = trim(filters.getCustomerNameLike());
        if (pattern == null) {
            return true;
        }
        String name = safeCustomerName(account);
        return name.toLowerCase().contains(pattern.toLowerCase());
    }

    private boolean matchesAccountId(Account account, AccountSearchFilters filters) {
        String expectedId = trim(filters.getAccountId());
        if (expectedId == null) {
            return true;
        }
        return expectedId.equals(account.getId());
    }

    private boolean matchesOwnerCustomerId(Account account, AccountSearchFilters filters) {
        String expectedOwner = trim(filters.getOwnerCustomerId());
        if (expectedOwner == null) {
            return true;
        }
        String ownerId = safeCustomerId(account);
        return expectedOwner.equals(ownerId);
    }

    private PageRequest normalize(PageRequest pageRequest) {
        int size = pageRequest == null ? MIN_PAGE_SIZE : pageRequest.getSize();
        int index = pageRequest == null ? 0 : pageRequest.getIndex();
        if (size < MIN_PAGE_SIZE) {
            size = MIN_PAGE_SIZE;
        }
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }
        if (index < 0) {
            index = 0;
        }
        return new PageRequest(size, index);
    }

    private List<Account> applyPaging(List<Account> accounts, PageRequest pageRequest) {
        if (accounts.isEmpty()) {
            return Collections.emptyList();
        }
        int size = pageRequest.getSize();
        int index = pageRequest.getIndex();
        int start = index * size;
        if (start >= accounts.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + size, accounts.size());
        return accounts.subList(start, end);
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    private String safeCustomerName(Account account) {
        Customer customer = account.getCustomer();
        String name = customer.getName();
        return name == null ? "" : name;
    }

    private String safeCustomerId(Account account) {
        Customer customer = account.getCustomer();
        String id = customer.getId();
        return id == null ? "" : id;
    }
}
