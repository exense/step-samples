---
use-case: load-testing
focus: plans
framework: none
language: groovy
target-platform: api
approach: keyword-driven
level: advanced
---

# 06 — Thresholds and SLA gates

A load test that produces a report somebody has to interpret is a load test nobody runs twice. One
that comes back PASSED or FAILED is a gate you can put in a pipeline.

**The lesson is the commented [`automation-package.yaml`](automation-package.yaml).** This page
holds the plan index and the reference tables.

> **Two plans fail on purpose** — the plan names say which.

## The plans

| Plan | Expected outcome | Shows |
|------|------------------|-------|
| A — A complete SLA gate | **PASSED** | All five aggregators, and which thresholds are worth writing |
| B — One SLA per population | **PASSED** | Per-thread-group `after` blocks in a scenario |
| C — Threshold on the failure rate | **FAILED** (on purpose) | Counting successes, and why a run with real errors is red anyway |
| D — A breached SLA | **FAILED** (on purpose) | What a violation looks like in the report |

`performanceAssert` is the control for every one of these. A second control, `assertMetric`, is
**not** a load-test gate — see the note at the end for what it is for.

## The aggregators

| Aggregator | What it is good for |
|------------|---------------------|
| `AVG` | the headline number — and the one that hides the tail |
| `MAX` | the worst single transaction; catches the timeout nobody saw |
| `MIN` | a floor — mostly used to prove the measurement is real, not empty |
| `COUNT` | how many transactions happened (completeness or throughput — see below) |
| `SUM` | total time spent; rarely a threshold |

A good gate uses several together; response time alone is always met by doing less work per unit
time. Set `continueOnError: true` on the `after` block or it stops at the first breach.

## Notes worth knowing

Selective notes, not a full reference — the commented descriptor covers every case. These are the
points most worth getting right.

### What `COUNT` means, and gating throughput

A thread group pins exactly one of the two halves of throughput (transactions ÷ elapsed time), and
that decides what a `COUNT` threshold means:

| Configuration | Pinned | A `COUNT` threshold is |
|---------------|--------|------------------------|
| fixed `iterations` | the count | a **completeness** check — same number however slow the run was |
| `iterations: 0` + `maxDuration` | the duration | a genuine **throughput** threshold |

A completeness check is still worth asserting: a keyword's count is *how often that step was
reached*, which falls short of `users` × `iterations` when an iteration fails part way, sits in an
untaken branch, or hits a dry data pool. To gate the **rate**, use the `iterations: 0` + `maxDuration`
shape from [02](../02-thread-group-configuration/) plan D.

> A throughput aggregator that works whichever half is pinned is **not available yet**; it is planned
> for `performanceAssert`. Until then, pin the duration and count — don't compute a rate in the plan.

### Where the threshold lives

| Placement | Evaluated |
|-----------|-----------|
| `after` on a **thread group** | when that population finishes — usually the best home |
| `after` on a **testScenario** | once, when every thread group has finished |
| `afterThread` | once per virtual user |

Different populations have different SLAs, so a per-population threshold reads best next to the load
it describes.

### Failure rate as a threshold

There is no failure-rate aggregator, so make the failures countable and count them:

| Number | Where it comes from |
|--------|---------------------|
| attempts | `COUNT` of the keyword measurement — every call |
| successes | `COUNT` of a measurement the keyword emits **only** on success |

A threshold on the success count is a threshold on the failure rate.
`continueParentNodeExecutionOnError: true` on the call keeps the thread group running past a failed
iteration — without it the first failure ends that virtual user and you measure a fraction of the
load.

**Why plan C is red though both thresholds pass:** real payments failed, and a failed node fails the
run. Step has no error-budget flag that greens a run with real errors — the thresholds say the
*rate* was acceptable, the status says failures happened. If a rejection is *expected* traffic, don't
raise a business error for it: return it as an output and branch on it, so the status reflects your
error budget.

### Reading the report

- A breach names the actual value: `Average of Slow Search expected to be lower than 100 but was 1672`.
- An empty series reads `No measurement is matching the defined filters.` — which covers a misspelled
  name, an instrumented-node name, and a success measurement never emitted alike.

## A note on `assertMetric`

`assertMetric` looks like an alternative to `performanceAssert`. It is not one for load testing, and
this sample deliberately leaves it out.

| | `performanceAssert` | `assertMetric` |
|---|---|---|
| Reads | the measurements **of this execution** | the stored **metric time series**, across every execution |
| Answers | *did this run meet its SLA?* | *is the system slower than it used to be?* |
| Use it for | gating a load test | cross-run trend and regression detection |

Because `assertMetric` is **not scoped to the current execution**, its aggregates fold in every past
run with the same measurement name — so on a single load test the numbers are meaningless and it
cannot serve as the gate. Its real home is **cross-execution** assertion — today's run against last
week's, an error rate creeping up over ten runs — typically in an assertion plan on a **scheduled**
execution. That is monitoring territory, covered by the monitoring samples.

So: **gate a load test with `performanceAssert`; reach for `assertMetric` only for cross-run
assertions.** (A future Step release may let `assertMetric` default to the current execution's scope,
which would make it usable here too — until then the split holds.)

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Four plans, all gated with `performanceAssert` |
| `keywords/checkout.groovy`, `searchProducts.groovy` | Transactions that meet their SLA |
| `keywords/slowSearch.groovy` | Always breaches its threshold |
| `keywords/flakyPayment.groovy` | Fails every third iteration, and emits a success measurement otherwise |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```
