package bank.contracts;

/*
 * Masking policy interface
 * IMPORTANT -> The implementation is provided by Geon, not me (this is for testing purposes only)
 */
public interface MaskingPolicy {
    boolean canSeeFullAccountNumber();
    String maskAccountNumber(String raw);
}
