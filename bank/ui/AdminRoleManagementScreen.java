package bank.ui;

import bank.controller.RoleAdminController;
import bank.dto.UserId;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Admin screen for assigning roles and viewing current roles.
 */
public class AdminRoleManagementScreen extends JFrame {
    private final UserId operator;
    private final RoleAdminController controller;
    private final JTextArea output;

    public AdminRoleManagementScreen(UserId operator, RoleAdminController controller) {
        super("Admin Role Management");
        this.operator = operator;
        this.controller = controller;

        setSize(520, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextField userField = new JTextField("customer");
        JTextField roleField = new JTextField("CUSTOMER");
        JButton assignBtn = new JButton("Assign Role");
        assignBtn.addActionListener(e -> assignRole(userField.getText(), roleField.getText()));

        JPanel top = new JPanel(new BorderLayout());
        top.add(userField, BorderLayout.CENTER);
        top.add(roleField, BorderLayout.EAST);
        top.add(assignBtn, BorderLayout.SOUTH);

        output = new JTextArea();
        output.setEditable(false);
        refreshRoles(userField.getText());

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
    }

    private void assignRole(String userId, String role) {
        try {
            controller.assignRole(operator, new UserId(userId), role);
            refreshRoles(userId);
        } catch (Exception ex) {
            output.setText("Error: " + ex.getMessage());
        }
    }

    private void refreshRoles(String userId) {
        try {
            Set<String> roles = controller.rolesFor(operator, new UserId(userId));
            output.setText("Roles for " + userId + ": " + roles);
        } catch (Exception ex) {
            output.setText("Error: " + ex.getMessage());
        }
    }
}
