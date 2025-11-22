package bank.repository;

import bank.Account;
import bank.Card;
import bank.Check;
import bank.Customer;
import bank.Saving;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class AccountSeeder {

    private static final String[] FIRST_NAMES = {
            "Alice","Bob","Carol","David","Eve",
            "Frank","Grace","Hank","Ivy","Jack",
            "Kara","Liam","Mona","Ned","Owen",
            "Pia","Quinn","Rae","Sam","Tia"
    };

    private static final String[] LAST_NAMES = {
            "Smith","Johnson","Brown","Williams","Jones",
            "Miller","Davis","Garcia","Rodriguez","Wilson"
    };

    private static final long SEED = 354L;
    private static final int OWNER_RANGE = 2000;

    public void seed10k(AccountRepository repository) {
        repository.seed(generate(10000));
    }

    public List<Account> generate(int count) {
        Random random = new Random(SEED);
        List<Account> accounts = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            accounts.add(randomAccount(random, i));
        }
        return accounts;
    }

    private Account randomAccount(Random random, int index) {
        Customer customer = randomCustomer(random);
        double balance = randomBalance(random);
        int type = random.nextInt(3);
        String id = String.valueOf(index);
        if (type == 0) {
            return new Saving(id, customer, balance);
        } else if (type == 1) {
            return new Check(id, customer, balance);
        } else {
            return new Card(id, customer, balance);
        }
    }

    private Customer randomCustomer(Random random) {
        String id = randomOwnerId(random);
        String name = randomName(random);
        return new Customer(id, name);
    }

    private String randomOwnerId(Random random) {
        int value = random.nextInt(OWNER_RANGE);
        return "user" + value;
    }

    private String randomName(Random random) {
        String first = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String last = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return first + " " + last;
    }

    private double randomBalance(Random random) {
        double min = 10.0;
        double max = 100000.0;
        return min + random.nextDouble() * (max - min);
    }
}
