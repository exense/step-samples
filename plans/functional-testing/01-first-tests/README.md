---
use-case: functional-testing
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: beginner
---

# 01 — First tests

The basic shape of a functional test in Step: a test case calls a keyword and asserts on its
outputs. A test set groups several test cases and reports a verdict for each of them.

## Plans

| Plan | Shows | Expected |
|------|-------|----------|
| A - First test case | `testCase` as the plan root, `callKeyword` with inputs, `assert` on two outputs | PASSED |
| B - Test set | `testSet` with two `testCase`s | PASSED |
| C - One failing test case in a test set (intentionally fails) | One test case fails, the other one still runs and passes | FAILED |

## Notes

- A **test case** has a single verdict: it fails if any of its asserts fails.
- A **test set** runs all its test cases, also after one of them has failed, and the report
  lists the verdict of each test case. The test set itself fails if any of its test cases fails.
- An `assert` is placed under the keyword call whose output it checks. `actual` names the output
  field, `expected` is compared as text, so numbers are written as strings (`"0"`).

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The three plans and the keyword declarations |
| `keywords/login.groovy` | Returns `status` and a `displayName` built from the `username` input |
| `keywords/searchProducts.groovy` | Returns `resultCount` and a `firstProduct` containing the `query` input |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

Plans A and B pass. Plan C fails on purpose: "Search returns results" fails, "Login succeeds"
passes.
