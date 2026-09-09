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

> **One plan fails on purpose** — the plan name says which.

## The plans

| Plan | Expected outcome | Shows |
|------|------------------|-------|
| A — One pool feeding one thread group | **PASSED** | Declaring in `before`, `item`, `.next()` |
| B — One pool shared by two thread groups | **PASSED** | A shared cursor across parallel populations |
| C — One account per virtual user | **PASSED** | Pulling in `beforeThread` instead of the loop body |
| D — The pool runs dry | **FAILED** (on purpose) | `resetAtEnd: false` returns `null`, silently |
| E — A pool that needs no file | **PASSED** | `json-array`, and the other sources |

## `dataSet` is a declaration, not a loop

| Control | What it is |
|---------|-----------|
| `forEach` | a **loop** — runs its children once per row |
| `dataSet` | a **declaration** — opens the source, binds a **cursor** to the variable named by `item`, and runs nothing |

A `dataSet` node with children is a silent mistake: the children never execute, and the node still
reports PASSED. Instead, declare it in the **`before` block** of the node that holds the load (the
thread group stays the plan root), and pull one row with `item.next()` inside the load. In a
`testScenario`, `before` is also what removes the race — a sibling `dataSet` is not guaranteed to be
bound before parallel thread groups start pulling.

The cursor is **shared**: every `.next()` in the execution takes the following row, so populations
never collide on the same account — but *which* rows a given group gets is not deterministic. Never
assume "the buyers get shopper1".

## Notes worth knowing

Selective notes, not a full reference — the commented descriptor covers every case. These are the
points most worth getting right.

### Where you pull decides how much data you need

| Block | Runs | Pull here when… |
|-------|------|-----------------|
| `before` | once for the test | never — this is the **declaration** |
| `beforeThread` | once per virtual user | the account belongs to the user (log in once, act many times) |
| `children` | every iteration | the data belongs to the transaction |

`before` always runs before `beforeThread`, so the cursor is bound by the time a thread claims its
row. Per-virtual-user is the common case *and* the cheaper one: plan C serves 2×3 iterations from a
4-row pool by pulling twice, not six times.

### `resetAtEnd`, and running dry

With `resetAtEnd: false`, `.next()` past the last row returns **`null`** — no stop, no error — and
the run feeds `null` into the keywords. Guard the pull with a `check` on `!= null`.

| Value | Meaning |
|-------|---------|
| `true` | recycle the rows — fine when the target does not mind the same account returning |
| `false` | each row used at most once — size the pool for the whole run, and guard the pull |

### Data sources

Only the `dataSource` block changes; the pull is always `.next()`. Available: `csv`, `excel`,
`file`, `folder`, `gsheet`, `json`, `json-array`, `sequence`, `sql`. The common ones for load are
`csv` (a pool checked in beside the plan) and `sql` (read straight from the system under test);
`sequence` needs no data to exist beforehand, and for data needing no pool at all the thread-group
counters from [02](../02-thread-group-configuration/) are often enough. A credential source takes
`protect: true` to obfuscate its values in the report.

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
