---
use-case: rpa
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: advanced
---

# 06 — Sessions and scheduling

Two things every unattended bot needs: a **session**, so all the steps of one run land on
the same agent and can share a browser; and a **schedule**, so it runs at 03:00 without
anybody launching it.

## What this sample shows

- `session` pinning a whole bot run to one agent token
- Where to put open/close so cleanup is guaranteed — and where **not** to put it
- `agents` and `routing` for sending a bot to the right machine
- `schedules` with a Quartz cron, `cronExclusions` and `executionParameters`

## Why a session

By default each keyword call takes whatever agent token is free, so two consecutive calls
may run on **different agents**. That is fine for stateless keywords and fatal for UI
automation: the second keyword would not find the browser the first one opened.

`session` wraps a block so every keyword inside uses the same token. Anything a keyword puts
into its `session` map is then visible to the next keyword.

## Do not use the session's own `before` block for stateful setup

A keyword in a **session's** `before` block runs on a **different
agent token** than the session body, so the body cannot see what `before` put into the
session.

Put a `sequence` inside the session and use **its** `before` / `after` instead. Those steps
run within the session's token, and `after` still runs when the body fails — so cleanup is
still guaranteed:

```yaml
- session:
    children:
      - sequence:
          before:
            steps:
              - callKeyword: {keyword: "Open Browser"}
          after:
            continueOnError: true
            steps:
              - callKeyword: {keyword: "Close Browser"}
          children:
            - callKeyword: {keyword: "Use Browser"}
```

## Routing a bot to the right agent

| Level | Attribute | Selects |
|-------|-----------|---------|
| Plan | `agents: auto_detect` or `[{image: "..."}]` | Which agent **pool** the plan runs on |
| Session / keyword | `routing:` | A token by agent **attributes** |

`routing` is commented out in plan B because it needs agents carrying those attributes on
your instance — an unmatched criterion means no token is ever granted and the plan waits.

## Scheduling

```yaml
schedules:
  - name: "Nightly stateful bot"
    cron: "0 0 3 * * ?"
    planName: "A - Stateful bot in one session"
    active: false
    cronExclusions: ["0 0 0-23 ? * SUN"]
    executionParameters:
      env: "production"
```

Step uses **Quartz** cron: six fields, **seconds first**. `"0 0 3 * * ?"` is 03:00 daily,
not the five-field Unix form — this is the usual trip-up.

`executionParameters` pre-fills the same values a user would type by hand for an on-demand
run — see [02-parameterized-bot](../02-parameterized-bot/).

### A schedule needs the package deployed, not executed

`step ap execute` runs the plans a package contains and nothing else — it never creates the
schedule. Schedules exist only once the package is **deployed** into a Step project:

```bash
step ap deploy -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

So the `schedules:` block in this sample has no effect when you run the package the way the
other samples are run. To see it, deploy the package and look under **Scheduler** in the
Step UI.

The schedule ships with `active: false`, so deploying it registers the entry without arming
a recurring job on your instance. Set it to `true` when you actually want it to fire.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Two plans plus the `schedules:` section |
| `keywords/openBrowser.groovy` | Parks a browser handle in the agent session |
| `keywords/useBrowser.groovy` | Reads the handle back — fails if the token changed |
| `keywords/closeBrowser.groovy` | Cleanup |

## Running it

To run the two plans:

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

To register the package — plans, keywords **and** the schedule — in a project:

```bash
step ap deploy -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```
