---
use-case: load-testing
focus: plans
framework: none
language: groovy
target-platform: api
approach: keyword-driven
level: beginner
---

# 02 — Configuring a thread group

`users` and `iterations` describe a load, but not a realistic one. A real population does not
appear all at once, does not hammer the system as fast as it can answer, and does not stop after
exactly N clicks.

This sample covers every knob a thread group has, and the four blocks that decide what runs once,
what runs once per virtual user, and what runs on every iteration.

## The plans

| Plan | Shows |
|------|-------|
| A — The three counters | `gcounter`, `userId`, `literationId`, and `item` / `userItem` / `localItem` |
| B — Pacing sets the throughput | `pacing` — controlling throughput instead of concurrency |
| C — Ramping the load up | `rampup`, `pack`, `startOffset` |
| D — Run for a fixed time | `maxDuration`, and the trap that comes with it |
| E — Setup per test versus per user | `before`, `beforeThread`, `afterThread`, `after` |

All five pass.

## The knobs

| Field | Meaning |
|-------|---------|
| `users` | virtual users running in parallel — each holds an agent token for the whole thread group |
| `iterations` | repetitions per user |
| `pacing` | fixed period between the **starts** of consecutive iterations |
| `rampup` | time taken to start all the users |
| `pack` | how many users are released together at each ramp-up step |
| `startOffset` | delay before the ramp-up begins |
| `maxDuration` | wall-clock cap on the whole thread group |

### `pacing` — the field that makes runs comparable

Without pacing, a virtual user starts its next iteration the instant the previous one returns.
That is a stress test: the faster the system answers, the harder you hit it, so throughput is
whatever the system allows and two runs cannot be compared.

`pacing` makes throughput a number you choose:

```
iterations per second = users / (pacing in seconds)
```

One user with a 3000 ms pacing is 20 transactions an hour whether the system answers in 100 ms or
in two seconds. That is also what lets response time degrade *visibly* instead of being masked by
the load quietly backing off.

`sequence` has a `pacing` field too, for pacing an inner block.

### `rampup` and `pack`

`rampup` is the time the group takes to start all its users. Four users over an 8000 ms ramp-up
start at 0, 2, 4 and 6 seconds. Starting everyone at once is a spike test — a different question,
measuring the cold start rather than the steady state.

`pack` is how many users are released **together** at each step. The same four users over 8000 ms
with `pack: 2` start at 0 s and 4 s, two at a time. Use it for a stepped ramp, or simply to stop
a long ramp-up from trickling in one user at a time.

`startOffset` delays the ramp-up. On its own in a single thread group it does nothing useful; its
purpose is staggering several thread groups inside a scenario — see [03](../03-scenarios-and-mixed-load/).

### `maxDuration` and the trap in it

Most load tests are specified as "an hour at this rate", not "500 iterations". The idiom is to
set `iterations` to a number nobody will reach and let `maxDuration` be the real stop condition,
so the run takes the same time whether the system is fast or slow that day.

The trap: **the iteration count is now an outcome, not an input.** A slow system produces both a
longer response time *and* fewer iterations. An SLA that reads only response times will pass a run
that managed a tenth of the work. Always assert throughput as well:

```yaml
- performanceAssert: {measurementName: "Checkout", aggregator: COUNT,
                      comparator: HIGHER_THAN, expectedValue: 2}
```

## The counters every thread group publishes

Three variables are always in scope inside a thread group:

| Variable | Value |
|----------|-------|
| `userId` | which virtual user this is — 1..`users` |
| `literationId` | the iteration within **this** user — 1..`iterations` |
| `gcounter` | the iteration across the **whole** thread group — 1..(`users` × `iterations`), unique |

They are how you build unique test data — order numbers, e-mail addresses, search terms — with no
external data source at all. `gcounter` when the value must be unique across the run, `userId`
when it must be stable for one virtual user.

`item`, `userItem` and `localItem` rename them:

| Field | Renames |
|-------|---------|
| `item` | `gcounter` |
| `userItem` | `userId` |
| `localItem` | `literationId` |

**Renaming replaces.** Once `userItem: "shopperNo"` is set, `userId` no longer exists — worth
knowing before renaming one counter in a plan that reads another by its default name.

### Coerce the counter before passing it on

```yaml
- iteration:
    expression: "gcounter as Integer"
```

Without `as Integer` the counter reaches the keyword as a string, `input.getInt` silently returns
its default, and every iteration takes the same branch. Nothing errors; the test just stops
testing what you meant. Sample [06](../06-thresholds-and-slas/) has a keyword that reports this
case explicitly rather than absorbing it.

## The four blocks

| Block | Runs |
|-------|------|
| `before` | once, before the thread group starts |
| `beforeThread` | once per virtual user, before its first iteration |
| `children` | every iteration |
| `afterThread` | once per virtual user, after its last iteration |
| `after` | once, when the whole thread group has finished |

Getting this wrong is the most common way to make a load test lie. Logging in inside `children`
means every iteration measures a login the real user performs once a day, and the reported average
becomes a blend of two unrelated things.

**The rule of thumb: `children` holds exactly the transaction the SLA is written about.** Setup
goes in `beforeThread`; setup the whole test needs once — warming a cache, seeding a data set —
goes in `before`. `afterThread` is the reliable place for per-user cleanup: it runs even when an
iteration failed, which a last child would not.

Plan E makes all of this visible with counts rather than prose: 1 warm-up, 2 logins, 6 checkouts,
2 logouts.

A variable `set` in `before` is visible to the iterations and to the `after` block — which is how
plan B measures its own duration to prove pacing worked.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Five plans, one knob group each |
| `keywords/recordIteration.groovy` | Echoes the counters back so the plan can assert they arrived |
| `keywords/checkout.groovy` | The measured transaction |
| `keywords/login.groovy`, `logout.groovy` | Per-virtual-user setup and cleanup |
| `keywords/warmUpCache.groovy` | The once-per-test setup |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```
