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

## Plans

| Plan | Shows | Expected |
|------|-------|----------|
| A - Parallel thread groups | Two user populations running at the same time | PASSED |
| B - Step ramp in three stages | Increasing the load in stages | PASSED |
| C - Background load | Measuring the application while a background job runs | PASSED |
| D - Warm-up phase | A warm-up phase before the measured phase | PASSED |

## Notes worth knowing

Selective notes, not a full reference — the commented descriptor covers every case. These are the
points most worth getting right.

### Where the SLA goes

| Placement | Evaluated |
|-----------|-----------|
| `after` on the **testScenario** | once, when every thread group has finished — the scenario-wide gate |
| `after` on a **thread group** | when that population finishes — for a threshold concerning only it |

Prefer the thread group's own block when the threshold belongs to one population.

### Patterns in the plans

- **Staged ramp** (B) — `rampup` inside one thread group ramps to a single target; a *step* profile
  (hold 1 user, then 2, then 3, comparing the steps) is several thread groups staggered with
  `startOffset`, each its own node in the report. Compare the stages in the dashboards.
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
