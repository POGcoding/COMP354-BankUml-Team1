package bank.controller;

import bank.contracts.AuthzService;
import bank.dto.*;
import bank.service.AccountQueryService;

/**
 * Controller for customer account view (FR-1).
 */
public class AccountViewController {
    private final AuthzService authzService;
    private final AccountQueryService accountQueryService;

    public AccountViewController(AuthzService authzService, AccountQueryService accountQueryService) {
        this.authzService = authzService;
        this.accountQueryService = accountQueryService;
    }

    public AccountSummary getAccountSummary(UserId requester, String accountId) {
        return accountQueryService.getAccountSummary(requester, accountId);
    }

    public Page<AccountRow> listAccounts(UserId requester, PageRequest pageRequest) {
        return accountQueryService.listAccounts(requester, pageRequest);
    }
}
