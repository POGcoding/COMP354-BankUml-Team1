package bank.repository;

import bank.contracts.AuditLogRepository;

import java.time.Instant;

public class ConsoleAuditLogRepository implements AuditLogRepository {
    @Override
    public void record(String message) {
        System.out.printf("[AUDIT] %s %s%n", Instant.now(), message);
    }
}
