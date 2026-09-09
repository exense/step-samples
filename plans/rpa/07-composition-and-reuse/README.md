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
up a customer*, *book an entry*. This sample shows the two ways Step lets you package a piece
of plan once and call it from many bots — plus the controls for running several bots
together and for keeping them out of each other's way.

## What this sample shows

- A **Composite keyword** — a plan that behaves like a keyword, with inputs and outputs
- `callPlan` to delegate to another whole plan
- `testSet` to run several test cases from one plan, several at a time
- `synchronized` to serialise access to a single shared licence

## The two reuse mechanisms

| Mechanism | Use it when |
|-----------|-------------|
| **Composite keyword** | The reused part has to **return outputs**; or it needs a **declared input schema**; or you want it to appear in reports **like any other keyword call** |
| `callPlan` | The reused part is a complete plan that also runs on its own. It returns nothing to the caller and produces its own branch in the report |

`testSet` also appears in this sample, but it is not a reuse mechanism — see
[below](#testset-running-several-test-cases).

Values passed through `callPlan.input` reach the called plan under **`input.`** — the same
convention a Composite keyword uses. A bare `archiveFolder` is not bound:

```yaml
- callPlan:
    selectionAttributes:
      - name: "Shared - Archive processed records"
    input:
      - archiveFolder: "/archive/2026-08"
```

```yaml
# in the called plan
- echo:
    text:
      expression: "'Archiving into ' + input.archiveFolder"
```

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
`output.*`. That sameness is the point — it is why a Composite is the right choice when the
reused part:

- **has to return outputs** — `return` hands them back, and the caller reads them with
  `output.<field>` exactly as for a Groovy or Java keyword;
- **needs a declared input contract** — `schema` states which inputs it takes and which are
  required, so callers do not have to read the plan to find out;
- **should report like a keyword** — one node in the execution tree with its internals nested
  beneath it, rather than a separate report branch.

The input schema is ordinary JSON Schema:

```yaml
- Composite:
    name: "Process One Record"
    schema:
      type: object
      properties:
        recordId:
          type: string
      required:
        - recordId
    plan:
      # ...
```

Two details that are easy to get wrong:

- The root is a **`sequence`**, not a `testCase` — a composite is a step inside someone
  else's plan, not a test case of its own.
- `return.output` values need `expression:`, like every other value in this YAML. There is
  no string interpolation — `"${confirmationId}"` would hand the caller that literal string.
  See [01-linear-bot](../01-linear-bot/).

## `testSet`: running several test cases

`testSet` is not about reuse. It is a control that runs its children — usually `testCase`
nodes — as separate test cases, `threads` of them at a time. Plan C uses it for the "nightly
batch" shape: three independent bots in one plan and one report.

Note what `threads` means in each place:

- `threads` on a **loop** parallelises **row processing** within one test case.
- `threads` on a **`testSet`** parallelises its **test cases**.

## Keeping a sub-plan out of a normal run

`Shared - Archive processed records` exists to be called by plan B. It expects
`archiveFolder` from its caller, so running it on its own fails — and by default, executing
the package runs every plan in it, including this one.

Mark it with a category and exclude that category at execution:

```yaml
- name: "Shared - Archive processed records"
  categories: ["RPA", "sub-plan"]
```

```bash
step ap execute -p . --excludeCategories=sub-plan
```

Categories are only labels — they change nothing about how `callPlan` reaches the plan, so
plan B still works. This is cleaner than giving the plan a default value for an input it
should really require: a default that exists only to keep a standalone run green hides a
missing input rather than reporting it.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Four plans plus the Composite keyword definition |
| `keywords/readRecord.groovy` | Returns `amount`, `status` |
| `keywords/submitRecord.groovy` | Returns a `confirmationId` |
| `keywords/reserveLicence.groovy` | Stands in for the single-licence legacy app |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project> --excludeCategories=sub-plan
```

`--excludeCategories=sub-plan` leaves out `Shared - Archive processed records`, which is
meant to be reached through `callPlan` rather than run on its own. Without the flag that
plan is executed too, and fails for want of the `archiveFolder` its caller supplies.
