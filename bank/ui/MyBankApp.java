package bank.ui;

import bank.AppConfig;
import bank.controller.AccountViewController;
import bank.controller.RoleAdminController;
import bank.controller.SearchController;
import bank.dto.AccountSearchFilters;
import bank.dto.AccountType;
import bank.dto.Page;
import bank.dto.PageRequest;
import bank.dto.UserId;
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

import java.util.Locale;
import java.util.stream.Collectors;

/**
 * JavaFX UI from teammate (keeps layout) wired to backend controllers.
 */
public class MyBankApp extends Application {

    private static AccountViewController staticAccountViewController;
    private static SearchController staticSearchController;
    private static RoleAdminController staticRoleAdminController;

    private AccountViewController accountViewController;
    private SearchController searchController;
    private RoleAdminController roleAdminController;
    private UserContext userContext;

    public static void main(String[] args) {
        launch(args);
    }

    /** Allow wiring controllers before launch. */
    public static void setControllers(AccountViewController avc,
                                      SearchController sc,
                                      RoleAdminController rac) {
        staticAccountViewController = avc;
        staticSearchController = sc;
        staticRoleAdminController = rac;
    }

    @Override
    public void start(Stage stage) {
        if (staticAccountViewController == null || staticSearchController == null || staticRoleAdminController == null) {
            AppConfig config = new AppConfig();
            accountViewController = config.getAccountViewController();
            searchController = config.getSearchController();
            roleAdminController = config.getRoleAdminController();
        } else {
            accountViewController = staticAccountViewController;
            searchController = staticSearchController;
            roleAdminController = staticRoleAdminController;
        }

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
            userContext = new UserContext("CUSTOMER", "customer");
            showCustomerView(stage);
        });

        tellerBtn.setOnAction(e -> {
            userContext = new UserContext("TELLER", "teller");
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

    private void showCustomerView(Stage stage) {
        Label title = new Label("Customer accounts");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<AccountRow> table = createAccountTable();

        table.setItems(loadCustomerAccounts());

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

        table.setItems(runSearch(queryField.getText()));

        searchBtn.setOnAction(e -> {
            String q = queryField.getText();
            table.setItems(runSearch(q));
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

    private void showAdminView(Stage stage) {
        Label title = new Label("Admin role management");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter user ID (e.g. cust-123)");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("CUSTOMER", "TELLER", "ADMIN");
        roleBox.setValue("CUSTOMER");

        HBox formRow1 = new HBox(10, new Label("User ID:"), userIdField);
        formRow1.setAlignment(Pos.CENTER_LEFT);

        HBox formRow2 = new HBox(10, new Label("Role:"), roleBox);
        formRow2.setAlignment(Pos.CENTER_LEFT);

        Button assignBtn = new Button("Assign role");
        Button removeBtn = new Button("Remove role");

        ListView<String> logView = new ListView<>();
        logView.setPrefHeight(180);

        assignBtn.setOnAction(e -> {
            String userId = userIdField.getText().trim();
            String role = roleBox.getValue();
            if (userId.isEmpty()) {
                logView.getItems().add("Please enter a user ID before assigning.");
                return;
            }
            try {
                roleAdminController.assignRole(new UserId(userContext.userId()), new UserId(userId), role);
                logView.getItems().add("Assigned role " + role + " to user " + userId + ".");
                refreshRoles(logView, userId);
            } catch (Exception ex) {
                logView.getItems().add("Error: " + ex.getMessage());
            }
        });

        removeBtn.setOnAction(e -> {
            String userId = userIdField.getText().trim();
            if (userId.isEmpty()) {
                logView.getItems().add("Please enter a user ID before removing.");
                return;
            }
            try {
                roleAdminController.removeRole(new UserId(userContext.userId()), new UserId(userId), roleBox.getValue());
                logView.getItems().add("Removed role " + roleBox.getValue() + " from user " + userId + ".");
                refreshRoles(logView, userId);
            } catch (Exception ex) {
                logView.getItems().add("Error: " + ex.getMessage());
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
                new Label("Activity log:"),
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

    private ObservableList<AccountRow> loadCustomerAccounts() {
        try {
            Page<bank.dto.AccountRow> page = accountViewController.listAccounts(
                    new UserId(userContext.userId()),
                    new PageRequest(0, 50));
            return mapRows(page);
        } catch (Exception ex) {
            return FXCollections.observableArrayList();
        }
    }

    private ObservableList<AccountRow> runSearch(String query) {
        try {
            AccountSearchFilters filters = new AccountSearchFilters();
            if (query != null && !query.isBlank()) {
                String q = query.trim();
                filters.setAccountNumber(q);
                try {
                    filters.setAccountType(AccountType.valueOf(q.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException ignored) {
                    // not an account type
                }
            }
            Page<bank.dto.AccountRow> page = searchController.search(
                    new UserId(userContext.userId()),
                    filters,
                    new PageRequest(0, 50));
            return mapRows(page);
        } catch (Exception ex) {
            return FXCollections.observableArrayList();
        }
    }

    private ObservableList<AccountRow> mapRows(Page<bank.dto.AccountRow> page) {
        return FXCollections.observableArrayList(
                page.getItems().stream()
                        .map(r -> new AccountRow(
                                r.getMaskedAccountNumber(), // show masked/unmasked per policy
                                r.getAccountType().name(),
                                String.format("$%.2f", r.getBalance())))
                        .collect(Collectors.toList())
        );
    }

    private void refreshRoles(ListView<String> logView, String userId) {
        try {
            var roles = roleAdminController.rolesFor(new UserId(userContext.userId()), new UserId(userId));
            logView.getItems().add("Roles for " + userId + ": " + roles);
        } catch (Exception ex) {
            logView.getItems().add("Error: " + ex.getMessage());
        }
    }
}
