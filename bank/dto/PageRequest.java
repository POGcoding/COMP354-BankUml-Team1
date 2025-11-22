package bank.dto;

public class PageRequest {
    private final int page;
    private final int size;

    public PageRequest(int page, int size) {
        this.page = Math.max(0, page);
        this.size = Math.min(50, Math.max(1, size));
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
