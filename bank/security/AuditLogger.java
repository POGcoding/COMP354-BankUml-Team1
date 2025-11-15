package bank.security;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

// Writing messages to the log file.
public class AuditLogger {
    private static final String LOG_FILE = "audit.log";

    public void log(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(LocalDateTime.now() + ": " + message + "\n");
        } catch (IOException e) {
            System.out.println("Audit logging failed: " + e.getMessage());
        }
    }
}
