package bank;

import bank.contracts.AccountRepository;
import bank.contracts.AuditLogRepository;
import bank.contracts.AuthzService;
import bank.contracts.RoleRepository;
import bank.controller.AccountViewController;
import bank.controller.RoleAdminController;
import bank.controller.SearchController;
import bank.repository.ConsoleAuditLogRepository;
import bank.repository.InMemoryAccountRepository;
import bank.repository.InMemoryRoleRepository;
import bank.service.AccountQueryService;
import bank.service.DefaultAuthzService;
import bank.service.RoleAdminService;
import bank.service.SearchService;

/**
 * Central wiring for controllers/services/repositories.
 */
public class AppConfig {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final AuditLogRepository auditLogRepository;

    private final AuthzService authzService;
    private final AccountQueryService accountQueryService;
    private final SearchService searchService;
    private final RoleAdminService roleAdminService;

    private final AccountViewController accountViewController;
    private final SearchController searchController;
    private final RoleAdminController roleAdminController;

    public AppConfig() {
        this.accountRepository = new InMemoryAccountRepository();
        this.roleRepository = new InMemoryRoleRepository();
        this.auditLogRepository = new ConsoleAuditLogRepository();

        this.authzService = new DefaultAuthzService(roleRepository, auditLogRepository, accountRepository);
        this.accountQueryService = new AccountQueryService(accountRepository, authzService);
        this.searchService = new SearchService(accountRepository, authzService);
        this.roleAdminService = new RoleAdminService(roleRepository, authzService, auditLogRepository);

        this.accountViewController = new AccountViewController(authzService, accountQueryService);
        this.searchController = new SearchController(searchService, authzService);
        this.roleAdminController = new RoleAdminController(authzService, roleAdminService);
    }

    public AccountViewController getAccountViewController() {
        return accountViewController;
    }

    public SearchController getSearchController() {
        return searchController;
    }

    public RoleAdminController getRoleAdminController() {
        return roleAdminController;
    }

    public RoleRepository getRoleRepository() {
        return roleRepository;
    }
}
