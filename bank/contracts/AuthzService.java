package bank.contracts;

import bank.dto.UserId;

/**
 * Authorization service contract (RBAC + masking).
 */
public interface AuthzService {
    boolean canSearch(UserId requester);

    boolean isCustomer(UserId requester);

    MaskingPolicy maskingPolicyFor(UserId requester);

    /**
     * Can the requester manage roles?
     */
    default boolean canManageRoles(UserId requester) {
        return false;
    }

    /**
     * Can the requester view any accounts (list)?
     */
    default boolean canViewAccounts(UserId requester) {
        return false;
    }

    /**
     * Can the requester view a specific account?
     */
    default boolean canViewAccount(UserId requester, String accountId) {
        return canViewAccounts(requester);
    }
}
