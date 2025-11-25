package bank.ui;

import bank.controller.AccountViewController;
import bank.controller.SearchController;
import bank.controller.RoleAdminController;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JavaFX GUI entry point for BankUML.
 * Screens:
 *  - Simulated login
 *  - Role selection
 *  - Customer account dashboard (masked balances)
 *  - Teller account search
 *  - Admin role management (mock, in-memory)
 */
public class MyBankApp extends Application {

    // Logged-in user (simulated)
    private UserContext userContext;

    // Controllers (wired by Main.java; not yet used in the mock UI)
    private static AccountViewController accountViewController;
    private static SearchController      searchController;
    private static RoleAdminController   roleAdminController;


    // Mock "role assignments" for the admin screen
    private final Map<String, String> assignedRoles = new HashMap<>();

    // Mock backing data used by Customer & Teller views
    private final List<RawAccount> mockAccounts = List.of(
            new RawAccount("CHK-001", "Chequing", 1234.56),
            new RawAccount("SAV-002", "Savings", 9876.54),
            new RawAccount("CRD-003", "Credit",  -250.00)
    );

    // ----- wiring from Main.java -----

    public static void setControllers(AccountViewController accountViewController,
                                      SearchController searchController,
                                      RoleAdminController roleAdminController) {
        MyBankApp.accountViewController = accountViewController;
        MyBankApp.searchController = searchController;
        MyBankApp.roleAdminController = roleAdminController; 
    }      
                                      

        // Currently unused; UI still uses mock data.
        // Later you can swap mockAccounts / assignedRoles for real controller calls.
    

    // ----- standard JavaFX entry point -----

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("MyBankUML");
        showLogin(stage);     // first screen: simulated login
        stage.show();
    }

    // ========================================================================
    // 1) Simulated login
    // ========================================================================

    private void showLogin(Stage stage) {
        Label title = new Label("Login (simulated)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter user ID (e.g. cust-123)");

        PasswordField pwField = new PasswordField();
        pwField.setPromptText("Password (ignored)");

        Label message = new Label();

        Button continueBtn = new Button("Continue");
        continueBtn.setOnAction(e -> {
            String id = userIdField.getText().trim();
            if (id.isEmpty()) {
                message.setText("Please enter a user ID.");
                return;
            }

            // Store a simple context; role will be chosen on next screen
            userContext = new UserContext(id, "CUSTOMER");

            showRoleSelection(stage);
        });

        VBox root = new VBox(10, title, userIdField, pwField, continueBtn, message);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 420, 260));
    }

    // ========================================================================
    // 2) Role selection
    // ========================================================================

    private void showRoleSelection(Stage stage) {
        Label title = new Label("Select role");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button customerBtn = new Button("Customer");
        Button tellerBtn   = new Button("Teller");
        Button adminBtn    = new Button("Admin");

        customerBtn.setPrefWidth(140);
        tellerBtn.setPrefWidth(140);
        adminBtn.setPrefWidth(140);

        customerBtn.setOnAction(e -> showCustomerView(stage));
        tellerBtn.setOnAction(e -> showTellerView(stage));
        adminBtn.setOnAction(e -> showAdminView(stage));

        VBox root = new VBox(15, title, customerBtn, tellerBtn, adminBtn);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 400, 250));
    }

    // ========================================================================
    // 3) Customer dashboard
    // ========================================================================

    private void showCustomerView(Stage stage) {
        Label title = new Label("Customer accounts");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<AccountRow> table = buildAccountTable();
        table.setItems(buildRowsForRole("CUSTOMER", null)); // masked balances

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showRoleSelection(stage));

        VBox root = new VBox(10, title, table, backBtn);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);

        stage.setScene(new Scene(root, 640, 420));
    }

    // ========================================================================
    // 4) Teller search view
    // ========================================================================

    private void showTellerView(Stage stage) {
        Label title = new Label("Teller account search");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField queryField = new TextField();
        queryField.setPromptText("Search by account ID or type");

        Button searchBtn = new Button("Search");

        TableView<AccountRow> table = buildAccountTable();

        HBox searchBar = new HBox(8, new Label("Query:"), queryField, searchBtn);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        // initially show all accounts (unfiltered)
        table.setItems(buildRowsForRole("TELLER", null));

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

    // ========================================================================
    // 5) Admin role management (mock)
    // ========================================================================

    private void showAdminView(Stage stage) {
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
            String oldRole = assignedRoles.get(id);

            if (oldRole == null) {
                assignedRoles.put(id, newRole);
                logArea.appendText("Assigned role " + newRole + " to user " + id + " (mock).\n");
            } else if (oldRole.equals(newRole)) {
                logArea.appendText("User " + id + " already had role " + newRole + " (no change).\n");
            } else {
                assignedRoles.put(id, newRole);
                logArea.appendText("Changed role for user " + id + " from " + oldRole +
                        " to " + newRole + " (mock).\n");
            }
        });

        removeBtn.setOnAction(e -> {
            String id = userIdField.getText().trim();
            if (id.isEmpty()) {
                logArea.appendText("Please enter a user ID before removing.\n");
                return;
            }

            String removed = assignedRoles.remove(id);
            if (removed == null) {
                logArea.appendText("User " + id + " had no role assigned (nothing to remove).\n");
            } else {
                logArea.appendText("Removed role " + removed + " from user " + id + " (mock).\n");
            }
        });

        HBox form = new HBox(10,
                new Label("User ID:"), userIdField,
                new Label("Role:"), roleBox,
                assignBtn, removeBtn
        );
        form.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showRoleSelection(stage));

        VBox root = new VBox(10, title, form,
                new Label("Activity log (mock actions only):"),
                logArea,
                backBtn
        );
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_LEFT);

        stage.setScene(new Scene(root, 700, 420));
    }

    // ========================================================================
    // Shared helpers
    // ========================================================================

    private TableView<AccountRow> buildAccountTable() {
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

    // backing data for mockAccounts
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
