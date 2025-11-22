package bank.ui;

import bank.controller.SearchController;
import bank.dto.AccountRow;
import bank.dto.AccountSearchFilters;
import bank.dto.AccountType;
import bank.dto.Page;
import bank.dto.PageRequest;
import bank.dto.UserId;

import javax.swing.*;
import java.awt.*;

/**
 * Teller search screen with simple text inputs and a result area.
 */
public class TellerSearchScreen extends JFrame {
    private final UserId user;
    private final SearchController controller;
    private final JTextArea resultsArea;
    private final JTextField accountNumberField;
    private final JTextField customerNameField;
    private final JComboBox<String> accountTypeBox;

    public TellerSearchScreen(UserId user, SearchController controller) {
        super("Teller Search");
        this.user = user;
        this.controller = controller;

        setSize(480, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        accountNumberField = new JTextField(10);
        customerNameField = new JTextField(10);
        accountTypeBox = new JComboBox<>(new String[] {"Any", "CARD", "SAVING", "CHECK"});

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        filters.add(new JLabel("Number:"));
        filters.add(accountNumberField);
        filters.add(new JLabel("Name:"));
        filters.add(customerNameField);
        filters.add(new JLabel("Type:"));
        filters.add(accountTypeBox);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> updateResults());
        filters.add(searchBtn);

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setText(runSearch(new AccountSearchFilters()));

        add(filters, BorderLayout.NORTH);
        add(new JScrollPane(resultsArea), BorderLayout.CENTER);
    }

    private void updateResults() {
        AccountSearchFilters filters = new AccountSearchFilters();
        if (!accountNumberField.getText().isBlank()) {
            filters.setAccountNumber(accountNumberField.getText().trim());
        }
        if (!customerNameField.getText().isBlank()) {
            filters.setCustomerName(customerNameField.getText().trim());
        }
        String type = (String) accountTypeBox.getSelectedItem();
        if (type != null && !"Any".equalsIgnoreCase(type)) {
            filters.setAccountType(AccountType.valueOf(type));
        }
        resultsArea.setText(runSearch(filters));
    }

    private String runSearch(AccountSearchFilters filters) {
        try {
            Page<AccountRow> page = controller.search(user, filters, new PageRequest(0, 10));
            StringBuilder sb = new StringBuilder("Search results for ").append(user).append("\n\n");
            if (page.isEmpty()) {
                sb.append("(no results)");
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
