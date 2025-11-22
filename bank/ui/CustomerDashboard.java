package bank.ui;

import bank.controller.AccountViewController;
import bank.dto.AccountRow;
import bank.dto.Page;
import bank.dto.PageRequest;
import bank.dto.UserId;

import javax.swing.*;
import java.awt.*;

/**
 * Customer dashboard showing account list for the logged-in user.
 */
public class CustomerDashboard extends JFrame {

    public CustomerDashboard(UserId user, AccountViewController controller) {
        super("Customer Dashboard");
        setSize(420, 240);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setText(loadAccounts(user, controller));

        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    private String loadAccounts(UserId user, AccountViewController controller) {
        try {
            Page<AccountRow> page = controller.listAccounts(user, new PageRequest(0, 10));
            StringBuilder sb = new StringBuilder("Accounts for ").append(user).append("\n\n");
            if (page.isEmpty()) {
                sb.append("(no accounts)");
            } else {
                for (AccountRow row : page.getItems()) {
                    sb.append(row.getAccountId())
                            .append(" |")
                            .append(row.getAccountType())
                            .append(" |")
                            .append(row.getMaskedAccountNumber())
                            .append(" |")
                            .append(row.getCustomerName())
                            .append("\n");
                }
            }
            return sb.toString();
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }
    }
}
