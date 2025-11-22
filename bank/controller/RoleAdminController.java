package bank.controller;

import bank.contracts.AuthzService;
import bank.dto.UserId;
import bank.service.RoleAdminService;

import java.util.Set;

/**
 * Controller for admin role management (FR-2).
 */
public class RoleAdminController {
    private final AuthzService authzService;
    private final RoleAdminService roleAdminService;

    public RoleAdminController(AuthzService authzService, RoleAdminService roleAdminService) {
        this.authzService = authzService;
        this.roleAdminService = roleAdminService;
    }

    public void assignRole(UserId operator, UserId target, String role) {
        if (operator == null || target == null) {
            throw new IllegalArgumentException("Operator and target are required");
        }
        roleAdminService.assignRole(operator, target, role);
    }

    public Set<String> rolesFor(UserId operator, UserId target) {
        if (operator == null || target == null) {
            throw new IllegalArgumentException("Operator and target are required");
        }
        return roleAdminService.rolesFor(operator, target);
    }

    public void removeRole(UserId operator, UserId target, String role) {
        if (operator == null || target == null) {
            throw new IllegalArgumentException("Operator and target are required");
        }
        roleAdminService.removeRole(operator, target, role);
    }
}
