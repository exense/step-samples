---
use-case: rpa
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: advanced
---

# 05 — Resilience and waiting

The heart of unattended RPA. A bot that runs at 03:00 with nobody watching has to cope with
a slow UI, a flaky click, and an upstream job that has not finished yet — without either
giving up too early or hanging forever.

> **Some plans in this package fail on purpose.** That is the lesson: look at which steps
> still ran after the failure. The plan names say which ones.

## What this sample shows

- `retryIfFails` absorbing a transient failure
- `sequence.before` / `after` for cleanup that runs even when the bot crashes
- `continueOnError` vs `continueParentNodeExecutionOnError`, side by side
- `while` vs `retryIfFails` for waiting — what actually separates them
- `failure` for aborting with your own message

## The plans

| Plan | Expected outcome | Shows |
|------|------------------|-------|
| A — Retry a flaky step | **PASSED** | `retryIfFails` absorbs two failures, passes on attempt 3 |
| B — Guaranteed cleanup | **FAILED** (on purpose) | `after` cleanup runs; the step after the failure does not |
| C — Error propagation flags | **FAILED** (on purpose) | Both flags, and how they combine |
| D1 — Wait with while | **PASSED** | `condition`, `postCondition`, `pacing`, `maxIterations`, `timeout` |
| D2 — Wait with retryIfFails | **PASSED** | `retryIfFails` + a nested `assert` as the not-ready signal |
| E — Explicit business failure | **TECHNICAL_ERROR** (on purpose) | `failure` with a custom message |

## Notes on the controls

### Calling a keyword inside `while` on auto-provisioned agents

Step forecasts how many agents an execution needs before it starts, and that forecast does
**not** account for keyword calls nested in a `while`. On an auto-provisioned setup the loop
therefore fails with

```
Not able to find any agent token matching selection criteria $agenttype=default
and accepting attributes {$tokenPartition=<execId>}
```

**Only the forecast is affected — the construct itself is fine.** With permanent agents, or
with provisioning declared on the plan, a `while` calling a keyword works normally. So if
you want the `while` form on an auto-provisioned instance, declare the agents:

```yaml
- name: "Poll until the queue is empty"
  agents:
    - replicas: 1
      pool: "<your-agent-pool>"
```

This is a provisioning limitation, not a reason to avoid `while`. Plan D2 uses
`retryIfFails` simply because it needs no pool name and therefore runs on any instance:

```yaml
- retryIfFails:
    maxRetries: 10
    gracePeriod: 500
    timeout: 60000
    children:
      - callKeyword:
          keyword: "Check Queue Size"
          children:
            - assert: {actual: "queueSize", operator: EQUALS, expected: "0"}
```

### `while` or `retryIfFails` for waiting?

Both call keywords perfectly well, and both wait. What separates them is **how you express
"not ready yet"**:

| | `while` | `retryIfFails` |
|---|---|---|
| Exit criterion | a **condition** stops holding | the block stops **failing** |
| "Not ready" is… | a normal state | a failure you have to fabricate (usually a failing `assert`) |
| Execution report | clean — nothing failed | one failed attempt per wait (soften with `reportLastTryOnly`) |

**Prefer `while`** when the system gives you an answer you can test — *how many items are
left?*, *is the status DONE?*. The condition reads as a condition, and a long wait does not
fill the report with failures.

**Prefer `retryIfFails`** when the thing you are waiting on genuinely **fails** rather than
reporting a state — a call that errors until the service is up. That is retry, not polling,
and it is also what you want for absorbing flakiness (plan A).

### Retry on a fresh session, not the same one

Neither plan A nor plan D2 wraps its retry in a `session`, and that is deliberate.

A retry is usually better on a **fresh** session — a new agent token, a new browser, a clean
slate. Half the reason a step is flaky is state left behind by the attempt that just failed;
retrying inside the same dirty session retries the problem along with the step.

Wrap a retry in a session only when the retried block genuinely depends on something
established **earlier and outside it** — typically a login you do not want to redo on every
attempt. That is a deliberate trade-off, not the default.

This has a consequence for the keywords: **keep them stateless.** Both samples count
attempts in the *plan* and pass the number in as an input:

```yaml
- set: {key: attempt, value: {expression: "0"}}     # a NUMBER, not "0"
- retryIfFails:
    children:
      - set:
          key: attempt
          value:
            expression: "attempt + 1"
      - callKeyword:
          keyword: "Flaky Step"
          inputs:
            - attempt:
                expression: "attempt"
```

Declaring the counter with `expression:` makes it a real number, so the arithmetic reads as
arithmetic. A plain `value: "0"` would be the *string* `"0"`, and every use would need
`.toInteger()` / `.toString()` around it.

Re-setting a variable declared outside the block updates *that* variable, so the count
survives across attempts. A keyword that remembered the count itself would force every
caller to pin to one agent — which is how you end up needing a session you did not want.

### `continueOnError` on an `after` block

It governs whether one failing cleanup step stops the **remaining** cleanup steps — the same
"keep going" semantics as on a sequence, applied to the cleanup list. Without it, a failing
logout means the licence never gets released.

It does **not** suppress or hide anything: both the body error and the cleanup error are
reported either way. And a failing cleanup step fails the run even when the body passed, so
keep cleanup steps defensive.

### The two error flags are complementary, not alternatives

- `continueOnError` on a **container** keeps the **inside** of the block going.
- `continueParentNodeExecutionOnError` on a **child** keeps the **outside** going.

Plan C needs both: `continueOnError` lets the first sequence finish its own children, but
that sequence still ends up failed — and a failed child stops its parent. Without
`continueParentNodeExecutionOnError` on it too, the test case would stop there and the
second demo would never run.

Neither flag hides the error. The run still reports as failed, which is usually what an RPA
batch wants: finish the remaining work, but report the failure.

### `failure` reports TECHNICAL_ERROR

A `failure` node aborts the run with your message attached, but the status is
`TECHNICAL_ERROR`, not `FAILED`. For a run that comes out as `FAILED`, express the rule as a
`check`, or raise `output.setBusinessError(...)` from inside a keyword.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Six plans covering the resilience controls |
| `keywords/flakyStep.groovy` | Stateless: fails when the `attempt` input is below 3 |
| `keywords/checkQueueSize.groovy` | Stateless: returns `3 - polls`, so the queue drains as polling goes on |
| `keywords/alwaysFails.groovy` | Always raises a business error |
| `keywords/login.groovy`, `logout.groovy` | Used by the `before` / `after` cleanup demo |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```
