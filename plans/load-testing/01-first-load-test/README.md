---
use-case: load-testing
focus: plans
framework: none
language: groovy
target-platform: api
approach: keyword-driven
level: beginner
---

# 01 — First load test

The baseline shape of a load-testing plan: a thread group repeats one transaction, from several
virtual users at once, and the plan states the SLA that transaction has to meet. Every other
sample in this set builds on this structure.

**The lesson is the commented [`automation-package.yaml`](automation-package.yaml)** — read that
for the reasoning at each node. This page orients you and collects the reference tables. The
keywords are 3-line Groovy stubs simulating a shop API, so the package runs on any Java agent —
no build, no browser, no system under test.

## What it covers

- `threadGroup` as the root of a load plan, with `users` and `iterations`
- choosing what one iteration contains — the unit your load numbers are denominated in
- `instrumentNode` for an end-to-end transaction measurement
- `performanceAssert` as the SLA gate, and the two rules about where it may go
- why a load test still needs a functional `assert`

## Where measurements come from

| Measurement | Named after | Created by | Can carry a `performanceAssert` |
|-------------|-------------|-----------|-------------------------------|
| Keyword call | the **keyword** | Step, automatically, for every call | yes |
| Instrumented node | the node's `nodeName` | `instrumentNode: true` | **no** — dashboards only |
| Custom | whatever the keyword chooses | the keyword itself — see [05](../05-measurements/) | yes |

## Two rules for `performanceAssert`

1. **It must live in an `after` or `afterThread` block.** Anywhere else the run ends in
   `TECHNICAL_ERROR`: `PerformanceAssert can only be defined in an 'after' or 'after thread' block`.
   `after` runs once when the thread group finishes (run-wide SLA); `afterThread` runs once per
   virtual user.
2. **`measurementName` must name a keyword or custom measurement**, never an `instrumentNode` one —
   that fails with `No measurement is matching the defined filters.`, the same message a misspelled
   name gives. For an SLA on a multi-step transaction, emit a custom measurement — see
   [05](../05-measurements/).

Set `continueOnError: true` on the `after` block, or it stops at the first breach and hides the
rest. Bound thresholds on both sides — an upper bound alone passes when the measurement is empty.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The plan — heavily commented, this is what to read |
| `keywords/searchProducts.groovy` | Returns a product id |
| `keywords/addToCart.groovy` | Returns a cart id |
| `keywords/checkout.groovy` | Returns an order id and `CONFIRMED` |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

The report should show 6 passing transactions, 18 passing keyword calls and three passing
performance asserts.
