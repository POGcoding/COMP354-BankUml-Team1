package bank.security;

import java.util.Map;
import java.util.HashMap;

public class RoleRepository {
    private final Map<String, String> roleMap = new HashMap<>();

    public void assignRole(String userId, String role) {
        roleMap.put(userId, role.toUpperCase());
    }

    public void removeRole(String userId) {
        roleMap.remove(userId);
    }

    public String getRole(String userId) {
        return roleMap.get(userId);
    }

    public Map<String, String> getAllRoles() {
        return roleMap;
    }
}
