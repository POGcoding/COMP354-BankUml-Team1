package bank.service;

import bank.contracts.AccountProjection;
import bank.contracts.AccountRepository;
import bank.contracts.AuthzService;
import bank.contracts.MaskingPolicy;
import bank.dto.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Account view/list operations with RBAC + masking.
 */
public class AccountQueryService {
    private final AccountRepository accountRepository;
    private final AuthzService authzService;

    public AccountQueryService(AccountRepository accountRepository, AuthzService authzService) {
        this.accountRepository = accountRepository;
        this.authzService = authzService;
    }

    public AccountSummary getAccountSummary(UserId requester, String accountId) {
        if (requester == null || accountId == null) {
            throw new IllegalArgumentException("Requester and accountId are required");
        }
        if (!authzService.canViewAccount(requester, accountId)) {
            throw new SecurityException("Not authorized to view account " + accountId);
        }
        AccountProjection projection = accountRepository.findById(accountId);
        if (projection == null) {
            return null;
        }
        MaskingPolicy policy = authzService.maskingPolicyFor(requester);
        return new AccountSummary(
                projection.getAccountId(),
                policy.maskAccountNumber(projection.getAccountNumber()),
                projection.getAccountType(),
                projection.getCustomerName());
    }

    public Page<AccountRow> listAccounts(UserId requester, PageRequest pageRequest) {
        if (requester == null || pageRequest == null) {
            throw new IllegalArgumentException("Requester and pageRequest are required");
        }
        if (!authzService.canViewAccounts(requester)) {
            throw new SecurityException("Not authorized to view accounts");
        }

        AccountRepository.OwnershipScope scope = authzService.isCustomer(requester)
                ? AccountRepository.OwnershipScope.OWNED_ONLY
                : AccountRepository.OwnershipScope.ANY;

        Page<AccountProjection> projections = accountRepository.search(requester, new AccountSearchFilters(), scope, pageRequest);
        MaskingPolicy policy = authzService.maskingPolicyFor(requester);
        List<AccountRow> rows = projections.getItems().stream()
                .map(p -> new AccountRow(
                        p.getAccountId(),
                        policy.maskAccountNumber(p.getAccountNumber()),
                        p.getAccountType(),
                        p.getCustomerName(),
                        p.getBalance()))
                .collect(Collectors.toList());

        return new Page<>(rows, projections.getPage(), projections.getSize(), projections.getTotalItems());
    }
}
