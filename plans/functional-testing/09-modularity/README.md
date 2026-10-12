---
use-case: functional-testing
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: advanced
---

# 09 — Modularity

Reuse a business flow instead of copying its steps: as a composite keyword that other tests call
like any keyword, or as a complete plan that a test suite calls.

## Plans

| Plan | Shows | Expected |
|------|-------|----------|
| A - Composite keyword | Packaging several keyword calls as one reusable keyword with inputs and outputs | PASSED |
| B - Call plans from a test suite | Building a test suite from existing plans | PASSED |
| Customer can log in | Test plan reused by plan B; also runs on its own | PASSED |
| Customer can search the catalogue | Test plan reused by plan B; also runs on its own | PASSED |

## Notes

- **Composite keyword or `callPlan`:**

  | | Composite keyword | `callPlan` |
  |---|---|---|
  | Reused part | A sequence of keyword calls | A complete plan |
  | Inputs | Declared in `schema`, read as `input.<name>` | Optional `input` list |
  | Outputs | Returned with `return`, asserted by the caller | None |
  | Report | One keyword call, with its inner calls below it | The called plan's own branch |

- `callPlan` selects the plan by name: `plan: "Customer can log in"`.
- A failing called plan fails the `callPlan` node and the caller. In a test set, the other
  called plans still run.
- The called plans are ordinary test plans, so they keep names that describe the tested
  behaviour, like any test case.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The four plans, the composite keyword and the keyword declarations |
| `keywords/login.groovy` | Logs in a user |
| `keywords/addToCart.groovy` | Creates a cart for the user |
| `keywords/checkout.groovy` | Returns an order id built from the cart |
| `keywords/searchProducts.groovy` | Returns products matching the query |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

All four plans pass.
