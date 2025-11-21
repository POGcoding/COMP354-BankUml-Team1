package bank.ui;

public class UserContext {

    private final String role;
    private final String userId;

    public UserContext(String role, String userId) {
        this.role = role;
        this.userId = userId;
    }

    public String role() {
        return role;
    }

    public String userId() {
        return userId;
    }
}
