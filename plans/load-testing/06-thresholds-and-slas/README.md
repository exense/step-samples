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

A load test that produces a report somebody has to interpret is a load test nobody runs twice. A
load test that comes back PASSED or FAILED is a gate you can put in a pipeline.

This sample covers the two threshold controls, what each can and cannot see, and the thresholds a
load test needs beyond "average response time".

> **Two plans in this package fail on purpose.** That is the lesson in them: the plan names say
> which ones.

## The plans

| Plan | Expected outcome | Shows |
|------|------------------|-------|
| A — A complete SLA gate | **PASSED** | All five aggregators, and which thresholds are worth writing |
| B — One SLA per population | **PASSED** | Per-thread-group `after` blocks in a scenario |
| C — Threshold on the failure rate | **FAILED** (on purpose) | Counting successes, and why a run with real errors is red anyway |
| D — A threshold over the metric time series | **PASSED** | `assertMetric`, and how it differs from `performanceAssert` |
| E — A breached SLA | **FAILED** (on purpose) | What a violation looks like in the report |

## The aggregators

| Aggregator | What it is good for |
|------------|---------------------|
| `AVG` | the headline number — and the one that hides the tail. An average of 800 ms is compatible with one user in twenty waiting eight seconds. |
| `MAX` | the worst single transaction. Brutal, and useful precisely because it is: a MAX threshold catches the timeout nobody saw. |
| `MIN` | a floor. Mostly used to prove the measurement is real rather than empty. |
| `COUNT` | how many transactions happened. With `iterations` fixed this is a **completeness** check, not a throughput one — see below. |
| `SUM` | total time spent. Rarely a threshold; useful for capacity sums. |

**A good gate uses several together.** Response time on its own can always be met by doing less
work per unit time — which is exactly what a struggling system does.

## What `COUNT` tells you, and how to gate throughput

Throughput is transactions ÷ elapsed time, and a thread group fixes exactly one of the two. That
decides what a `COUNT` threshold on it actually means:

| Configuration | Pinned | A `COUNT` threshold is |
|---------------|--------|------------------------|
| fixed `iterations` | the count | a **completeness** check — the same number however slow the run was |
| `iterations: 0` + `maxDuration` | the duration | a genuine **throughput** threshold |

Plan A fixes `iterations`, so its `COUNT` of 6 confirms the work was done but says nothing about
the rate.

It is still worth asserting, because a keyword's count is **how often that step was reached**, not
`users` × `iterations`. It falls short when an iteration fails part way — the steps after the
failure never run — when the step sits inside an `if` or `switch` that was not always taken, or
when a data pool ran dry. On a single-keyword iteration like plan A's the two numbers coincide; on
the multi-step transactions in [01](../01-first-load-test/) and [05](../05-measurements/) they do
not, and comparing counts across the steps of one transaction is how you find where iterations were
breaking off.

**To gate throughput, bound the duration and count**, which is [02](../02-thread-group-configuration/)
plan D's shape:

```yaml
threadGroup:
  users: 1
  iterations: 0            # unlimited: loop until maxDuration
  maxDuration: 5000        # the real stop condition
  pacing: 1000
  after:
    steps:
      - performanceAssert: {measurementName: "Checkout", aggregator: COUNT,
                            comparator: HIGHER_THAN, expectedValue: 2}
```

`iterations: 0` is the loop-for-a-duration form; a fixed count would pin the count instead of the
duration, and omitting `iterations` runs a single time. This is also how load requirements are
usually written — *an hour at this rate* — so it is rarely a
compromise.

> A throughput aggregator that works whichever of the two is pinned is **not available yet**; it is
> planned for `performanceAssert` in a future release. Until then, express a throughput requirement
> as a duration-bounded run with a `COUNT` threshold, rather than computing a rate inside the plan.

Set `continueOnError: true` on the `after` block. Without it the block stops at the first breach,
and a run that violates three thresholds tells you about one of them. Plan E shows the payoff: two
breaches reported in one run, plus a third threshold that passed.

## Where the threshold lives

| Placement | Evaluated |
|-----------|-----------|
| `after` on a **thread group** | when that population finishes |
| `after` on a **testScenario** | once, when every thread group has finished |
| `afterThread` | once per virtual user |

Different populations have different SLAs — search may have to answer in under a second while a
checkout is allowed three. Putting both in the scenario's `after` block works, but the threshold
then sits far from the load it describes. Each thread group's own `after` block is usually the
better home.

## Failure rate as a threshold

"Under 1% errors" is part of every real SLA, and it is not a response time. There is no
failure-rate aggregator, so the pattern is to make the failures countable and then count them:

| Number | Where it comes from |
|--------|---------------------|
| attempts | `COUNT` of the keyword measurement — every call, successful or not |
| successes | `COUNT` of a measurement the keyword emits **only** on success |

```groovy
// in the keyword, on the success path only
output.startMeasure("Payment accepted")
output.stopMeasure()
```

Six attempts, two of which fail, leaves four successes — so a threshold on the success count is a
threshold on the failure rate.

`continueParentNodeExecutionOnError: true` on the keyword call is what keeps the thread group
running past a failed iteration. Without it the first failure ends that virtual user, and the run
measures a fraction of the load you asked for — the opposite of what you want when the question is
*how often does it fail?*

### Why plan C is red even though both thresholds pass

Two payments genuinely failed, and a failed node fails the run. Step has no error-budget flag that
makes a run with real errors come back green. The thresholds tell you the failure *rate* was
acceptable; the run status tells you failures happened. Both are true and both are worth
reporting.

If a rejection is **expected traffic** rather than an error — a payment the business means to
decline — do not raise a business error for it. Return it as an ordinary output and branch on it in
the plan, and the run status then reflects your error budget rather than the provider's mood.

## `performanceAssert` vs `assertMetric`

They look similar and answer different questions.

| | `performanceAssert` | `assertMetric` |
|---|---|---|
| Reads | the measurements **of this execution** | the **metric time series** |
| Scope | this run only | **not scoped to this run** — the series spans every execution |
| Sees instrumented-node measurements | no | **yes** |
| Placement | must be in `after` / `afterThread` | anywhere |
| Answers | *did this run meet its SLA?* | *is the system drifting?* |

```yaml
- assertMetric:
    metric: "response-time"          # required
    aggregation: AVG                 # required
    comparator: LOWER_THAN
    expectedValue: 60000
    filters:
      - field: "name"                # `name` selects a measurement name
        filterType: EQUALS
        filter: "Search transaction"
```

Two things to know before using `assertMetric` as a gate:

1. **It is not scoped to the current execution.** A `COUNT` over a measurement name this run
   produced twice comes back with every point the series holds, from every run that ever used the
   same name. Count assertions are meaningless here — use `performanceAssert` for those.
2. **It reaches measurements `performanceAssert` cannot**, including instrumented-node
   measurements, and it can read gauges and counters a keyword pushed. Together with
   `slidingWindow`, that is its real use: thresholds over *time* — is the response time drifting up
   across the last ten minutes, across yesterday's run and today's — which is what monitoring and
   alerting rules are built out of.

Note `field: "name"` and not `attributes.name`; the latter matches nothing and reports
`No metric found for the defined filters`.

## What a breach looks like

```
Average of Slow Search expected to be lower than 100 but was 1672
Max of Slow Search expected to be lower than 200 but was 2894
```

The control reports the **actual value** alongside the threshold, so a breached SLA tells you how
far off it was without opening the dashboards.

An empty series reads differently:

```
No measurement is matching the defined filters.
```

That is what you get from a misspelled measurement name, from an instrumented-node name, and from
a success measurement that was never emitted — three quite different problems with one message.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Five plans covering both threshold controls |
| `keywords/checkout.groovy`, `searchProducts.groovy` | Transactions that meet their SLA |
| `keywords/slowSearch.groovy` | Always breaches its threshold |
| `keywords/flakyPayment.groovy` | Fails every third iteration, and emits a success measurement otherwise |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```
