package bank.dto;

import lombok.Getter;

/*
 * This wraps a user's ID as a type-safe object
 */

@Getter
public class UserId {
    private final String value;

    public UserId(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
