package bank.repository;

import bank.Account;
import bank.Card;
import bank.Check;
import bank.Customer;
import bank.Saving;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public final class AccountRepositoryTest {

    private AccountRepository repository;
    private AccountSeeder seeder;

    @BeforeEach
    void setUp() {
        repository = new AccountRepository();
        seeder = new AccountSeeder();
        seeder.seed10k(repository);
    }

    @Test
    void seedShouldLoadTenThousandAccounts() {
        assertEquals(10000, repository.count());
    }

    @Test
    void searchWithoutFiltersReturnsFirstPageAndTotalCount() {
        AccountSearchFilters filters = new AccountSearchFilters();
        PageRequest pageRequest = new PageRequest(50, 0);

        SearchResult result = repository.search(filters, pageRequest);

        assertEquals(50, result.getAccounts().size());
        assertEquals(repository.count(), result.getTotalCount());
    }

    @Test
    void searchByTypeReturnsOnlyThatType() {
        AccountSearchFilters filters = new AccountSearchFilters();
        filters.setAccountType("Saving");
        PageRequest pageRequest = new PageRequest(50, 0);

        SearchResult result = repository.search(filters, pageRequest);
        List<Account> accounts = result.getAccounts();

        assertFalse(accounts.isEmpty());
        for (Account account : accounts) {
            assertEquals("Saving", account.getClass().getSimpleName());
        }
    }

    @Test
    void searchByCustomerNameIsCaseInsensitiveSubstring() {
        AccountSearchFilters filters = new AccountSearchFilters();
        filters.setCustomerNameLike("alice");
        PageRequest pageRequest = new PageRequest(50, 0);

        SearchResult result = repository.search(filters, pageRequest);
        List<Account> accounts = result.getAccounts();

        assertFalse(accounts.isEmpty());
        for (Account account : accounts) {
            String name = account.getCustomer().getName().toLowerCase();
            assertTrue(name.contains("alice"));
        }
    }

    @Test
    void searchByAccountIdReturnsSingleAccount() {
        AccountSearchFilters filters = new AccountSearchFilters();
        filters.setAccountId("1");
        PageRequest pageRequest = new PageRequest(10, 0);

        SearchResult result = repository.search(filters, pageRequest);
        List<Account> accounts = result.getAccounts();

        assertEquals(1, result.getTotalCount());
        assertEquals(1, accounts.size());
        assertEquals("1", accounts.get(0).getId());
    }

    @Test
    void searchByOwnerCustomerIdReturnsOnlyOwnedAccounts() {
        AccountRepository smallRepo = new AccountRepository();

        Customer owner1 = new Customer("owner-1", "Owner One");
        Customer owner2 = new Customer("owner-2", "Owner Two");

        Account a1 = new Saving("A1", owner1, 100.0);
        Account a2 = new Check("A2", owner1, 200.0);
        Account a3 = new Card("A3", owner2, 300.0);

        smallRepo.seed(List.of(a1, a2, a3));

        AccountSearchFilters filters = new AccountSearchFilters();
        filters.setOwnerCustomerId("owner-1");
        PageRequest pageRequest = new PageRequest(10, 0);

        SearchResult result = smallRepo.search(filters, pageRequest);
        List<Account> accounts = result.getAccounts();

        assertEquals(2, result.getTotalCount());
        assertEquals(2, accounts.size());
        for (Account account : accounts) {
            assertEquals("owner-1", account.getCustomer().getId());
        }
    }

    @Test
    void paginationCapsPageSizeAtFifty() {
        AccountSearchFilters filters = new AccountSearchFilters();
        PageRequest pageRequest = new PageRequest(500, 0);

        SearchResult result = repository.search(filters, pageRequest);

        assertEquals(50, result.getPageSize());
        assertEquals(50, result.getAccounts().size());
    }

    @Test
    void paginationReturnsEmptyForTooLargeIndex() {
        AccountSearchFilters filters = new AccountSearchFilters();
        PageRequest pageRequest = new PageRequest(50, 10_000);

        SearchResult result = repository.search(filters, pageRequest);

        assertTrue(result.getAccounts().isEmpty());
    }

    @Test
    void searchOnTenThousandAccountsMeetsPerformanceTarget() {
        AccountSearchFilters filters = new AccountSearchFilters();
        filters.setCustomerNameLike("a");
        PageRequest pageRequest = new PageRequest(50, 0);

        long start = System.nanoTime();
        SearchResult result = repository.search(filters, pageRequest);
        long end = System.nanoTime();

        long elapsedMillis = (end - start) / 1_000_000;

        assertFalse(result.getAccounts().isEmpty());
        assertTrue(elapsedMillis < 2000, "Elapsed ms was " + elapsedMillis);
        System.out.println("Search elapsed: " + elapsedMillis + " ms");
    }
}
