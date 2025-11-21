package bank.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


// GUI entry point for the JavaFX version of BankUML.
// Implements:
//  - Role selection screen
//  - Customer dashboard (masked balances)
//  - Teller account search
//  - Admin role management (mock, in-memory roles)

public class MyBankApp extends Application {

    private UserContext userContext;

    // tiny in-memory “role database” for the admin screen
    private final Map<String, String> assignedRoles = new HashMap<>();

    // raw mock data used by both Customer & Teller views
    private final List<RawAccount> mockAccounts = List.of(
            new RawAccount("CHK-001", "Chequing", 1234.56),
            new RawAccount("SAV-002", "Savings", 9876.54),
            new RawAccount("CRD-003", "Credit", -250.00)
    );

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("MyBankUML");
        showRoleSelection(stage);
        stage.show();
    }

    // -------------------- PART 1: role selection --------------------

    private void showRoleSelection(Stage stage) {
        Label title = new Label("Select role");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button customerBtn = new Button("Customer");
        Button tellerBtn   = new Button("Teller");
        Button adminBtn    = new Button("Admin");

        customerBtn.setOnAction(e -> {
            userContext = new UserContext("CUSTOMER", "cust-123");
            showCustomerView(stage);
        });

        tellerBtn.setOnAction(e -> {
            userContext = new UserContext("TELLER", "teller-001");
            showTellerView(stage);
        });

        adminBtn.setOnAction(e -> {
            userContext = new UserContext("ADMIN", "admin");
            showAdminView(stage);
        });

        VBox root = new VBox(15, title, customerBtn, tellerBtn, adminBtn);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
    }

    // -------------------- PART 2: customer dashboard --------------------

    // Shows all accounts for the current customer using mask policy

    private void showCustomerView(Stage stage) {
        Label title = new Label("Customer accounts");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<AccountRow> table = createAccountTable();

        // show all accounts for this user (mocked) with CUSTOMER masking
        table.setItems(buildRowsForRole(userContext.role(), null));

        Button back = new Button("Back");
        back.setOnAction(e -> showRoleSelection(stage));

        HBox bottomBar = new HBox(back);
        bottomBar.setPadding(new Insets(10));
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setSpacing(10);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setCenter(table);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 520, 320);
        stage.setScene(scene);
    }

    // -------------------- PART 3: teller search screen --------------------
    // Allows tellers to search accounts by ID or type, no masking.
    private void showTellerView(Stage stage) {
        Label title = new Label("Teller account search");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField queryField = new TextField();
        queryField.setPromptText("Search by account ID or type...");
        Button searchBtn = new Button("Search");

        HBox searchBar = new HBox(10, new Label("Query:"), queryField, searchBtn);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(10, 0, 10, 0));

        TableView<AccountRow> table = createAccountTable();

        // initial view: show all accounts, masking based on TELLER role (no mask)
        table.setItems(buildRowsForRole(userContext.role(), null));

        searchBtn.setOnAction(e -> {
            String q = queryField.getText();
            table.setItems(buildRowsForRole(userContext.role(), q));
        });

        Button back = new Button("Back");
        back.setOnAction(e -> showRoleSelection(stage));

        HBox bottomBar = new HBox(back);
        bottomBar.setPadding(new Insets(10));
        bottomBar.setAlignment(Pos.CENTER_LEFT);

        VBox top = new VBox(title, searchBar);
        top.setSpacing(5);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setTop(top);
        BorderPane.setAlignment(top, Pos.CENTER);
        root.setCenter(table);
        root.setBottom(bottomBar);

        Scene scene = new Scene(root, 650, 350);
        stage.setScene(scene);
    }

    // -------------------- PART 4: admin role management --------------------

    // Simple mock role assign/remove using an in-memory Map

    private void showAdminView(Stage stage) {
        Label title = new Label("Admin role management");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // user id input
        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter user ID (e.g. cust-123)");

        // role combo box
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("CUSTOMER", "TELLER", "ADMIN");
        roleBox.setValue("CUSTOMER");

        HBox formRow1 = new HBox(10, new Label("User ID:"), userIdField);
        formRow1.setAlignment(Pos.CENTER_LEFT);

        HBox formRow2 = new HBox(10, new Label("Role:"), roleBox);
        formRow2.setAlignment(Pos.CENTER_LEFT);

        Button assignBtn = new Button("Assign role");
        Button removeBtn = new Button("Remove role");

        // log area
        ListView<String> logView = new ListView<>();
        logView.setPrefHeight(180);

        // Assign role logic
        assignBtn.setOnAction(e -> {
            String userId = userIdField.getText().trim();
            String role = roleBox.getValue();

            if (userId.isEmpty()) {
                logView.getItems().add("Please enter a user ID before assigning.");
                return;
            }

            String previous = assignedRoles.put(userId, role);

            if (previous == null) {
                logView.getItems().add("Assigned role " + role + " to user " + userId + " (mock).");
            } else if (previous.equals(role)) {
                logView.getItems().add("User " + userId + " already had role " + role + " (no change).");
            } else {
                logView.getItems().add("Changed role for user " + userId +
                        " from " + previous + " to " + role + " (mock).");
            }
        });

        // Remove role logic
        removeBtn.setOnAction(e -> {
            String userId = userIdField.getText().trim();

            if (userId.isEmpty()) {
                logView.getItems().add("Please enter a user ID before removing.");
                return;
            }

            String previous = assignedRoles.remove(userId);

            if (previous == null) {
                logView.getItems().add("User " + userId + " had no role assigned (nothing to remove).");
            } else {
                logView.getItems().add("Removed role " + previous + " from user " + userId + " (mock).");
            }
        });

        Button back = new Button("Back");
        back.setOnAction(e -> showRoleSelection(stage));

        HBox buttonRow = new HBox(10, assignBtn, removeBtn);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        VBox center = new VBox(10,
                formRow1,
                formRow2,
                buttonRow,
                new Label("Activity log (mock actions only):"),
                logView
        );
        center.setPadding(new Insets(10, 0, 0, 0));

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setCenter(center);

        HBox bottom = new HBox(10, back);
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottom);

        Scene scene = new Scene(root, 600, 380);
        stage.setScene(scene);
    }

    // -------------------- shared helpers --------------------

    /** Create the three-column table used by both customer & teller. */
    private TableView<AccountRow> createAccountTable() {
        TableView<AccountRow> table = new TableView<>();

        TableColumn<AccountRow, String> idCol = new TableColumn<>("Account ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        idCol.setPrefWidth(150);

        TableColumn<AccountRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(150);

        TableColumn<AccountRow, String> balCol = new TableColumn<>("Balance");
        balCol.setCellValueFactory(new PropertyValueFactory<>("balanceDisplay"));
        balCol.setPrefWidth(150);

        table.getColumns().add(idCol);
        table.getColumns().add(typeCol);
        table.getColumns().add(balCol);

        return table;
    }

    /**
     * Build AccountRow list for the given role, optionally filtered by query.
     * Query matches accountId or type (case-insensitive).
     */
    private ObservableList<AccountRow> buildRowsForRole(String role, String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase(Locale.ROOT);

        List<AccountRow> rows = mockAccounts.stream()
                .filter(acc ->
                        q.isEmpty()
                                || acc.id.toLowerCase(Locale.ROOT).contains(q)
                                || acc.type.toLowerCase(Locale.ROOT).contains(q)
                )
                .map(acc -> {
                    String displayBalance = MaskingPolicy.maskBalance(acc.balance, role);
                    return new AccountRow(acc.id, acc.type, displayBalance);
                })
                .collect(Collectors.toList());

        return FXCollections.observableArrayList(rows);
    }

    // raw data backing our mock accounts
    private static class RawAccount {
        final String id;
        final String type;
        final double balance;

        RawAccount(String id, String type, double balance) {
            this.id = id;
            this.type = type;
            this.balance = balance;
        }
    }
}
