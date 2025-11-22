package bank.contracts;

import bank.dto.AccountSearchFilters;
import bank.dto.Page;
import bank.dto.PageRequest;
import bank.dto.UserId;

/**
 * Account repository interface.
 */
public interface AccountRepository {
    Page<AccountProjection> search(UserId requester, AccountSearchFilters filters, OwnershipScope scope, PageRequest page);

    AccountProjection findById(String accountId);

    enum OwnershipScope {
        OWNED_ONLY,
        ANY
    }
} 
