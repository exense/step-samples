---
use-case: load-testing
focus: plans
framework: none
language: groovy
target-platform: api
approach: keyword-driven
level: intermediate
---

# 03 — Scenarios and mixed load

Real systems are never hit by one kind of user. While a few hundred browse, a handful buy, and at
02:00 a batch job runs through the middle of it. A load test that models only the happy path
measures a system nobody is using. `testScenario` composes those populations — it runs its children
**in parallel**, each with its own profile.

**The lesson is the commented [`automation-package.yaml`](automation-package.yaml).** This page
holds the plan index and the reference tables.

## The plans

| Plan | Shows |
|------|-------|
| A — Browsers and buyers at the same time | `testScenario` with two thread groups, `startOffset`, scenario-wide `after` |
| B — Step ramp in three stages | A staged ramp built from several thread groups |
| C — Measure the site while the batch job runs | A thread group next to a plain `sequence` |
| D — Warm-up phase then measured phase | `sequence` as the root — phases instead of parallelism |

All four are expected to pass.

## The three composing roots

| Root | Children run | Use for |
|------|--------------|---------|
| `testScenario` | **in parallel** | mixed populations, staged ramps, load next to a batch job |
| `sequence` | **one after the other** | phases — warm-up, measured, cool-down |
| `testSet` | as separate test cases, `threads` at a time | a batch of independent tests, not a load profile |

Confusing `testScenario` with `testSet` is the usual slip: `testSet` parallelises *test cases*,
`testScenario` runs *load profiles* side by side.

## Notes worth knowing

Selective notes, not a full reference — the commented descriptor covers every case. These are the
points most worth getting right.

### Where the SLA goes

| Placement | Evaluated |
|-----------|-----------|
| `after` on the **testScenario** | once, when every thread group has finished — the scenario-wide gate |
| `after` on a **thread group** | when that population finishes — for a threshold concerning only it |

Prefer the thread group's own block when the threshold belongs to one population. Assert a `COUNT`
**per population** and make each exact: a scenario mixes different transactions, so no single
aggregate describes it, and one population breaking off mid-transaction still leaves the totals
looking plausible.

### Patterns in the plans

- **Staged ramp** (B) — `rampup` inside one thread group ramps to a single target; a *step* profile
  (hold 1 user, then 2, then 3, comparing the steps) is several thread groups staggered with
  `startOffset`, each its own node in the report. Compare the stages in the dashboards, not with one
  cross-stage `COUNT`.
- **Load next to a batch** (C) — a scenario child need not be a thread group; a plain `sequence`
  runs once, in parallel with the load, and is measured like anything else.
- **Phases** (D) — a warm-up nested in the measured thread group pollutes its measurement with
  cold-start timings; a separate warm-up *phase* under a `sequence` root keeps them in different
  nodes, so the threshold applies only to the measured phase.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Four plans — the composing controls |
| `keywords/searchProducts.groovy` | The browse traffic |
| `keywords/checkout.groovy` | The buy traffic |
| `keywords/runBatchReport.groovy` | The batch job that runs alongside |
| `keywords/warmUp.groovy` | The warm-up phase |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```
