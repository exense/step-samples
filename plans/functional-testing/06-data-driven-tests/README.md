---
use-case: functional-testing
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: intermediate
---

# 06 — Data-driven tests

One test, many data rows: each row of a CSV file becomes its own test case, with its own
inputs, expected result and verdict.

## Plans

| Plan | Shows | Expected |
|------|-------|----------|
| A - One test case per data row | One test case per row of a CSV file, with the expected result in the data | PASSED |
| B - One failing data row (intentionally fails) | A failing row does not stop the other rows | FAILED |

## Notes

- Placing the `testCase` inside the `forEach`, under a `testSet` root, gives one test case per
  row in the report. A `forEach` inside a single test case would give one verdict for all rows.
- Each row holds the inputs and the expected result: `${row.username}` in the keyword inputs,
  `${row.expectedStatus}` in the `assert`.
- The test case name is built from the row with the `expression:` form:
  `'Login as ' + row.username + ' expecting ' + row.expectedStatus`.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The two plans and the keyword declaration |
| `data/logins.csv` | Users, passwords and expected statuses |
| `data/logins-with-wrong-expectation.csv` | The same, with one row (`expected-to-fail`) whose expected status is wrong on purpose |
| `keywords/login.groovy` | Accepts known users with the demo password |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

Plan A passes with four test cases. Plan B fails on purpose: "Login as expected-to-fail
expecting LOGGED_IN" fails, the three other test cases pass.
