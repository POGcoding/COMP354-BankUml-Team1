package bank.repository;

import bank.contracts.RoleRepository;
import bank.dto.UserId;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InMemoryRoleRepository implements RoleRepository {
    private final Map<String, Set<String>> rolesByUser = new HashMap<>();

    public InMemoryRoleRepository() {
        // seed a few defaults
        assignRole(new UserId("admin"), "ADMIN");
        assignRole(new UserId("teller"), "TELLER");
        assignRole(new UserId("customer"), "CUSTOMER");
    }

    @Override
    public void assignRole(UserId userId, String role) {
        rolesByUser.computeIfAbsent(userId.getValue(), k -> new HashSet<>())
                .add(role.toUpperCase());
    }

    @Override
    public void removeRole(UserId userId, String role) {
        Set<String> roles = rolesByUser.get(userId.getValue());
        if (roles != null) {
            roles.remove(role.toUpperCase());
            if (roles.isEmpty()) {
                rolesByUser.remove(userId.getValue());
            }
        }
    }

    @Override
    public boolean hasRole(UserId userId, String role) {
        return rolesFor(userId).contains(role.toUpperCase());
    }

    @Override
    public Set<String> rolesFor(UserId userId) {
        return rolesByUser.getOrDefault(userId.getValue(), Collections.emptySet());
    }
}
