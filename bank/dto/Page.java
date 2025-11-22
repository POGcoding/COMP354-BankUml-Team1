package bank.dto;

import java.util.List;

/*
 * This is a generic page wrapper for paginated results
 * @param <T> type of items in the page
 */
public class Page<T> {
    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalItems;

    public Page(List<T> items, int page, int size, long totalItems) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalItems() {
        return totalItems;
    }
}
