---
use-case: rpa
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: advanced
---

# 07 — Composition and reuse

Once you have more than one bot, the same sub-process shows up everywhere: *log in*, *look
up a customer*, *book an entry*. This sample shows the three ways Step lets you package a
piece of plan once and call it from many bots.

## What this sample shows

- A **Composite keyword** — a plan that behaves like a keyword, with inputs and outputs
- `callPlan` to delegate to another whole plan
- `testSet` to run several bots as one orchestrated batch
- `synchronized` to serialise access to a single shared licence

## The three reuse mechanisms

| Mechanism | Returns outputs? | Use it when |
|-----------|------------------|-------------|
| **Composite keyword** | Yes, via `return` | The thing you are reusing is a *step* inside someone else's plan. **Reach for this first** |
| `callPlan` | No — it produces its own report branch | The thing you are reusing is a whole bot in its own right, with its own schedule |
| `testSet` | n/a | You want to run several independent bots as one batch |

## The Composite keyword

A Composite's implementation is a **plan**, declared under `keywords:`:

```yaml
keywords:
  - Composite:
      name: "Process One Record"
      plan:
        root:
          sequence:                          # a sequence, not a testCase
            children:
              - callKeyword:
                  keyword: "Read Record"
                  inputs:
                    - recordId:
                        expression: "input.recordId"    # the composite's own inputs
              # ...
              - return:
                  output:
                    - confirmationId:
                        expression: "confirmationId"
```

The caller invokes it exactly like any other keyword, and reads its `return` values with
`output.*`.

Two details that are easy to get wrong:

- The root is a **`sequence`**, not a `testCase` — a composite is a step inside someone
  else's plan, not a test case of its own.
- `return.output` values need `expression:`, like every other value in this YAML. There is
  no string interpolation — `"${confirmationId}"` would hand the caller that literal string.
  See [01-linear-bot](../01-linear-bot/).

## `forEach threads` vs `testSet threads`

- `threads` on a **loop** parallelises **rows** of one bot.
- `threads` on a **`testSet`** parallelises **whole bots**.

## A plan that is called is still a plan

`Shared - Archive processed records` exists to be called by plan B, but nothing marks it as
"a sub-plan" — so it is also executed on its own when the whole package runs. That is why it
defaults its input: a plan meant to be called by others should still stand up when launched
directly.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Four plans plus the Composite keyword definition |
| `keywords/readRecord.groovy` | Returns `amount`, `status` |
| `keywords/submitRecord.groovy` | Returns a `confirmationId` |
| `keywords/reserveLicence.groovy` | Stands in for the single-licence legacy app |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```
