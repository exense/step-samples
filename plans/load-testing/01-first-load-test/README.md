---
use-case: load-testing
focus: plans
framework: none
language: groovy
target-platform: api
approach: keyword-driven
level: beginner
---

# 01 — First load test

The baseline shape of a load-testing plan: a thread group repeats one transaction, from several
virtual users at once, and the plan states the SLA that transaction has to meet. Every other
sample in this set builds on this structure.

The subject of this sample is the **plan**. The keywords are 3-line Groovy stubs that simulate
a shop API, so the package runs on any Java agent — no build, no browser, no system under test.

## What this sample shows

- `threadGroup` as the root of a load plan, with `users` and `iterations`
- **The iteration is the unit your load numbers are denominated in** — what to put in it, and why
- `instrumentNode` for an end-to-end transaction measurement
- `performanceAssert` as the SLA gate, and **the two rules about where it may go**
- Why a load test still needs a functional `assert`

## The anatomy of a load plan

```yaml
root:
  threadGroup:
    users: 2                     # virtual users, running in parallel
    iterations: 3                # repetitions per user  ->  6 transactions
    children:
      - sequence:
          nodeName: "Search and buy"
          instrumentNode: true   # measure the transaction end to end
          children:
            - callKeyword: {...}
    after:
      continueOnError: true
      steps:
        - performanceAssert: {...}   # the SLA
```

A `threadGroup` is to a load test what a `testCase` is to a functional test or a bot: the root
that says what "one run" means. `users` sizes the load — each virtual user holds its own agent
token for the whole thread group, so it also sizes the agent capacity you need.

## What goes in one iteration

Everything a thread group reports is *per iteration*, so what you put in `children` decides what
`users`, `pacing` and throughput actually mean. Take `users: 10` with `pacing: 30000`:

| If `children` is… | …the run means |
|-------------------|----------------|
| one HTTP call | 20 HTTP calls a minute, and a 30-second pause between **every call** |
| search → add to cart → check out | 20 **purchases** a minute, and a 30-second pause between purchases |

Same numbers, different tests. The second is a load somebody can state a requirement about — *the
shop must sustain 1200 orders an hour* — and its pacing models a real user, who pauses between
purchases rather than between two clicks of the same purchase.

**Choose the iteration to be the thing your requirement is stated in.** Usually that is a complete
user action: search, add to cart, buy. Sometimes it genuinely is one call — an API whose
requirement reads *500 GET /products per second* is correctly modelled with one call per
iteration. What you want to avoid is an iteration nobody has a target for, because then the
throughput figure has to be divided by something before anyone can act on it.

Sizing a transaction this way also gives the report an end-to-end number to gate on, which is what
`instrumentNode` below is for.

## Where measurements come from

| Measurement | Named after | Created by |
|-------------|-------------|-----------|
| Keyword call | the **keyword** | Step, automatically, for every call |
| Instrumented node | the node's `nodeName` | `instrumentNode: true` on the node |
| Custom | whatever the keyword chooses | the keyword itself — see [05](../05-measurements/) |

`instrumentNode: true` on the `sequence` is what gives you an end-to-end "Search and buy" series
in the performance dashboards. Without it the report has three keyword timings and no number for
the transaction as a whole.

## `performanceAssert`: two rules about placement

`performanceAssert` compares an aggregate of a measurement against a threshold after the load is
over. It is what turns a load test from "here are some numbers, go and look" into a pass/fail
gate a pipeline can use.

```yaml
- performanceAssert:
    measurementName: "Checkout"
    aggregator: AVG            # AVG | MAX | MIN | COUNT | SUM
    comparator: LOWER_THAN     # LOWER_THAN | HIGHER_THAN | EQUALS
    expectedValue: 5000
```

**1. It must live in an `after` or `afterThread` block.** As an ordinary child — of the thread
group, of a `testCase`, anywhere — the execution ends in `TECHNICAL_ERROR` with:

```
PerformanceAssert can only be defined in an 'after' or 'after thread' block
```

| Block | Runs |
|-------|------|
| `after` | once, when the whole thread group has finished — the right place for a run-wide SLA |
| `afterThread` | once per virtual user, as that user finishes |

**2. `measurementName` must name a keyword measurement** — or a custom measurement a keyword
created itself. The measurement produced by `instrumentNode` is *not* matched. Asserting on
`"Search and buy"` fails with:

```
No measurement is matching the defined filters.
```

which is the same error a typo in a keyword name produces, so the mistake is easy to miss. To
put an SLA on a multi-step transaction, emit a custom measurement from the keyword — see
[05-measurements](../05-measurements/).

Set `continueOnError: true` on the `after` block. Without it the block stops at the first
breached threshold and you learn about one SLA violation per run instead of all of them.

### Bound both sides

The plan asserts a floor as well as a ceiling:

```yaml
- performanceAssert: {measurementName: "Checkout", aggregator: MIN,
                      comparator: HIGHER_THAN, expectedValue: 50}
```

An upper bound alone passes happily when the measurement is missing, empty, or the keyword has
been stubbed down to nothing. A floor fails the moment the transaction stops doing real work.

### `COUNT` is the cheapest check there is

`COUNT` on the transaction keyword catches a thread group that quietly did less work than you
asked for — a `maxDuration` that cut the run short, users that never started, a data source that
ran dry. Response times over a run that only did a third of the work look wonderful.

## A load test still has to check its answers

A system under load starts returning fast, cheap, **wrong** responses — an error page renders
quicker than a checkout. The nested `assert` on the checkout status runs once per iteration, so a
failure at high load appears as a failure count next to the timings:

```yaml
- assert:
    actual: "status"
    operator: EQUALS
    expected: "CONFIRMED"
```

Without it, the response times would look excellent and mean nothing.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The plan — heavily commented, this is what to read |
| `keywords/searchProducts.groovy` | Returns a product id |
| `keywords/addToCart.groovy` | Returns a cart id |
| `keywords/checkout.groovy` | Returns an order id and `CONFIRMED` |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

The execution report should show 6 passing transactions, 18 passing keyword calls and three
passing performance asserts.
