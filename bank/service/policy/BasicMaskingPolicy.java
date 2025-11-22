package bank.service.policy;

import bank.contracts.MaskingPolicy;

/**
 * Simple masking: either full or last-4 only.
 */
public class BasicMaskingPolicy implements MaskingPolicy {
    private final boolean allowFull;

    private BasicMaskingPolicy(boolean allowFull) {
        this.allowFull = allowFull;
    }

    public static BasicMaskingPolicy allowFullAccess() {
        return new BasicMaskingPolicy(true);
    }

    public static BasicMaskingPolicy masked() {
        return new BasicMaskingPolicy(false);
    }

    @Override
    public boolean canSeeFullAccountNumber() {
        return allowFull;
    }

    @Override
    public String maskAccountNumber(String raw) {
        if (allowFull) {
            return raw;
        }
        if (raw == null) {
            return "";
        }
        String digits = raw.replaceAll("\\s+", "");
        if (digits.length() <= 4) {
            return "****";
        }
        String last4 = digits.substring(digits.length() - 4);
        return "**** **** **** " + last4;
    }
}
