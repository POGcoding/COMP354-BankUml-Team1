package bank;

import bank.ui.MyBankApp;
import javafx.application.Application;

/**
 * Application launcher wiring controllers to the JavaFX UI.
 */
public class Main {
    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        MyBankApp.setControllers(
                config.getAccountViewController(),
                config.getSearchController(),
                config.getRoleAdminController());
        MyBankApp.setRoleRepository(config.getRoleRepository());
        Application.launch(MyBankApp.class, args);
    }
}
