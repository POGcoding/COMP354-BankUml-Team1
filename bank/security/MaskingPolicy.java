package bank.security;

import bank.Customer;

public class MaskingPolicy {

    public static String getVisibleCustomerInfo(Customer customer, String role) {
        return switch (role.toUpperCase()) {
            case "ADMIN" -> customer.toString();
            case "TELLER" -> maskPartial(customer);
            case "CUSTOMER" -> maskOwn(customer);
            default -> "Access denied.";
        };
    }

    private static String maskPartial(Customer customer) {
        return "Customer: " + customer.getName() + "\n"
                + "ID: ****" + safeTail(customer.getId()) + "\n"
                + "Balance: [hidden]";
    }

    private static String maskOwn(Customer customer) {
        return "Customer: " + customer.getName() + "\n"
                + "ID: ********\n"
                + "Balance: [hidden]";
    }

    private static String safeTail(String value) {
        if (value == null) {
            return "";
        }
        return value.length() <= 4 ? value : value.substring(value.length() - 4);
    }
}
