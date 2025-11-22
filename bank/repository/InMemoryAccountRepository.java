package bank.repository;

import bank.contracts.AccountProjection;
import bank.contracts.AccountRepository;
import bank.dto.AccountSearchFilters;
import bank.dto.AccountType;
import bank.dto.Page;
import bank.dto.PageRequest;
import bank.dto.UserId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * In-memory account repository with filtering and pagination.
 */
public class InMemoryAccountRepository implements AccountRepository {
    private final List<SimpleAccountProjection> accounts = new ArrayList<>();

    public InMemoryAccountRepository() {
        seedDefaults();
    }

    @Override
    public Page<AccountProjection> search(UserId requester, AccountSearchFilters filters, OwnershipScope scope, PageRequest page) {
        final AccountSearchFilters f = filters == null ? new AccountSearchFilters() : filters;
        final UserId req = requester;
        final OwnershipScope sc = scope;

        List<SimpleAccountProjection> filtered = accounts.stream()
                .filter(acc -> sc != OwnershipScope.OWNED_ONLY || acc.ownerUserId.equalsIgnoreCase(req.getValue()))
                .filter(acc -> !f.hasAccountNumber() || contains(acc.accountNumber, f.getAccountNumber()) || contains(acc.accountId, f.getAccountNumber()))
                .filter(acc -> !f.hasCustomerName() || contains(acc.customerName, f.getCustomerName()))
                .filter(acc -> !f.hasPlaceOfBirth() || contains(acc.placeOfBirth, f.getPlaceOfBirth()))
                .filter(acc -> !f.hasAccountType() || acc.accountType == f.getAccountType())
                .collect(Collectors.toList());

        int from = page.getPage() * page.getSize();
        int to = Math.min(from + page.getSize(), filtered.size());
        List<AccountProjection> pageItems = from >= filtered.size()
                ? Collections.emptyList()
                : new ArrayList<>(filtered.subList(from, to));

        return new Page<>(pageItems, page.getPage(), page.getSize(), filtered.size());
    }

    @Override
    public AccountProjection findById(String accountId) {
        return accounts.stream()
                .filter(acc -> acc.accountId.equalsIgnoreCase(accountId))
                .findFirst()
                .orElse(null);
    }

    private boolean contains(String source, String query) {
        return source != null && query != null && source.toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }

    private void seedDefaults() {
        // a handful of predictable records for demos/tests
        accounts.add(new SimpleAccountProjection("ACC-DEMO-1", "5200 1111 2222 3333", AccountType.CARD, "Demo Customer", "customer", "Toronto", 1234.56));
        accounts.add(new SimpleAccountProjection("ACC-DEMO-2", "5200 4444 5555 6666", AccountType.SAVING, "Demo Customer", "customer", "Montreal", 9876.54));
        accounts.add(new SimpleAccountProjection("ACC-DEMO-3", "5200 7777 8888 9999", AccountType.CHECK, "Demo Customer", "customer", "Vancouver", 250.00));

        AccountSeeder seeder = new AccountSeeder();
        accounts.addAll(seeder.generate(10_000));
    }

    /**
     * Internal projection backing.
     */
    public static class SimpleAccountProjection implements AccountProjection {
        private final String accountId;
        private final String accountNumber;
        private final AccountType accountType;
        private final String customerName;
        private final String ownerUserId;
        private final String placeOfBirth;
        private final double balance;

        public SimpleAccountProjection(String accountId,
                                       String accountNumber,
                                       AccountType accountType,
                                       String customerName,
                                       String ownerUserId,
                                       String placeOfBirth,
                                       double balance) {
            this.accountId = accountId;
            this.accountNumber = accountNumber;
            this.accountType = accountType;
            this.customerName = customerName;
            this.ownerUserId = ownerUserId == null ? "" : ownerUserId;
            this.placeOfBirth = placeOfBirth == null ? "" : placeOfBirth;
            this.balance = balance;
        }

        @Override
        public String getAccountId() {
            return accountId;
        }

        @Override
        public String getAccountNumber() {
            return accountNumber;
        }

        @Override
        public AccountType getAccountType() {
            return accountType;
        }

        @Override
        public String getCustomerName() {
            return customerName;
        }

        @Override
        public String getOwnerUserId() {
            return ownerUserId;
        }

        @Override
        public String getPlaceOfBirth() {
            return placeOfBirth;
        }

        @Override
        public double getBalance() {
            return balance;
        }
    }

    /**
     * Generates deterministic sample data (~10k) for performance tests.
     */
    private static class AccountSeeder {
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

        private static final String[] PLACES = {
                "Toronto","Vancouver","Calgary","Montreal","Ottawa",
                "Quebec City","Halifax","Winnipeg","Victoria","Edmonton"
        };

        private static final long SEED = 354L;
        private static final int OWNER_RANGE = 2000;

        public List<SimpleAccountProjection> generate(int count) {
            Random random = new Random(SEED);
            List<SimpleAccountProjection> result = new ArrayList<>(count);
            for (int i = 1; i <= count; i++) {
                result.add(randomAccount(random, i));
            }
            return result;
        }

        private SimpleAccountProjection randomAccount(Random random, int index) {
            String accountId = "ACC-" + index;
            String accountNumber = "5200 " + (1000 + index % 9000) + " " + (1000 + random.nextInt(9000)) + " " + (1000 + random.nextInt(9000));
            AccountType type = AccountType.values()[random.nextInt(AccountType.values().length)];
            String customerName = randomName(random);
            String ownerUserId = randomOwnerId(random);
            String place = PLACES[random.nextInt(PLACES.length)];
            double balance = randomBalance(random);
            return new SimpleAccountProjection(accountId, accountNumber, type, customerName, ownerUserId, place, balance);
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
}
