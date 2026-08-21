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
and how they nest is decided partly by Step and partly by the plan — and getting it wrong is how
you end up with a run full of numbers that cannot answer the question you ran it to answer.

## The plans

| Plan | Shows |
|------|-------|
| A — Where measurements come from | The three sources side by side, and which ones carry an SLA |
| B — A custom measurement around a whole transaction | `startMeasure` / `stopMeasure`, nested |
| C — Count the failures too | Why a response-time average alone is the most misleading number in load testing |

Every plan in this package is expected to pass; none of them fails by design.

## The three sources

| Source | Named after | Created by | Can carry a `performanceAssert` |
|--------|-------------|-----------|-------------------------------|
| Keyword call | the **keyword** | Step, automatically, for every call | **yes** |
| Instrumented node | the node's `nodeName` | `instrumentNode: true` | **no** |
| Custom | whatever the keyword chooses | `output.startMeasure(...)` in the keyword | **yes** |

A keyword measurement is named after the **keyword**, never the node. Two calls to `Checkout` from
different nodes land in the same series — usually what you want, occasionally not.

`instrumentNode: true` on any node times that node and reports it under its `nodeName`. This is
how a multi-keyword transaction gets an end-to-end number in the dashboards.

Custom measurements are the only way to time something **smaller** than a keyword call — the two
page loads inside one browser keyword, for instance.

## Which measurements can carry an SLA

`performanceAssert` sees keyword measurements and custom measurements. It does **not** see
instrumented-node measurements. Asserting on an instrumented sequence fails with:

```
No measurement is matching the defined filters.
```

which is the same message a misspelled keyword name produces — so the mistake reads as a typo and
gets "fixed" by changing a name that was already right.

**Instrumented nodes are for the dashboards. SLAs go on keyword or custom measurements.** To put a
threshold on a whole multi-step transaction, emit a custom measurement around it from inside the
keyword — which is what plan B does. ([06](../06-thresholds-and-slas/) shows the other route:
`assertMetric`, which *can* read instrumented nodes but has different rules.)

## Custom measurements

```groovy
output.startMeasure("Catalog page")
// ... do the work
output.stopMeasure()

output.startMeasure("Product page")
// ... do the work
output.stopMeasure(["page": "product-detail"])   // with attributes
```

Measurements **nest**: `stopMeasure()` closes the most recently opened one. Plan B's keyword wraps
three inner steps in one `Purchase journey`, so the report shows both the end-to-end journey and
where inside it the time went — and the journey, being a custom measurement, can carry the SLA
that the equivalent instrumented sequence could not.

The plan proves the nesting rather than asserting it in prose: the journey's `MIN` must exceed the
sum of its parts' sleeps, and the two page measurements in plan A are bounded on opposite sides of
the same threshold, so they cannot be the same number reported twice.

## Naming measurements

A measurement name is the axis every dashboard, threshold and comparison is grouped by. The names
**are** the report. Two rules pay for themselves:

**Name the business step, not the technical one.** `Checkout` tells you what broke.
`POST /api/v3/order` tells you where, which you can find out afterwards anyway.

**Keep the name stable and bounded.** A name built out of a variable — `Checkout for shopper 417`
— creates one series per virtual user. Nothing aggregates, the dashboards fill with noise, and no
threshold can be written against it. Put the varying part in an **attribute** instead:

```groovy
output.stopMeasure(["region": "eu"])
```

## Measure the failures, not just the successes

A response-time average computed over successful calls only is the most comfortable and most
misleading number in load testing. When a system under stress starts failing fast, **the average
improves.**

Two habits prevent it:

- **Assert the count as well as the time.** A run that did half the work has a great average.
  Every plan in this set carries a `COUNT` assertion for that reason.
- **Check the answers**, so failures are counted as failures. A nested `assert` on the keyword
  output turns a wrong response into a failure count in the report, next to the timings.

[06](../06-thresholds-and-slas/) puts thresholds on that failure count.

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
