package bank.ui;

public class MaskingPolicy {

    /**
     * Very simple masking rule:
     * - Customer: hide most of the balance (****123.45)
     * - Teller/Admin: show full balance
     */
    public static String maskBalance(double balance, String role) {
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            return "****" + String.format("%.2f", balance);
        }
        return String.format("%.2f", balance);
    }
}
