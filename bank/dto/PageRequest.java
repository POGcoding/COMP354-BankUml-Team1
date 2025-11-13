package bank.dto;

import lombok.Getter;

/*
 * The follwing is a pagination request with automatic sanitization
 * 1. The page must be >= 0
 * 2. The size is capped at 50 items maximum
 */

 @Getter
public class PageRequest {
    private final int page;
    private final int size;

    public PageRequest(int page, int size) {
        this.page = Math.max(0, page);
        this.size = Math.min(50, Math.max(1, size));
    }
}
