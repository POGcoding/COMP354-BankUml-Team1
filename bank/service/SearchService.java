package bank.service;

import bank.contracts.AccountProjection;
import bank.contracts.AccountRepository;
import bank.contracts.AuthzService;
import bank.contracts.MaskingPolicy;
import bank.dto.*;

import java.util.List;
import java.util.stream.Collectors;

/*
 * Search Service implementing FR-3 requirements
 */
public class SearchService {
    private final AccountRepository repository;
    private final AuthzService authzService;

    /*
     * Contructor with dependency injection
     * The dependencies will be wired by Mattis (Integration Engineer)
     * 
     * @param repository account data access (Armen's implementation)
     * @param authzService authorization service (Geon's implementation)
     */

    public SearchService(AccountRepository repository, AuthzService authzService) {
        this.repository = repository;
        this.authzService = authzService;
    }
    /*
     * The following is the main Search method -> called by the SearchController
     * 
     * Method's Flow:
     * 1. Determine scope
     * 2. Call repository to get projections
     * 3. Get masking policy for this user
     * 4. Map projections to rows with masking applied
     * 5. Log performance metrics for TR-03 evidence
     * 
     * @param requester -> user performing search
     * @param filters -> search criteria
     * @param pageRequest -> pagination parameters
     * @return page of account rows with the masking applied
     */
    public Page<AccountRow> searchAccounts(UserId requester, AccountSearchFilters filters, PageRequest pageRequest) {

        // This starts the perfromance timer for TR-03 evidence
        long startTime = System.currentTimeMillis();

        // Step 1: This detemrines the ownership scope based on the user role
        // If the user = Customer -> Can only see their own accounts
        // If user = Teller/Admin -> Can see all accounts
        AccountRepository.OwnershipScope scope = determineOwnershipScope(requester);

        // Step 2: The calls the repository with filters
        Page<AccountProjection> projections = repository.search(requester, filters, scope, pageRequest);

        // Step 3: This gets the masking policy for the user
        MaskingPolicy policy = authzService.maskingPolicyFor(requester);

        // Step 4: This maps projections to rows with masking
        List<AccountRow> rows = mapToRows(projections.getItems(), policy);

        // Step 5: This calculates and logs the performance
        long duration = System.currentTimeMillis() - startTime;
        logPerformance(duration, filters, projections.getTotalItems());

        // We then return the paginated results with the same metadata
        return new Page<>(rows, projections.getPage(), projections.getSize(), projections.getTotalItems());
    }

    /*
     * The following determines search scope based on the user role
     * 
     * @param requester -> user performaing the search
     * @return OWNED_ONLY if the user is a customer, ANY otherwise
     */
    private AccountRepository.OwnershipScope determineOwnershipScope(UserId requester) {
        boolean isCustomer = authzService.isCustomer(requester);
        
        if (isCustomer) {
            return AccountRepository.OwnershipScope.OWNED_ONLY;
        } else {
            return AccountRepository.OwnershipScope.ANY;
        }
    }

    /*
     * The following maps projections to account rows with masking applied
     * 
     * For each projection from the repository:
     * 1. We get the raw (unmaksed) account number
     * 2. We apply the masking policy based on the user role
     * 3. We create AccountRow with a masked number
     * 
     * @param projections -> the raw data from the repository
     * @param policy -> the masking policy for the current user
     * @return the list of account rows for display
     */
    private List<AccountRow> mapToRows(List<AccountProjection> projections, MaskingPolicy policy) {
        return projections.stream().map(proj -> new AccountRow(
            proj.getAccountId(), 
            policy.maskAccountNumber(proj.getAccountNumber()),
            proj.getAccountType(),
            proj.getCustomerName(),
            proj.getBalance())).collect(Collectors.toList());
    }

    /*
     * The following logs performance metrics for TR-03 evidence
     * 
     * TR-03 requires <= 2 seconds per search on ~10k accounts
     * 
     * @param durationMs -> search duration in milliseconds
     * @param fitlers -> filters that were applied
     * @param totalResults -> total matching items accross all pages
     */
    private void logPerformance(long durationMs, AccountSearchFilters filters, long totalResults) {
        System.out.printf("[PERF] Search completed in %dms | Results: %d | Filters: %s%n", durationMs, totalResults, formatFilters(filters));
        
        // The following warns if the performance target is missed
        if (durationMs > 2000) {
            System.err.printf("[WARNING] Performance target missed! Expected <=2000ms, actual: %dms%n", durationMs);
        }
    }

    /*
     * The following formats filters for logging
     * 
     * It creates a readable string representation of applied filters.
     * It only includes filters that are actually set (non-null/non-blank)
     * 
     * @param filters -> search filters to format
     * @return a human-readable filter string
     */
    private String formatFilters(AccountSearchFilters filters) {
        StringBuilder sb = new StringBuilder();
        
        // Add account number filter if present
        if (filters.hasAccountNumber()) {
            sb.append("accountNumber=").append(filters.getAccountNumber()).append(" ");
        }
        
        // Add customer name filter if present
        if (filters.hasCustomerName()) {
            sb.append("customerName=").append(filters.getCustomerName()).append(" ");
        }
        
        // Add account type filter if present
        if (filters.hasAccountType()) {
            sb.append("accountType=").append(filters.getAccountType()).append(" ");
        }
        if (filters.hasPlaceOfBirth()) {
            sb.append("placeOfBirth=").append(filters.getPlaceOfBirth()).append(" ");
        }
        
        // Return "none" if no filters applied, otherwise return trimmed string
        String result = sb.toString().trim();
        return result.isEmpty() ? "none" : result;
    }
}
