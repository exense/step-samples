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

A load test that sends the same account and the same product on every iteration measures the
target's caches, not the target. Realistic load needs a pool of test data, handed out so that no
two virtual users collide.

`dataSet` is the control for that, and it does **not** work like `forEach`.

> **One plan in this package fails on purpose.** That is the lesson in it: the plan name says
> which one.

## The plans

| Plan | Expected outcome | Shows |
|------|------------------|-------|
| A — One pool feeding one thread group | **PASSED** | Declaring in `before`, `item`, `.next()` |
| B — One pool shared by two thread groups | **PASSED** | A shared cursor across parallel populations |
| C — One account per virtual user | **PASSED** | Pulling in `beforeThread` instead of the loop body |
| D — The pool runs dry | **FAILED** (on purpose) | `resetAtEnd: false` returns `null`, silently |
| E — A pool that needs no file | **PASSED** | `json-array`, and the other sources |

## `dataSet` is a declaration, not a loop

This is the one thing to understand, and it is the opposite of what the name suggests:

| Control | What it is |
|---------|-----------|
| `forEach` | a **loop**. It runs its children once per row. |
| `dataSet` | a **declaration**. It opens the data source, binds a **cursor** over it to the variable named by `item`, and runs nothing. |

A `dataSet` node with children is a common and completely silent mistake: **the children are never
executed, and the node still reports PASSED.**

## Declare the pool in a `before` block

The declaration belongs in the `before` block of whatever node contains the load — and the thread
group stays the root of the plan:

```yaml
root:
  threadGroup:
    users: 1
    iterations: 3

    before:                              # runs once, before the load starts
      steps:
        - dataSet:
            item: "shopperPool"          # names the CURSOR, not the row
            resetAtEnd: true
            dataSource:
              csv: {file: "data/users.csv"}

    children:
      - set:
          key: shopper
          value:
            expression: "shopperPool.next()"    # one pull per iteration
      - callKeyword:
          keyword: "Login"
          inputs:
            - user:
                expression: "shopper.Username"
```

`item` names the cursor. `shopperPool` is an object you call `.next()` on — it is **not** a map of
columns. `.next()` advances the cursor and returns the row as a map.

Two reasons for `before` rather than a sibling node:

- **It removes a race.** See the next section — in a `testScenario` a sibling declaration is not
  guaranteed to be bound before the load starts pulling from it.
- **It keeps the thread group at the root.** A load plan's root should be the load profile.
  Wrapping the whole thing in a `testCase` just to have somewhere to put the declaration buries
  the profile one level down for no benefit.

## One pool, several thread groups

`testScenario` runs its children **in parallel**. A `dataSet` written as a plain sibling of the
thread groups is therefore racing them: nothing guarantees the cursor is bound before the first
`.next()`, and the failure mode is a plan that works on a quiet instance and breaks on a busy one.

Put it in the scenario's `before` block, which runs to completion before any child starts:

```yaml
root:
  testScenario:
    before:
      steps:
        - dataSet: {item: "shopperPool", resetAtEnd: true, dataSource: {...}}
    children:
      - threadGroup: {nodeName: "Browsers", ...}
      - threadGroup: {nodeName: "Buyers", ...}
```

Once bound, the cursor is **shared**. Every `.next()` in the execution, from whichever thread
group, takes the *following* row — so two populations drawing from one pool never collide on the
same account, which is exactly what you want when the system under test locks a session per user.

The consequence: **which** rows a given thread group gets is not deterministic. It depends on the
order the threads happen to reach their `.next()`. Never write a plan that assumes "the buyers get
shopper1".

## Where you pull decides how much data you need

Three blocks, three frequencies:

| Block | Runs | Holds |
|-------|------|-------|
| `before` | once for the test | the **declaration** |
| `beforeThread` | once per virtual user | a pull, if the account belongs to the user |
| `children` | every iteration | a pull, if the data belongs to the transaction |

`before` is guaranteed to run before `beforeThread`, so the cursor is always bound by the time a
thread claims its row.

Per-virtual-user is the shape most load tests actually need: a virtual user logs in once and then
does twenty things as that account — it does not become a different person between two clicks.

```yaml
before:
  steps:
    - dataSet: {item: "shopperPool", resetAtEnd: false, dataSource: {...}}
beforeThread:
  steps:
    - set:
        key: myShopper
        value:
          expression: "shopperPool.next()"
    - callKeyword: {keyword: "Login", ...}
```

It is also much cheaper. Plan C runs 2 users × 3 iterations against a pool of 4 rows with
`resetAtEnd: false`. Pulling per iteration would exhaust the pool; pulling per thread uses 2 rows.
The plan proves it with counts — two logins for six orders.

## When the pool runs dry

With `resetAtEnd: false`, `.next()` past the last row does **not** stop the thread group and does
**not** raise an error. It returns `null`, and the run carries on feeding `null` into the keywords.

Depending on the keyword that is either a confusing `NullPointerException` deep in the report or —
far worse — a keyword that shrugs, sends an empty value, and keeps the run green while half the
load was meaningless.

Guard the pull:

```yaml
- check:
    nodeName: "The pool still had a row to give"
    expression: "shopper != null"
```

Decide `resetAtEnd` deliberately:

| Value | Meaning |
|-------|---------|
| `true` | recycle the rows — fine when the target does not care that the same account comes back |
| `false` | each row used at most once — then size the pool for the whole run, and guard the pull |

## Data sources

Only the `dataSource` block changes; the pull is always `.next()`. Available: `csv`, `excel`,
`file`, `folder`, `gsheet`, `json`, `json-array`, `sequence`, `sql`.

The two that come up most in load tests are `csv` — a generated pool checked in next to the plan —
and `sql`, which reads the pool straight out of the system under test:

```yaml
dataSource:
  sql:
    connectionString: "jdbc:postgresql://db:5432/shop"
    driverClass: "org.postgresql.Driver"
    user: "loadtest"
    password:
      expression: "dbPassword"
    query: "SELECT username, product_id FROM test_accounts"
```

`sequence` is the one to reach for when the data does not need to exist beforehand — a pool of
unique order numbers, say. And for data that needs no pool at all, the thread-group counters
(`gcounter`, `userId`) from [02](../02-thread-group-configuration/) are often enough.

A source holding credentials can be declared with `protect: true`, which obfuscates its values in
the report.

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
