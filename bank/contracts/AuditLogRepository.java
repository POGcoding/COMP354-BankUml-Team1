package bank.contracts;

public interface AuditLogRepository {
    void record(String message);
}
