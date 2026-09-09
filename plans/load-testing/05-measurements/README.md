---
use-case: load-testing
focus: plans
framework: none
language: groovy
target-platform: api
approach: keyword-driven
level: advanced
---

# 05 — Measurements

Everything a load test reports comes out of measurements. Which ones exist, what they are called
and how they nest is decided partly by Step and partly by the plan — and getting it wrong leaves a
run full of numbers that cannot answer the question you ran it to answer.

**The lesson is the commented [`automation-package.yaml`](automation-package.yaml)** and the two
measurement keywords. This page holds the plan index and the reference tables.

## The plans

| Plan | Shows |
|------|-------|
| A — Where measurements come from | The three sources side by side, and which ones carry an SLA |
| B — A custom measurement around a whole transaction | `startMeasure` / `stopMeasure`, nested |
| C — Count the failures too | Why a response-time average alone is the most misleading number in load testing |

All three are expected to pass.

## The three sources

| Source | Named after | Created by | Can carry a `performanceAssert` |
|--------|-------------|-----------|-------------------------------|
| Keyword call | the **keyword** (never the node) | Step, automatically, for every call | **yes** |
| Instrumented node | the node's `nodeName` | `instrumentNode: true` | **no** — dashboards only |
| Custom | whatever the keyword chooses | `output.startMeasure(...)` in the keyword | **yes** |

Custom measurements are the only way to time something **smaller** than a keyword call (two page
loads inside one browser keyword), and they **nest** — `stopMeasure()` closes the most recently
opened one, so one keyword can report an end-to-end journey *and* where inside it the time went.
Asserting on an instrumented-node measurement fails with `No measurement is matching the defined
filters.` — the same message a misspelled name gives, so the mistake reads as a typo. For an SLA on
a multi-step transaction, wrap it in a custom measurement (plan B), not an instrumented sequence.

## Notes worth knowing

Selective notes, not a full reference — the commented descriptor covers every case. These are the
points most worth getting right.

### Naming measurements

The name is the axis every dashboard and threshold groups by — the names **are** the report.

- **Name the business step, not the endpoint.** `Checkout`, not `POST /api/v3/order`.
- **Keep the name stable and bounded.** A name built from a variable (`Checkout for shopper 417`)
  makes one series per user and nothing aggregates. Put the varying part in an **attribute**:
  `output.stopMeasure(["region": "eu"])`.

### Measure the failures, not just the successes

An average over successful calls only is the most misleading number in load testing — when a system
starts failing fast, the average *improves*. Two habits prevent it: **assert the count** as well as
the time (every plan here carries a `COUNT`), and **check the answers** so a wrong response becomes
a failure count. [06](../06-thresholds-and-slas/) puts thresholds on that failure count.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Three plans on where measurements come from and what they are worth |
| `keywords/browseCatalog.groovy` | Two custom measurements inside one keyword call |
| `keywords/completePurchase.groovy` | A custom measurement wrapping three nested ones |
| `keywords/checkout.groovy` | A plain keyword — its measurement is created for it |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```
