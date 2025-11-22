package bank.security;

// Role changes happen (like assigning or removing roles), and logging is triggered.
public class RoleAdminController {
    private final AuthzService authz = new AuthzService();
    private final RoleRepository repo = new RoleRepository();
    private final AuditLogger logger = new AuditLogger();

    public void assignRole(String adminId, String targetUserId, String role) {
        if (!authz.hasRole(adminId, "ADMIN")) {
            System.out.println("Unauthorized: Only ADMIN can assign roles.");
            return;
        }
        repo.assignRole(targetUserId, role);
        logger.log(adminId + " assigned role " + role + " to " + targetUserId);
    }

    public void removeRole(String adminId, String targetUserId) {
        if (!authz.hasRole(adminId, "ADMIN")) {
            System.out.println("Unauthorized: Only ADMIN can remove roles.");
            return;
        }
        repo.removeRole(targetUserId);
        logger.log(adminId + " removed role from " + targetUserId);
    }
}
