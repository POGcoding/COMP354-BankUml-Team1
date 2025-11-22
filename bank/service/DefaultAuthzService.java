package bank.service;

import bank.contracts.AccountProjection;
import bank.contracts.AccountRepository;
import bank.contracts.AuditLogRepository;
import bank.contracts.AuthzService;
import bank.contracts.MaskingPolicy;
import bank.contracts.RoleRepository;
import bank.dto.UserId;
import bank.service.policy.BasicMaskingPolicy;

/**
 * RBAC for Customer/Teller/Admin roles.
 */
public class DefaultAuthzService implements AuthzService {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_TELLER = "TELLER";
    public static final String ROLE_CUSTOMER = "CUSTOMER";

    private final RoleRepository roleRepository;
    private final AuditLogRepository auditLogRepository;
    private final AccountRepository accountRepository;

    public DefaultAuthzService(RoleRepository roleRepository,
                               AuditLogRepository auditLogRepository,
                               AccountRepository accountRepository) {
        this.roleRepository = roleRepository;
        this.auditLogRepository = auditLogRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean canSearch(UserId requester) {
        return hasRole(requester, ROLE_TELLER) || hasRole(requester, ROLE_ADMIN);
    }

    @Override
    public boolean isCustomer(UserId requester) {
        return hasRole(requester, ROLE_CUSTOMER);
    }

    @Override
    public MaskingPolicy maskingPolicyFor(UserId requester) {
        if (hasRole(requester, ROLE_ADMIN) || hasRole(requester, ROLE_TELLER)) {
            return BasicMaskingPolicy.allowFullAccess();
        }
        return BasicMaskingPolicy.masked();
    }

    @Override
    public boolean canManageRoles(UserId requester) {
        return hasRole(requester, ROLE_ADMIN);
    }

    @Override
    public boolean canViewAccounts(UserId requester) {
        return hasRole(requester, ROLE_ADMIN) || hasRole(requester, ROLE_TELLER) || hasRole(requester, ROLE_CUSTOMER);
    }

    @Override
    public boolean canViewAccount(UserId requester, String accountId) {
        if (hasRole(requester, ROLE_ADMIN) || hasRole(requester, ROLE_TELLER)) {
            return true;
        }
        if (hasRole(requester, ROLE_CUSTOMER)) {
            AccountProjection projection = accountRepository.findById(accountId);
            boolean allowed = projection != null && requester.getValue().equalsIgnoreCase(projection.getOwnerUserId());
            if (!allowed) {
                auditLogRepository.record("Denied account access for user " + requester + " on account " + accountId);
            }
            return allowed;
        }
        auditLogRepository.record("Denied account access for user " + requester + " on account " + accountId);
        return false;
    }

    private boolean hasRole(UserId requester, String role) {
        return requester != null && roleRepository.hasRole(requester, role);
    }
}
