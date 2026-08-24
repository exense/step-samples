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

Real systems are never hit by one kind of user. While a few hundred people browse, a handful buy,
and at 02:00 a batch job runs straight through the middle of it. A load test that models only the
happy path measures a system nobody is using.

`testScenario` is the control that composes those populations: it runs its children **in
parallel**, each with its own load profile.

## The plans

| Plan | Shows |
|------|-------|
| A — Browsers and buyers at the same time | `testScenario` with two thread groups, `startOffset`, scenario-wide `after` |
| B — Step ramp in three stages | A staged ramp built from several thread groups |
| C — Measure the site while the batch job runs | A thread group next to a plain `sequence` |
| D — Warm-up phase then measured phase | `sequence` as the root — phases instead of parallelism |

Every plan in this package is expected to pass; none of them fails by design.

## The three composing roots

| Root | Children run | Use for |
|------|--------------|---------|
| `testScenario` | **in parallel** | mixed populations, staged ramps, load next to a batch job |
| `sequence` | **one after the other** | phases — warm-up, measured, cool-down |
| `testSet` | as separate test cases, `threads` at a time | a batch of independent tests, not a load profile |

Confusing `testScenario` with `testSet` is the usual slip. `testSet` parallelises *test cases*;
`testScenario` runs *load profiles* side by side.

## The mixed-workload shape

Each thread group keeps its own configuration — that is the whole point. Browsers are many and
cheap; buyers are few and expensive. Modelling them as one thread group doing "browse then buy"
produces a traffic mix no real shop ever sees.

```yaml
root:
  testScenario:
    children:
      - threadGroup: {nodeName: "Browsers", users: 2, iterations: 3, pacing: 1000, ...}
      - threadGroup: {nodeName: "Buyers", users: 1, iterations: 2, startOffset: 2000, ...}
```

`startOffset` on the buyers means the checkout numbers are measured against a system that is
already busy, rather than an idle one.

## Where the SLA goes

| Placement | Evaluated |
|-----------|-----------|
| `after` on the **testScenario** | once, when every thread group has finished — the scenario-wide gate |
| `after` on a **thread group** | when that population finishes — the place for a threshold that concerns only it |

Both work. Prefer the thread group's own block when the threshold belongs to one population: it
reads next to the profile it describes. [06](../06-thresholds-and-slas/) covers this in more
detail.

Assert a `COUNT` **per population**, and make each one exact. A scenario mixes populations running
different transactions, so no single aggregate describes it — and if the buyers broke off half way
through their transaction while the browsers ran in full, the totals still look plausible. Only the
per-population counts show it.

## A staged ramp

`rampup` inside one thread group gives a smooth ramp to a single target. What it cannot express is
a **step profile**: hold 1 user, then 2, then 3, each for a while, and compare the response times
between the steps. That is the shape that answers *where does it start to hurt?*

Several thread groups in a scenario, staggered with `startOffset`, is how you write it. Each stage
is its own node in the report, so each has its own numbers. In a real test each stage also carries
`maxDuration`, to hold the load for a fixed time rather than a fixed number of iterations.

A `performanceAssert` on a measurement name spans **every** node that produced it, so a COUNT over
the whole ramp is the total across all stages. To compare the stages against each other — the
reason to build a ramp this way — read them in the performance dashboards, where each stage is a
separate node; a per-stage threshold goes in that stage's own `after` block.

## Load next to a batch job

A scenario's children do not all have to be thread groups. "What do response times look like while
the nightly batch runs?" is a thread group next to a plain `sequence`. The sequence runs once, in
parallel with the load, and its duration is measured like anything else.

## Phases, and why a warm-up needs its own

Nesting a warm-up in the same thread group as the measured load pollutes the measurement with
cold-start timings. A separate warm-up **phase** keeps them in different nodes, so the threshold
applies only to the measured phase:

```yaml
root:
  sequence:
    children:
      - threadGroup: {nodeName: "Warm-up phase", ...}
      - threadGroup:
          nodeName: "Measured phase"
          after:
            steps:
              - performanceAssert: {...}     # on the measured group, not the sequence
```

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
