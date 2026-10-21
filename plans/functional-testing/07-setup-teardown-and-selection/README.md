---
use-case: functional-testing
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: intermediate
---

# 07 — Setup, teardown and test selection

Create the test data a test needs, remove it afterwards even when the test fails, and run only a
subset of the tests.

## Plans

| Plan | Shows | Expected |
|------|-------|----------|
| A - Shared setup and cleanup for a test set | Test data created once for all test cases of a set and removed at the end | PASSED |
| B - Setup and cleanup for a test case | Test data created and removed for a single test case | PASSED |
| C - Cleanup after a failure (intentionally fails) | Cleanup that runs even when the test fails | FAILED |

## Notes

- `before` runs before the children, `after` runs after them, also when they fail. With
  `continueOnError: true`, the remaining cleanup steps run even if one of them fails.
- A variable set in `before` is visible to the test cases and to `after`.
- Setup on the test set is shared by its test cases. Setup on the test case keeps each test
  independent of the others, which matters when test cases run in parallel or alone.
- **Selecting tests.** `categories` are set on plans. `--includeCategories` and
  `--excludeCategories` select the plans to run:

  ```bash
  step ap execute -p . ... --includeCategories smoke
  ```

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The three plans and the keyword declarations |
| `keywords/createTestCustomer.groovy` | Creates a test customer and returns its id |
| `keywords/deleteTestCustomer.groovy` | Deletes it; fails without a valid id |
| `keywords/addToCart.groovy` | Creates a cart for the customer |
| `keywords/clearCart.groovy` | Empties the cart |
| `keywords/placeOrder.groovy` | Checks out the cart |
| `keywords/login.groovy` | Logs in with the customer id |
| `keywords/getOrderHistory.groovy` | Returns the number of orders |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

Plans A and B pass. Plan C fails on purpose: its login assert fails, and the cleanup in `after`
still deletes the customer.
