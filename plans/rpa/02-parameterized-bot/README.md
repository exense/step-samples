---
use-case: rpa
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: beginner
---

# 02 — Parameterized bot (self-service RPA)

A bot a business user launches **on demand** to perform one action, supplying the inputs at
execution time. This is the self-service RPA pattern: one plan, many callers, different
inputs each run.

## What this sample shows

- **Execution parameters** — values the caller chooses when starting the run
- The **defaulting idiom**, so the same plan still runs unattended with no parameters
- **Step parameters** for centrally managed values, including `protectedValue` credentials
- How a schedule pre-fills the same values for an unattended run (see
  [06-session-and-scheduling](../06-session-and-scheduling/))

## The three ways a value reaches a plan

| Source | Declared where | Use it for |
|--------|----------------|-----------|
| Execution parameter | Chosen at execution start (UI dialog, CLI `-ep`, Maven plugin) | What the caller decides: which record, which customer |
| Step parameter | `parameters:` in `automation-package.yaml` | Credentials, endpoints, anything centrally managed |
| Schedule parameter | `schedules[].executionParameters` | The values an unattended run starts with |

All three end up as ordinary plan variables, read with `expression: "name"`. Note there is
**no** `${...}` interpolation in this YAML — see [01-linear-bot](../01-linear-bot/).

## Defaulting an execution parameter

Step declares execution parameters as plan variables **only if the caller supplied them**.
Referencing a missing one fails, so a plan that must also run unattended should default it:

```yaml
- set:
    key: recordId
    value:
      expression: "binding.variables.containsKey('recordId') ? recordId : 'REC-001'"
```

## The three calls share a session

`Login`, `Submit Record` and `Logout` sit inside a `session` block, so they run on the same
agent token. `Login` parks the application context in the keyword `session` object and the
others read it from there — which is why **no session id is passed between them in the
plan**. See [01-linear-bot](../01-linear-bot/) for the full explanation.

## Protected credentials

`botPassword` is declared with `protectedValue: true`, which masks it in the UI and in
execution reports. The plan passes it into the `Login` keyword with `expression:` — the value
never appears in the plan tree.

> The value in this sample is a dummy string so the package runs anywhere. In a real
> package, set protected parameters on the Step instance rather than committing them.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The plan, plus the `parameters:` section |
| `keywords/login.groovy` | Fails with a business error if no password arrives; parks the app context in the agent `session` |
| `keywords/submitRecord.groovy` | Reads the context from the `session`; returns a `confirmationId` |
| `keywords/logout.groovy` | Cleanup |

## Running it

With defaults:

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

With execution parameters — the self-service path:

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project> -ep recordId=REC-042 -ep amount=7500
```

In the Step UI, the same values go in the **Execution parameters** section of the execute
dialog.
