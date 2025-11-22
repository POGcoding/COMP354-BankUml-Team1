package bank.repository;

public final class PageRequest {

    private final int size;
    private final int index;

    public PageRequest(int size, int index) {
        this.size = size;
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public int getIndex() {
        return index;
    }
}
