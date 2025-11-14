package bank.contracts;

import bank.dto.UserId;

/*
 * Authorization service interface
 * IMPORTANT -> The implementation is provided by Geon, not me (this is for testing purposes only)
 */
public interface AuthzService {
    boolean canSearch(UserId requester);
    boolean isCustomer(UserId requester);
    MaskingPolicy maskingPolicyFor(UserId requester);
}
