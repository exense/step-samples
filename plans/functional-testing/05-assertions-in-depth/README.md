---
use-case: functional-testing
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: intermediate
---

# 05 — Assertions in depth

Check exactly what is expected, test that the application refuses what it must refuse, and get
every failed check reported, not only the first one.

## Plans

| Plan | Shows | Expected |
|------|-------|----------|
| A - Assert operators | All assert operators, negation and custom error messages | PASSED |
| B - Check across keyword outputs | Checking a condition that combines the outputs of several keywords | PASSED |
| C - Negative test | Testing that the application refuses an action it must refuse, such as a login with a wrong password | PASSED |
| D - Collect all failures (intentionally fails) | Reporting every failed assertion and continuing with the next steps | FAILED |

## Notes

- **`assert` or `check`.** `assert` sits under a keyword call and checks one of its outputs; use
  it wherever possible. `check` evaluates any Groovy expression; use it when no single keyword
  output can be asserted, such as a comparison between two outputs (plan B).
- **Operators:** `EQUALS`, `BEGINS_WITH`, `ENDS_WITH`, `CONTAINS`, `MATCHES` (regular
  expression), `LESS_THAN`, `LESS_THAN_OR_EQUALS`, `GREATER_THAN`, `GREATER_THAN_OR_EQUALS` and
  `IS_NULL` (the output is missing). `doNegate: true` inverts any of them.
- **Numbers and booleans.** In the YAML, `expected` is always written as a string (`"50"`,
  `"true"`). Step converts it to the type of the output, so `expected: "50"` compares as the
  number 50 with `LESS_THAN` or `EQUALS` when the output is a number.
- **Negative tests.** A negative test passes when the application refuses the action. For that,
  the keyword reports the refusal as outputs (`status`, `errorMessage`) instead of failing, and
  the test asserts on them.
- **Collecting failures.** All asserts under a keyword call run, even after one has failed. The
  following steps are skipped, unless they are in a `sequence` with `continueOnError: true`
  (plan D).

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The four plans and the keyword declarations |
| `keywords/getProduct.groovy` | Returns the details of a product |
| `keywords/addToCart.groovy` | Returns the line total for a quantity |
| `keywords/login.groovy` | Returns `REJECTED` and an `errorMessage` for a wrong password |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

Plans A, B and C pass. Plan D fails on purpose with two failed asserts, and its "Add To Cart"
call still runs and passes.
