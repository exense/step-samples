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
exactly N clicks. This sample covers every thread-group knob and the four before/after blocks.

**The lesson is the commented [`automation-package.yaml`](automation-package.yaml).** This page maps
the knobs and calls out the handful that most often trip people up.

## The plans

| Plan | Shows |
|------|-------|
| A — The three counters | `gcounter`, `userId`, `literationId`, and `item` / `userItem` / `localItem` |
| B — Pacing sets the throughput | `pacing` — controlling throughput instead of concurrency |
| C — Ramping the load up | `rampup`, `pack`, `startOffset` |
| D — Run for a fixed time | `iterations: 0` + `maxDuration` |
| E — Setup per test versus per user | `before`, `beforeThread`, `afterThread`, `after` |

All five are expected to pass.

## The knobs

| Field | Meaning |
|-------|---------|
| `users` | virtual users running in parallel — each holds an agent token for the whole thread group |
| `iterations` | repetitions per user (but see the table below) |
| `pacing` | fixed period between the **starts** of consecutive iterations — makes throughput a number you choose, so two runs compare |
| `rampup` | time taken to start all the users; starting all at once is a spike test, not a load test |
| `pack` | how many users are released together at each ramp-up step |
| `startOffset` | delay before the ramp-up begins — only useful for staggering thread groups in a [scenario](../03-scenarios-and-mixed-load/) |
| `maxDuration` | wall-clock cap on the whole thread group |

## Notes worth knowing

Not a full reference — the [controls documentation](https://step.dev/knowledgebase/userdocs/plans/controls/)
and the commented descriptor cover every field. These are the ones this sample dwells on because
they are the easiest to get wrong.

### Controlling throughput — `pacing` with `users`

Define the load by `users` alone and the throughput is **uncontrolled** — it rises and falls with
response time, so no two runs compare. This is the most common mistake. `pacing` (the fixed gap
between iteration *starts*) turns throughput into a number you set:

```
iterations/sec = users ÷ pacing(s)          pacing(s) = users ÷ target-rate
```

So 1 user at 3 s pacing is 20 transactions a minute, fast system or slow. One caveat: it holds only
while each iteration finishes **within** its pacing window — if an iteration outlasts `pacing`, that
user falls behind and the rate drops back to response-time-bound. Give `pacing` headroom over the
slowest iteration, or add users.

### Running for a duration

A fixed count needs nothing special — `iterations: N`. Running for a *time* is the non-obvious one:
it needs **`iterations: 0`** (unlimited) together with `maxDuration`. `maxDuration` on its own does
**not** give a duration-bounded run, because the `iterations` default is 1:

| `iterations` | Result |
|--------------|--------|
| `0` | loops until `maxDuration` — the duration-bounded run |
| omitted | runs **once** (defaults to 1); `maxDuration` only caps that single iteration |

### The counters every thread group publishes

| Variable | Value | Renamed by |
|----------|-------|-----------|
| `userId` | which virtual user this is — 1..`users` | `userItem` |
| `literationId` | the iteration within **this** user — 1..`iterations` | `localItem` |
| `gcounter` | the iteration across the **whole** group — 1..(`users`×`iterations`), unique | `item` |

Build unique test data from these — `gcounter` when it must be unique across the run, `userId` when
it must be stable for one user. **Renaming replaces:** once `userItem: "shopperNo"` is set, `userId`
no longer exists. Coerce with `as Integer` before passing a counter to a keyword, or it arrives as a
string and `input.getInt` silently returns its default (see [06](../06-thresholds-and-slas/)).

### The four blocks

| Block | Runs |
|-------|------|
| `before` | once, before the thread group starts |
| `beforeThread` | once per virtual user, before its first iteration |
| `children` | every iteration |
| `afterThread` | once per virtual user, after its last iteration |
| `after` | once, when the whole thread group has finished |

**Put each step in the block that matches how often a real user does it.** Getting it wrong
distorts the traffic mix with no visible error: a login in `children` instead of `beforeThread`
sends one login per iteration instead of one per session. Plan E proves the placement with counts
(1 warm-up, 2 logins, 6 checkouts, 2 logouts).

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
