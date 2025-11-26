package bank.ui;

import bank.AppConfig;
import bank.contracts.RoleRepository;
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
 * JavaFX UI wired to backend controllers with role-based login.
 */
public class MyBankApp extends Application {

    private static AccountViewController staticAccountViewController;
    private static SearchController staticSearchController;
    private static RoleAdminController staticRoleAdminController;
    private static RoleRepository staticRoleRepository;

    private AccountViewController accountViewController;
    private SearchController searchController;
    private RoleAdminController roleAdminController;
    private RoleRepository roleRepository;
    private UserContext userContext;

    // Teller inputs
    private TextField tellerAccountNumberField;
    private TextField tellerCustomerNameField;
    private ComboBox<String> tellerAccountTypeBox;

    public static void setControllers(AccountViewController avc,
                                      SearchController sc,
                                      RoleAdminController rac) {
        staticAccountViewController = avc;
        staticSearchController = sc;
        staticRoleAdminController = rac;
    }

    public static void setRoleRepository(RoleRepository repo) {
        staticRoleRepository = repo;
    }

    @Override
    public void start(Stage stage) {
        if (staticAccountViewController == null || staticSearchController == null || staticRoleAdminController == null) {
            AppConfig config = new AppConfig();
            accountViewController = config.getAccountViewController();
            searchController = config.getSearchController();
            roleAdminController = config.getRoleAdminController();
            roleRepository = config.getRoleRepository();
        } else {
            accountViewController = staticAccountViewController;
            searchController = staticSearchController;
            roleAdminController = staticRoleAdminController;
            roleRepository = staticRoleRepository;
        }

        stage.setTitle("MyBankUML");
        showLoginScene(stage);
        stage.show();
    }

    private void showLoginScene(Stage stage) {
        Label title = new Label("Login");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Enter user ID (e.g., customer)");
        Label errorLabel = new Label();

        Button loginBtn = new Button("Continue");
        loginBtn.setOnAction(e -> routeByUser(userField.getText(), stage, errorLabel));

        VBox root = new VBox(10, title, userField, loginBtn, errorLabel);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(root, 420, 220));
    }

    private void routeByUser(String userId, Stage stage, Label errorLabel) {
        String id = userId == null ? "" : userId.trim().toLowerCase(Locale.ROOT);
        if (id.isEmpty()) {
            errorLabel.setText("Please enter a user ID.");
            return;
        }
        var roles = roleRepository.rolesFor(new UserId(id));
        if (roles.isEmpty()) {
            errorLabel.setText("Unknown user ID. Please try again.");
            return;
        }
        if (roles.contains("ADMIN")) {
            userContext = new UserContext("ADMIN", id);
            showAdminView(stage);
        } else if (roles.contains("TELLER")) {
            userContext = new UserContext("TELLER", id);
            showTellerView(stage);
        } else if (roles.contains("CUSTOMER")) {
            userContext = new UserContext("CUSTOMER", id);
            showCustomerView(stage);
        } else {
            errorLabel.setText("No supported role found for this user.");
        }
    }

    private void showCustomerView(Stage stage) {
        Label title = new Label("Customer accounts");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<AccountRow> table = createAccountTable();
        table.setItems(loadCustomerAccounts());

        Button back = new Button("Back");
        back.setOnAction(e -> showLoginScene(stage));

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setCenter(table);
        root.setBottom(back);
        BorderPane.setAlignment(back, Pos.CENTER_LEFT);

        stage.setScene(new Scene(root, 760, 420));
    }

    private void showTellerView(Stage stage) {
        Label title = new Label("Teller account search");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        tellerAccountNumberField = new TextField();
        tellerAccountNumberField.setPromptText("Account ID/Number");
        tellerCustomerNameField = new TextField();
        tellerCustomerNameField.setPromptText("Customer name");
        tellerAccountTypeBox = new ComboBox<>();
        tellerAccountTypeBox.getItems().addAll("Any", "CARD", "SAVING", "CHECK");
        tellerAccountTypeBox.setValue("Any");
        Button searchBtn = new Button("Search");

        HBox searchBar = new HBox(10,
                new Label("Account:"), tellerAccountNumberField,
                new Label("Name:"), tellerCustomerNameField,
                new Label("Type:"), tellerAccountTypeBox,
                searchBtn);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(10, 0, 10, 0));

        TableView<AccountRow> table = createAccountTable();
        table.setItems(runSearchFromInputs());

        searchBtn.setOnAction(e -> table.setItems(runSearchFromInputs()));

        Button back = new Button("Back");
        back.setOnAction(e -> showLoginScene(stage));

        VBox root = new VBox(10, title, searchBar, table, back);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_LEFT);

        stage.setScene(new Scene(root, 900, 480));
    }

    private void showAdminView(Stage stage) {
        Label title = new Label("Admin role management");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter user ID");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("CUSTOMER", "TELLER", "ADMIN");
        roleBox.setValue("CUSTOMER");

        Button assignBtn = new Button("Assign role");
        Button removeBtn = new Button("Remove role");
        ListView<String> logView = new ListView<>();
        logView.setPrefHeight(180);

        assignBtn.setOnAction(e -> {
            String userId = userIdField.getText().trim().toLowerCase(Locale.ROOT);
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
            String userId = userIdField.getText().trim().toLowerCase(Locale.ROOT);
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
        back.setOnAction(e -> showLoginScene(stage));

        HBox form = new HBox(10,
                new Label("User ID:"), userIdField,
                new Label("Role:"), roleBox,
                assignBtn, removeBtn);
        form.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(10, title, form,
                new Label("Activity log:"),
                logView,
                back);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_LEFT);

        stage.setScene(new Scene(root, 700, 420));
    }

    private TableView<AccountRow> createAccountTable() {
        TableView<AccountRow> table = new TableView<>();

        TableColumn<AccountRow, String> idCol = new TableColumn<>("Account ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("accountId"));
        idCol.setPrefWidth(180);

        TableColumn<AccountRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(140);

        TableColumn<AccountRow, String> nameCol = new TableColumn<>("Customer");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        nameCol.setPrefWidth(200);

        TableColumn<AccountRow, String> balCol = new TableColumn<>("Balance");
        balCol.setCellValueFactory(new PropertyValueFactory<>("balanceDisplay"));
        balCol.setPrefWidth(140);

        table.getColumns().add(idCol);
        table.getColumns().add(typeCol);
        table.getColumns().add(nameCol);
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

    private ObservableList<AccountRow> runSearchFromInputs() {
        try {
            AccountSearchFilters filters = new AccountSearchFilters();
            if (tellerAccountNumberField != null && !tellerAccountNumberField.getText().isBlank()) {
                filters.setAccountNumber(tellerAccountNumberField.getText().trim());
            }
            if (tellerCustomerNameField != null && !tellerCustomerNameField.getText().isBlank()) {
                filters.setCustomerName(tellerCustomerNameField.getText().trim());
            }
            if (tellerAccountTypeBox != null) {
                String selected = tellerAccountTypeBox.getValue();
                if (selected != null && !"Any".equalsIgnoreCase(selected)) {
                    filters.setAccountType(AccountType.valueOf(selected));
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
                                r.getMaskedAccountNumber(),
                                r.getAccountType().name(),
                                String.format("$%.2f", r.getBalance()),
                                r.getCustomerName()))
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
