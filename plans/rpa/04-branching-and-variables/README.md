---
use-case: rpa
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: intermediate
---

# 04 — Branching and variables

An approval bot that decides what to do with each record: route it by type, auto-approve
small amounts, escalate large ones. This is where an RPA plan stops being a script and
starts encoding business rules.

## What this sample shows

- `switch` / `case` to route by document type, and the fallback pattern for values no case
  handles
- `if` for a threshold decision
- `assert` with `doNegate` and `customErrorMessage`, and `check` for plan variables
- **How `set` scoping works**, and where to declare a variable that must outlive a branch
- `skipNode` to switch a step off without deleting it

## The plans

| Plan | Shows |
|------|-------|
| A | `switch` / `case` per record type, plus the fallback pattern for unmatched values |
| B | `if` threshold routing, `set` scoping, `assert` with `doNegate` |
| C | `skipNode` on a temporarily disabled step |

## Notes on `switch`

Two behaviours worth knowing before writing one:

### The expression must be dynamic

```yaml
- switch:
    expression: "recordType"        # WRONG - the static string "recordType"
- switch:
    expression:
      expression: "recordType"      # RIGHT - reads the variable
```

The static form matches no case, so the switch executes nothing — and the plan still reports
PASSED. Same rule as everywhere else in this YAML: a plain string is a literal, never a
variable reference.

### There is no `default` case

When the expression matches nothing, the switch runs **nothing** and passes.

The fallback pattern — what plan A does — is to set a sentinel before the switch, have each
case overwrite it, and test it afterwards:

```yaml
- set: {key: routed, value: "NONE"}
- switch:
    expression: {expression: "recordType"}
    children:
      - case: {value: "INVOICE", children: [ ... , {set: {key: routed, value: "INVOICE"}}]}
      # ...
- if:
    condition: {expression: "routed == 'NONE'"}
    children:
      - set: {key: routed, value: "MANUAL"}
```

Note `routed` is declared **before** the switch — a `set` living only inside a case is
scoped to that case (see `set` scoping below).

Because both behaviours are silent, plan A ends each iteration with a `check` that the record
took the branch its type demands. Break the switch and that check goes red.

## How `set` scoping works

A variable declared by `set` belongs to **the block the `set` is in**. A `set` inside an
`if` is therefore *not* visible to that `if`'s siblings afterwards:

```yaml
- if:
    condition: {expression: "amount > 1000"}
    children:
      - set: {key: decision, value: "ESCALATED"}   # scoped to the if
- check:
    expression: "decision != 'UNDECIDED'"          # would not see it
```

Declare the variable in the outer block first, then re-set it inside the branches — that is
what plan B does.

> There is one deliberate exception: a `set` nested inside a `callKeyword` is **promoted to
> the parent scope**. That is the idiom for capturing keyword outputs — see
> [01-linear-bot](../01-linear-bot/).

## `if` conditions must be boolean

`if.condition` is a Groovy expression that has to evaluate to a boolean. Write a real
comparison (`amount > 1000`), not a bare string.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Three plans covering the branching controls |
| `keywords/readRecord.groovy` | Returns `recordType`, `amount`, `status` |
| `keywords/autoApprove.groovy` | The straight-through path |
| `keywords/escalate.groovy` | The human-approval path |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```
