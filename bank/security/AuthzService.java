package bank.security;

import java.util.Map;
import java.util.HashMap;

public class AuthzService {
    private RoleRepository roleRepo = new RoleRepository();

    public boolean hasRole(String userId, String requiredRole) {
        String userRole = roleRepo.getRole(userId);
        return requiredRole.equalsIgnoreCase(userRole);
    }

    public boolean isAuthorized(String userId, String action) {
        String role = roleRepo.getRole(userId);
        if (role == null) return false;

        return switch (role.toUpperCase()) {
            case "ADMIN" -> true;
            case "TELLER" -> !action.equalsIgnoreCase("DELETE_CUSTOMER");
            case "CUSTOMER" -> action.equalsIgnoreCase("VIEW_OWN_INFO");
            default -> false;
        };
    }

    public String getUserRole(String userId) {
        return roleRepo.getRole(userId);
    }
}
