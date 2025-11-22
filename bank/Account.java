package bank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Account {
    protected final String id;
    protected final Customer customer;
    protected final List<Transaction> transactions;
    protected double balance;

    public Account(Customer customer) {
        this(UUID.randomUUID().toString(), customer, 0.0);
    }

    public Account(String id, Customer customer, double balance) {
        this.id = id;
        this.customer = customer;
        this.balance = balance;
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public abstract void pay();
    public abstract void receipt();
}
