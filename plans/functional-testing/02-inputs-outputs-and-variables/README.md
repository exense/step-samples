---
use-case: functional-testing
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: beginner
---

# 02 — Inputs, outputs and variables

Where the input values of a keyword come from: the output of an earlier keyword, a variable, or
a value computed when the test runs.

## Plans

| Plan | Shows | Expected |
|------|-------|----------|
| A - Store an output in a variable | Keeping a keyword output in a variable and using it in a later keyword input | PASSED |
| B - Pass an output to the next keyword | Passing an output directly to the next keyword call with `previous` | PASSED |
| C - Computed inputs | Computing input values at run time: unique ids, dates, numbers | PASSED |

## Notes

- **Three ways to read an output:**

  | Binding | Holds | Readable from |
  |---------|-------|---------------|
  | `output.<field>` | The output of the keyword call it sits under | The `children` of that call only |
  | `previous.<field>` | The output of the keyword call just before | The next node only; the following keyword call replaces it |
  | a variable created by `set` | Whatever you put in it | The rest of the block. A `set` inside a keyword call stays readable after the call |

- **`${...}` or `expression:`.** `${...}` embeds a Groovy expression in a text value and always
  produces text: `"user-${runId}@test.local"`. Use `expression:` when the value must keep its
  type, such as a number computed from variables (`maxOrders` in plan C), or when the logic
  contains braces. Plain YAML numbers and booleans (`quantity: 2`) are passed with their type.
- Write regular expressions in single quotes (`'user-[0-9a-f]{8}@test\.local'`): in a
  double-quoted YAML string, `\.` is an invalid escape.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The three plans and the keyword declarations |
| `keywords/createOrder.groovy` | Returns an `orderId` built from the `customer` and `quantity` inputs |
| `keywords/getOrder.groovy` | Returns the order; fails for an unknown id |
| `keywords/cancelOrder.groovy` | Cancels the order; fails for an unknown id |
| `keywords/registerCustomer.groovy` | Echoes the inputs it received and the JSON type of `maxOrders` |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

All three plans pass.
