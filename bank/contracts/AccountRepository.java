package bank.contracts;

import bank.dto.AccountSearchFilters;
import bank.dto.Page;
import bank.dto.PageRequest;

/*
 * Account repository interface
 * IMPORTANT -> The implementation is provided by Armen, not me (this is for testing purposes only)
 */
public interface AccountRepository {
    Page<AccountProjection> search(AccountSearchFilters filters, OwnershipScope scope, PageRequest page);
    
    enum OwnershipScope {
        OWNED_ONLY,
        ANY
    }
} 
