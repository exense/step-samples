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
| B — Guaranteed cleanup | **FAILED** (on purpose) | `after` cleanup runs; subsequent steps in the sequence do not |
| C — Error propagation flags | **FAILED** (on purpose) | Both flags, and how they combine |
| D1 — Wait with while | **PASSED** | `condition`, `postCondition`, `pacing`, `maxIterations`, `timeout` |
| D2 — Wait with retryIfFails | **PASSED** | `retryIfFails` + a nested `assert` as the not-ready signal |
| E — Explicit business failure | **TECHNICAL_ERROR** (on purpose) | `failure` with a custom message |

## Notes on the controls

### `while` or `retryIfFails` for waiting?

Both call keywords perfectly well, and both wait. What separates them is **how you express
"not ready yet"**:

| | `while` | `retryIfFails` |
|---|---|---|
| Exit criterion | a **condition** stops holding | the block stops **failing** |
| "Not ready" is… | a normal state | an error — either a real one, or a failing `assert` you add |
| Execution report | clean — nothing failed | one failed attempt per wait (soften with `reportLastTryOnly`) |

**Prefer `while`** when the system gives you an answer you can test — *how many items are
left?*, *is the status DONE?*. The condition reads as a condition, and a long wait does not
fill the report with failures.

**Prefer `retryIfFails`** when the call genuinely **errors** until the system is ready — a
request refused while a service starts up. Then the failure is real, the retry is doing what
it was designed for, and this is also the control for absorbing flakiness (plan A).

Using `retryIfFails` purely to wait, with an `assert` added only to force a retry, works but
is a workaround: it records every wait as a failed attempt. Plan D2 shows the shape, so that
both forms appear side by side.

> On older Step versions, keyword calls nested in a `while` are not counted when forecasting
> how many agents to provision, so such a loop can fail to obtain an agent token on an
> auto-provisioned instance. Declaring `agents` on the plan avoids it.

### Retry on a fresh session, not the same one

Neither plan A nor plan D2 wraps its retry in a `session`, and that is deliberate.

A failed attempt often leaves state behind — a half-filled form, a stale selection. Retrying
inside that same session retries the mess along with the step, so a retry on a **fresh**
session is usually more likely to succeed.

The exception is when the retried block depends on something set up **before** it, such as a
login you do not want to repeat on every attempt. Then wrap the retry in a session
deliberately.

For this to be possible, **keywords must be stateless** — a keyword that remembers something
between calls forces every caller to pin to one agent. So both samples count attempts in the
*plan* and pass the number in as an input:

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

On a `sequence`, `continueOnError` means "if one of my children fails, keep running the
others" — that is what plan C uses it for. An `after` block takes the same attribute with the
same meaning, applied to the cleanup steps: it governs whether one failing cleanup step stops
the **remaining** ones. Without it, a failing logout means the licence never gets released.

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
