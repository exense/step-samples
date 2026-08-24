# Load-testing plan samples

Six small Automation Packages, each teaching one aspect of writing a **Step plan** for load and
performance testing. Read them in order — each builds on the one before.

The subject is the **plan**, not the keywords. Every keyword here is a 3-line Groovy
`GeneralScript` stub simulating a shop API, so every package runs on any Java agent with **no
build, no browser and no system under test**. Every plan in the set is executable, and each one
asserts its own outcome rather than merely running.

## The samples

| # | Sample | Level | Controls covered |
|---|--------|-------|------------------|
| 01 | [First load test](01-first-load-test/) | beginner | `threadGroup`, `users`, `iterations`, `instrumentNode`, `performanceAssert` |
| 02 | [Configuring a thread group](02-thread-group-configuration/) | beginner | `pacing`, `rampup`, `pack`, `startOffset`, `maxDuration`, `before` / `beforeThread` / `afterThread` / `after`, the counters |
| 03 | [Scenarios and mixed load](03-scenarios-and-mixed-load/) | intermediate | `testScenario`, staged ramps, `sequence` as phases, `testSet` |
| 04 | [Test data and data sets](04-test-data-and-datasets/) | intermediate | `dataSet`, `item`, `.next()`, `resetAtEnd`, data sources |
| 05 | [Measurements](05-measurements/) | advanced | keyword / instrumented / custom measurements, `startMeasure`, naming |
| 06 | [Thresholds and SLA gates](06-thresholds-and-slas/) | advanced | aggregators, failure rate, `assertMetric` vs `performanceAssert` |

For what each control does and how to configure it, see the official
[controls documentation](https://step.dev/knowledgebase/userdocs/plans/controls/). For the YAML
shape of a standalone plan, see [../reference/](../reference/).

## Control coverage matrix

| Control | Sample |
|---------|--------|
| `threadGroup` as a plan root | 01, 02, 05, 06 |
| `users`, `iterations` | 01 |
| `pacing`, `rampup`, `pack`, `startOffset`, `maxDuration` | 02 |
| `item`, `userItem`, `localItem` and the `gcounter` / `userId` / `literationId` counters | 02 |
| `before`, `beforeThread`, `afterThread`, `after` | 02, 04 |
| `testScenario`, `sequence` and `testSet` as composing roots | 03 |
| `dataSet` + `.next()`, `resetAtEnd`, `csv` / `json-array` / `sql` | 04 |
| `instrumentNode` | 01, 05, 06 |
| `output.startMeasure` / `stopMeasure` | 05 |
| `performanceAssert` — `AVG`, `MAX`, `MIN`, `COUNT`, `SUM` | 01, 02, 03, 04, 05, 06 |
| `assertMetric` | 06 |
| `assert` inside a load test | 01, 05, 06 |
| `continueOnError`, `continueParentNodeExecutionOnError` | 01, 06 |

## Running any of them

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

Or point the Step MCP server at the directory and use `step_validate_plan` /
`step_execute_automation_package`.

Three plans are **expected to fail** — that is the lesson in them. Their names say so, and each
sample's README lists the expected outcome per plan:

| Sample | Plan |
|--------|------|
| [04](04-test-data-and-datasets/) | D — The pool runs dry |
| [06](06-thresholds-and-slas/) | C — Threshold on the failure rate |
| [06](06-thresholds-and-slas/) | E — A breached SLA |

Every other plan is expected to pass.

## Two placement rules for `performanceAssert`

Neither follows from the structure of the YAML, and both report an error that names something
other than the cause:

**`performanceAssert` must live in an `after` or `afterThread` block.** Anywhere else — as a child
of the thread group, of a `testCase`, of anything — the execution ends in `TECHNICAL_ERROR` with
`PerformanceAssert can only be defined in an 'after' or 'after thread' block`.

**`performanceAssert` cannot see `instrumentNode` measurements.** It matches keyword measurements
and custom ones a keyword created itself. Asserting on an instrumented sequence gives
`No measurement is matching the defined filters.` — the same message a misspelled keyword name
produces, so the mistake reads as a typo. [05](05-measurements/) covers the ways round it.

## What these samples teach

The controls are the vocabulary; these are the ideas that decide whether a load plan is any good.

1. **The iteration is the unit your load numbers are counted in.** Everything a thread group
   reports is per iteration, so `users: 10` with `pacing: 30000` is 20 iterations a minute — but
   20 of *what*? Choose the iteration to be the thing your requirement is stated in, usually a
   complete user action. An iteration nobody has a target for gives you a throughput figure that
   has to be divided by something before anyone can act on it.

2. **Put each step in the block that matches how often a real user does it.** A real user logs in
   once per session, so a login belongs in `beforeThread`; move it into `children` and a 2 × 3 run
   sends six logins instead of two — triple the load on the authentication service, and no error
   anywhere. Once-per-test setup goes in `before`.

3. **Pace the load, or you are not testing — you are being tested.** Without `pacing`, throughput
   is whatever the system happens to allow, so two runs cannot be compared and a degrading system
   quietly reduces its own load.

4. **Response time alone is never the whole SLA.** A struggling system meets any latency target by
   doing less work per unit time. Which threshold catches that depends on the thread group: run it
   `iterations: 0` + `maxDuration` and the duration is pinned, so a `COUNT` threshold *is* a
   throughput gate; with `iterations` fixed the count is pinned, so the same threshold only checks
   completeness. State rate requirements as `iterations: 0` duration-bounded runs.

5. **A load test still has to check its answers.** A system under stress starts returning fast,
   cheap, *wrong* responses; an error page renders quicker than a checkout. Without a functional
   `assert` in the loop, the response times look excellent and mean nothing.

6. **Real traffic is mixed.** Browsers, buyers and the nightly batch hit the system at once.
   `testScenario` composes those populations, each with its own profile — modelling them as one
   thread group produces a traffic mix nobody ever sees.

7. **Data is part of the load.** The same account on every iteration measures the target's caches.
   A `dataSet` hands out a shared pool; where you pull from it — per iteration or per virtual user
   — decides both realism and how much data you need.

8. **Names are the report.** Every dashboard, threshold and comparison groups by measurement name.
   Name the business step rather than the endpoint, and keep the name bounded — a name built from
   a variable creates one series per virtual user and nothing aggregates.

Each sample README calls out the pitfalls for its own controls — the ones that silently do
nothing, and the pairs that are easy to confuse.
