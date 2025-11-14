# Search Feature Implementation

**Implemented by:** Pierre-Olivier  
**Date:** November 14th, 2024

---

## What I've Built

### Components
- **SearchController** (`bank/controller/SearchController.java`) - Public API for GUI
- **SearchService** (`bank/service/SearchService.java`) - Search logic with performance tracking
- **6 DTOs** (`bank/dto/`) - Data structures (UserId, AccountType, AccountSearchFilters, PageRequest, Page, AccountRow)
- **4 Contracts** (`bank/contracts/`) - Interfaces defining dependencies

### Features Implemented
- Multi-filter AND logic support (TR-02)
- Pagination capped at 50 items
- Performance tracking for TR-03 (≤2s target)
- Role-based scope determination (Customer vs Teller/Admin)
- Account masking via policy pattern

---

## Public API

### SearchController.search()

```java
public Page<AccountRow> search(
    UserId requester,
    AccountSearchFilters filters,
    PageRequest page
) throws SecurityException, IllegalArgumentException
```

**What it does:**
1. Validates authorization via `AuthzService.canSearch()`
2. Sanitizes input (null filters → empty filters)
3. Delegates to `SearchService`
4. Returns paginated, masked results

**Exceptions:**
- `SecurityException`: User not authorized
- `IllegalArgumentException`: Null requester or page

**Example usage:**
```java
SearchController controller = ...; // Wired by integration

UserId user = new UserId("customer123");
AccountSearchFilters filters = new AccountSearchFilters();
filters.setCustomerName("Smith");
PageRequest page = new PageRequest(0, 25);

try {
    Page<AccountRow> results = controller.search(user, filters, page);
    // Display results...
} catch (SecurityException e) {
    // Show access denied
}
```

---

## Dependencies (Contracts)

My implementation depends on these interfaces that need to be provided:

### 1. AuthzService
**Location:** `bank/contracts/AuthzService.java`

```java
public interface AuthzService {
    boolean canSearch(UserId requester);
    boolean isCustomer(UserId requester);
    MaskingPolicy maskingPolicyFor(UserId requester);
}
```

**How I use it:**
- `canSearch()` - Called by SearchController to check authorization
- `isCustomer()` - Called by SearchService to determine scope (OWNED_ONLY vs ANY)
- `maskingPolicyFor()` - Called by SearchService to get masking policy for results

---

### 2. MaskingPolicy
**Location:** `bank/contracts/MaskingPolicy.java`

```java
public interface MaskingPolicy {
    boolean canSeeFullAccountNumber();
    String maskAccountNumber(String raw);
}
```

**How I use it:**
- `maskAccountNumber()` - Called on each search result to mask account numbers

---

### 3. AccountRepository
**Location:** `bank/contracts/AccountRepository.java`

```java
public interface AccountRepository {
    Page<AccountProjection> search(
        AccountSearchFilters filters, 
        OwnershipScope scope, 
        PageRequest page
    );
}
```

**How I use it:**
- My SearchService calls this with filters and scope
- Expects AND logic for multiple filters
- Expects proper pagination support

**OwnershipScope:**
- `OWNED_ONLY` - I pass this for Customer role
- `ANY` - I pass this for Teller/Admin

---

### 4. AccountProjection
**Location:** `bank/contracts/AccountProjection.java`

```java
public interface AccountProjection {
    String getAccountId();
    String getAccountNumber();  // Raw/unmasked
    AccountType getAccountType();
    String getCustomerName();
}
```

**How I use it:**
- Received from AccountRepository
- I map these to AccountRow with masking applied

---

## Data Flow

```
GUI
 ↓
SearchController (validates auth, sanitizes input)
 ↓
SearchService (determines scope, applies masking, tracks performance)
 ↓
AccountRepository (filters data with AND logic)
 ↑
Returns AccountProjection (raw data)
 ↓
SearchService (maps to AccountRow with masking)
 ↓
Returns Page<AccountRow> (masked results)
```

---

## Filter Behavior

My `AccountSearchFilters` DTO has three optional filters:
- `accountNumber` (String)
- `customerName` (String)
- `accountType` (AccountType enum)

**How I handle them:**
- Null/blank filters are ignored (not treated as "match none")
- I pass all non-null filters to the repository
- I expect the repository to combine them with AND logic

---

## Performance Tracking

My SearchService logs performance for every search:

**Log format:**
```
[PERF] Search completed in 156ms | Results: 247 | Filters: customerName=Smith
[PERF] Search completed in 892ms | Results: 10000 | Filters: none
```

**What I measure:**
- Total time: repository call + mapping + masking
- Warns if > 2000ms

**After integration, I will:**
1. Collect these logs
2. Calculate averages/max
3. Create performance verification report (TR-03)

---

## Pagination

My `PageRequest` DTO automatically:
- Caps `size` at 50 (even if caller requests more)
- Enforces `page >= 0`

The returned `Page<AccountRow>` includes:
- `items` - list of results for this page
- `page` - current page number
- `size` - items per page
- `totalItems` - total matching items across all pages

---

## Notes

- Empty search results return an empty `Page` (not an error)
- All account numbers in results are masked according to the policy
- Customer users only see their own accounts (via OWNED_ONLY scope)
- Teller/Admin users see all accounts (via ANY scope)