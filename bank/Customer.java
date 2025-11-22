package bank;

public class Customer {

    private String id;
    private String name;

    // Constructor with name only
    public Customer(String name) {
        this("unknown", name);
    }

    public Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Display customers info
    public void printCustomerInfo() {
        System.out.println("Customer's info: " );
        System.out.println("id: " + id);
        System.out.println("name: "+ name);
    }
}
