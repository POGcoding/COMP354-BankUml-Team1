package bank.service;

import bank.contracts.AuditLogRepository;
import bank.contracts.AuthzService;
import bank.contracts.RoleRepository;
import bank.dto.UserId;

import java.util.Set;

/**
 * Role management operations.
 */
public class RoleAdminService {
    private final RoleRepository roleRepository;
    private final AuthzService authzService;
    private final AuditLogRepository auditLogRepository;

    public RoleAdminService(RoleRepository roleRepository, AuthzService authzService, AuditLogRepository auditLogRepository) {
        this.roleRepository = roleRepository;
        this.authzService = authzService;
        this.auditLogRepository = auditLogRepository;
    }

    public void assignRole(UserId operator, UserId target, String role) {
        if (operator == null || target == null || role == null || role.isBlank()) {
            throw new IllegalArgumentException("Operator, target, and role are required");
        }
        if (!authzService.canManageRoles(operator)) {
            auditLogRepository.record("Denied role change by " + operator + " for " + target + " -> " + role);
            throw new SecurityException("Not authorized to assign roles");
        }
        String normalizedRole = role.toUpperCase();
        roleRepository.assignRole(target, normalizedRole);
        auditLogRepository.record(operator + " assigned role " + normalizedRole + " to " + target);
    }

    public Set<String> rolesFor(UserId operator, UserId target) {
        if (!authzService.canManageRoles(operator)) {
            auditLogRepository.record("Denied role listing by " + operator + " for " + target);
            throw new SecurityException("Not authorized to view roles");
        }
        return roleRepository.rolesFor(target);
    }

    public void removeRole(UserId operator, UserId target, String role) {
        if (operator == null || target == null || role == null || role.isBlank()) {
            throw new IllegalArgumentException("Operator, target, and role are required");
        }
        if (!authzService.canManageRoles(operator)) {
            auditLogRepository.record("Denied role removal by " + operator + " for " + target + " -> " + role);
            throw new SecurityException("Not authorized to remove roles");
        }
        String normalizedRole = role.toUpperCase();
        roleRepository.removeRole(target, normalizedRole);
        auditLogRepository.record(operator + " removed role " + normalizedRole + " from " + target);
    }
}
