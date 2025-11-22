package bank.contracts;

import bank.dto.UserId;

import java.util.Set;

public interface RoleRepository {
    void assignRole(UserId userId, String role);

    void removeRole(UserId userId, String role);

    boolean hasRole(UserId userId, String role);

    Set<String> rolesFor(UserId userId);
}
