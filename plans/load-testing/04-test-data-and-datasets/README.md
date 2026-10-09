---
use-case: load-testing
focus: plans
framework: none
language: groovy
target-platform: api
approach: keyword-driven
level: intermediate
---

# 04 — Test data and data sets

A load test that sends the same account and product on every iteration measures the target's
caches, not the target. Realistic load needs a pool of test data, handed out so that no two virtual
users collide. `dataSet` is the control for that — and it does **not** work like `forEach`.

**The lesson is the commented [`automation-package.yaml`](automation-package.yaml)**, which walks
each shape at its node. This page holds the plan index and the reference tables.

## The plans

| Plan | Expected outcome | Shows |
|------|------------------|-------|
| A — One pool feeding one thread group | **PASSED** | Declaring in `before`, `item`, `.next()` |
| B — One pool shared by two thread groups | **PASSED** | A shared cursor across parallel populations |
| C — One account per virtual user | **PASSED** | Pulling in `beforeThread` instead of the loop body |
| D — The pool runs dry | **FAILED** (on purpose) | `resetAtEnd: false` returns `null`, silently |
| E — A pool that needs no file | **PASSED** | `json-array`, and the other sources |

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Five plans covering the data-set mechanics |
| `data/users.csv` | The pool — four accounts, each with a product |
| `keywords/login.groovy` | Echoes the account back so the plan can prove the row arrived |
| `keywords/placeOrder.groovy` | Echoes the product back |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```
