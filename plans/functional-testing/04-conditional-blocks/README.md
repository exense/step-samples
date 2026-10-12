---
use-case: functional-testing
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: intermediate
---

# 04 — Conditional blocks

Branch a test on what the application returns: run a step only when it is needed, or check a
different expected value per case.

## Plans

| Plan | Shows | Expected |
|------|-------|----------|
| A - If block | Running a step only when a condition on a keyword output holds | PASSED |
| B - Switch | Running a different branch for each value of a keyword output | PASSED |

## Notes

- The `if` condition is a Groovy expression that returns a boolean. Plan A stores the boolean
  output with `expression:` so it keeps its type.
- `switch` compares its expression with the `value` of each `case` and runs the matching one.
  There is no default case: when nothing matches, nothing runs and the switch passes. Plan B
  therefore asserts first that the account type is one the cases handle.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The two plans and the keyword declarations |
| `keywords/openHomePage.groovy` | Shows the cookie banner on a first visit |
| `keywords/acceptCookies.groovy` | Closes the cookie banner |
| `keywords/getCustomer.groovy` | Returns the account type of a customer |
| `keywords/getDiscount.groovy` | Returns the discount of a customer |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

Both plans pass.
