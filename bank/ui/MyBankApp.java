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
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

// GUI entry point for the JavaFX version of BankUML.
// Implements:
//  - Login/simulated login
//  - Role selection screen
//  - Customer dashboard (masked balances)
//  - Teller account search
//  - Admin role management (mock, in-memory roles)

public class MyBankApp extends Application {

    // we'll use this later when we actually care about the user
    private UserContext userContext;

    // Mock accounts for now
    private final List<RawAccount> mockAccounts = List.of(
            new RawAccount("CHK-001", "Chequing", 1234.56),
            new RawAccount("SAV-002", "Savings", 9876.54),
            new RawAccount("CRD-003", "Credit", -250.00)
    );

    // In-memory “role assignments” for admin mock
    private final Map<String, String> userRoles = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("MyBankUML");
        showLogin(stage);     // ✅ login first
        stage.show();
    }

    // ----------------------------
    // 1) Simulated Login Screen
    // ----------------------------
    private void showLogin(Stage stage) {
        Label title = new Label("Login (simulated)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter user ID (e.g. cust-123)");

        PasswordField pwField = new PasswordField();
        pwField.setPromptText("Password (ignored)");

        Label msg = new Label();

        Button cont = new Button("Continue");

        cont.setOnAction(e -> {
            String id = userIdField.getText().trim();
            if (id.isEmpty()) {
                msg.setText("Please enter a user ID.");
                return;
            }

            // store into context (simulated login)
            userContext = new UserContext(id, "CUSTOMER"); // role overwritten later

            showRoleSelection(stage);
        });

        VBox root = new VBox(10, title, userIdField, pwField, cont, msg);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 420, 260));
    }

    // ----------------------------
    // 2) Role Selection Screen
    // ----------------------------
    private void showRoleSelection(Stage stage) {
        Label label = new Label("Select role");
        label.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button customerBtn = new Button("Customer");
        Button tellerBtn = new Button("Teller");
        Button adminBtn = new Button("Admin");

        customerBtn.setPrefWidth(120);
        tellerBtn.setPrefWidth(120);
        adminBtn.setPrefWidth(120);

        customerBtn.setOnAction(e -> {
            setRole("CUSTOMER");
            showCustomerView(stage);
        });

        tellerBtn.setOnAction(e -> {
            setRole("TELLER");
            showTellerSearch(stage);
        });

        adminBtn.setOnAction(e -> {
            setRole("ADMIN");
            showAdminRoleMgmt(stage);
        });

        VBox root = new VBox(12, label, customerBtn, tellerBtn, adminBtn);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 420, 300));
    }

    private void setRole(String role) {
        if (userContext == null) userContext = new UserContext("unknown", role);
        userContext = new UserContext(userContext.userId(), role);
        userRoles.putIfAbsent(userContext.userId(), role); // seed mock
    }

    // ----------------------------
    // 3) Customer View
    // ----------------------------
    private void showCustomerView(Stage stage) {
        Label title = new Label("Customer accounts");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<AccountRow> table = buildAccountTable(false);

        ObservableList<AccountRow> rows = buildRowsForRole("CUSTOMER", null);
        table.setItems(rows);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showRoleSelection(stage));

        VBox root = new VBox(10, title, table, backBtn);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);

        stage.setScene(new Scene(root, 640, 420));
    }

    // ----------------------------
    // 4) Teller Search View
    // ----------------------------
    private void showTellerSearch(Stage stage) {
        Label title = new Label("Teller account search");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField queryField = new TextField();
        queryField.setPromptText("Search by Account ID or type");

        Button searchBtn = new Button("Search");

        HBox searchBar = new HBox(8, new Label("Query:"), queryField, searchBtn);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        TableView<AccountRow> table = buildAccountTable(true);

        // initial list
        table.setItems(buildRowsForRole("TELLER", ""));

        searchBtn.setOnAction(e -> {
            String q = queryField.getText();
            table.setItems(buildRowsForRole("TELLER", q));
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showRoleSelection(stage));

        VBox root = new VBox(10, title, searchBar, table, backBtn);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_LEFT);

        stage.setScene(new Scene(root, 720, 450));
    }

    // ----------------------------
    // 5) Admin Role Management View
    // ----------------------------
    private void showAdminRoleMgmt(Stage stage) {
        Label title = new Label("Admin role management");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter user ID (e.g. cust-123)");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("CUSTOMER", "TELLER", "ADMIN");
        roleBox.setValue("CUSTOMER");

        Button assignBtn = new Button("Assign role");
        Button removeBtn = new Button("Remove role");

        TextArea logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(8);

        assignBtn.setOnAction(e -> {
            String id = userIdField.getText().trim();
            if (id.isEmpty()) {
                logArea.appendText("Please enter a user ID before assigning.\n");
                return;
            }

            String newRole = roleBox.getValue();
            String oldRole = userRoles.get(id);

            if (oldRole == null) {
                userRoles.put(id, newRole);
                logArea.appendText("Assigned role " + newRole + " to user " + id + " (mock).\n");
            } else if (oldRole.equals(newRole)) {
                logArea.appendText("User " + id + " already had role " + newRole + " (no change).\n");
            } else {
                userRoles.put(id, newRole);
                logArea.appendText("Changed role for user " + id + " from " + oldRole + " to " + newRole + " (mock).\n");
            }
        });

        removeBtn.setOnAction(e -> {
            String id = userIdField.getText().trim();
            if (id.isEmpty()) {
                logArea.appendText("Please enter a user ID before removing.\n");
                return;
            }

            String removed = userRoles.remove(id);
            if (removed == null) {
                logArea.appendText("User " + id + " had no role assigned (nothing to remove).\n");
            } else {
                logArea.appendText("Removed role " + removed + " from user " + id + " (mock).\n");
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("User ID:"), 0, 0);
        form.add(userIdField, 1, 0);

        form.add(new Label("Role:"), 0, 1);
        form.add(roleBox, 1, 1);

        HBox buttons = new HBox(8, assignBtn, removeBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(10, title, form, buttons,
                new Label("Activity log (mock actions only):"),
                logArea);

        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_LEFT);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showRoleSelection(stage));
        root.getChildren().add(backBtn);

        stage.setScene(new Scene(root, 640, 420));
    }

    // ----------------------------
    // Helpers
    // ----------------------------
    private TableView<AccountRow> buildAccountTable(boolean fullBalances) {
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

        table.getColumns().addAll(idCol, typeCol, balCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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
