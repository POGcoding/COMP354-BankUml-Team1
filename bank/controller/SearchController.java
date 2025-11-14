package bank.controller;

import bank.contracts.AuthzService;
import bank.dto.*;
import bank.service.SearchService;

/*
 * Search Controller implementing FR-3 entry point
 * 
 * This is my(Pierre-Olivier) public API for the search feature.
 */
public class SearchController {
    private final SearchService searchService;
    private final AuthzService authzService;

    /*
     * Constructor with dependency injection
     * Dependencies will be wired by Mattis (Integration Engineer)
     * 
     * @param searchService -> search business logic
     * @param authzService -> authorization service
     */
    public SearchController(SearchService searchService, AuthzService authzService) {
        this.searchService = searchService;
        this.authzService = authzService;
    }

    /*
     * The following is the mai nsearch endpoint for GUI/Integration
     * 
     * Method's flow:
     * 1. Checks if user is authorized to search
     * 2. Sanitizes filters by converting null to empty filters
     * 3. Delegate to SearchService
     * 4. Returns results
     * 
     * @param requester -> user performing search
     * @param filters -> search criteria
     * @param page -> pagination parameters
     * @return page of account rows (will be empty if there are no results)
     * @throws SecurityException if the user is not authorized to search
     * @throws IllegalArgumentException if the requester is null
     */
    public Page<AccountRow> search(UserId requester, AccountSearchFilters filters, PageRequest page) {
        // Validates if a requester is provided
        if (requester == null){
            throw new IllegalArgumentException("Requester cannot be null");
        }

        // Validates if a page reuqest is provided
        if (page == null) {
            throw new IllegalArgumentException("Page request cannot be null");
        }

        // Step 1: Validate authorization
        if (!authzService.canSearch(requester)) {
            throw new SecurityException("User not authorized to search: " + requester);
        }

        // Step 2: Sanitize filters
        filters = sanitizeFilters(filters);

        // Step 3: Delegate to service layer
        return searchService.searchAccounts(requester, filters, page);
    }

    /*
     * Sanitize filters -> convert null to empty filters object
     * 
     * @param filters -> original filters
     * @return non-null filters object
     */
    private AccountSearchFilters sanitizeFilters(AccountSearchFilters filters) {
        return filters != null ? filters : new AccountSearchFilters();
    }
    
}
