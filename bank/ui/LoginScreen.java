package bank.ui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Simple role selection window used to launch the appropriate dashboard.
 * Mirrors the existing compiled UI layout (3 buttons + label).
 */
public class LoginScreen extends JFrame {

    public enum RoleSelection { CUSTOMER, TELLER, ADMIN }

    public LoginScreen(Consumer<RoleSelection> onSelect) {
        super("MyBankUML - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 160);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Select a role to continue", SwingConstants.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton customerBtn = new JButton("Customer");
        JButton tellerBtn = new JButton("Teller");
        JButton adminBtn = new JButton("Admin");

        customerBtn.addActionListener(e -> {
            dispose();
            onSelect.accept(RoleSelection.CUSTOMER);
        });
        tellerBtn.addActionListener(e -> {
            dispose();
            onSelect.accept(RoleSelection.TELLER);
        });
        adminBtn.addActionListener(e -> {
            dispose();
            onSelect.accept(RoleSelection.ADMIN);
        });

        buttons.add(customerBtn);
        buttons.add(tellerBtn);
        buttons.add(adminBtn);

        add(label, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);
    }
}
