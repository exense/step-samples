# RPA plan samples

Seven small Automation Packages, each teaching one aspect of writing a **Step plan** for
RPA. Read them in order — each builds on the one before.

The subject is the **plan**, not the keywords. Every keyword here is a 3-line Groovy
`GeneralScript` stub simulating a back-office application, so every package runs on any Java
agent with **no build, no browser and no external system**. Every plan in the set is
executable, and each one asserts its own outcome rather than merely running.

## The samples

| # | Sample | Level | Controls covered |
|---|--------|-------|------------------|
| 01 | [Linear bot](01-linear-bot/) | beginner | `testCase`, `session`, `callKeyword`, `set`, `echo`, `assert`, `check`, `sleep` |
| 02 | [Parameterized bot](02-parameterized-bot/) | beginner | execution parameters, `parameters` (`protectedValue`), defaulting |
| 03 | [Data-driven loops](03-data-driven-loops/) | intermediate | `forEach`, `for`, `item`, `threads`, `maxFailedLoops`, data sources, `script` write-back |
| 04 | [Branching and variables](04-branching-and-variables/) | intermediate | `if`, `switch`/`case`, `assert` operators, `set` scope, `skipNode` |
| 05 | [Resilience and waiting](05-resilience-and-waiting/) | advanced | `retryIfFails`, `before`/`after`, `continueOnError`, `while`, `failure` |
| 06 | [Sessions and scheduling](06-session-and-scheduling/) | advanced | `session`, `routing`, `agents`, `schedules` |
| 07 | [Composition and reuse](07-composition-and-reuse/) | advanced | `Composite` + `return`, `callPlan`, `testSet`, `synchronized` |

For what each control does and how to configure it, see the official
[controls documentation](https://step.dev/knowledgebase/userdocs/plans/controls/). For the YAML shape of a standalone plan, see
[../reference/](../reference/).

## Control coverage matrix

| Control | Sample |
|---------|--------|
| `testCase` / `sequence` | 01 |
| `callKeyword` + input/output chaining | 01 |
| `set`, `echo`, `check`, `assert`, `sleep` | 01, 04 |
| execution parameters, `parameters` | 02 |
| `forEach` + `csv` / `json-array` / `sequence` / `folder` / `sql` | 03 |
| `for`, `item`, `threads`, `maxFailedLoops` | 03 |
| `script` + `row.put(...)` write-back | 03 |
| `if`, `switch` / `case`, `skipNode` | 04 |
| `retryIfFails` | 05 |
| `before` / `after` | 05, 06 |
| `continueOnError`, `continueParentNodeExecutionOnError` | 05 |
| `while`, `failure` | 05 |
| `session` | 01, 02, 06 |
| `routing`, `agents`, `schedules` | 06 |
| `Composite` + `return`, `callPlan`, `testSet`, `synchronized` | 07 |

## Running any of them

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

Or point the Step MCP server at the directory and use `step_validate_plan` /
`step_execute_automation_package`.

`execute` runs the plans and nothing else. To register a package — plans, keywords,
schedules and parameters — in a project, **deploy** it instead. That matters for
[06](06-session-and-scheduling/), whose `schedules:` block only takes effect on deploy:

```bash
step ap deploy -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

Three plans in [05](05-resilience-and-waiting/) are **expected to fail** — that is the lesson
in them. Their names say so, and that sample's README lists the expected outcome per plan.

[07](07-composition-and-reuse/) contains a plan meant to be reached through `callPlan` rather
than run directly. It is tagged with a `sub-plan` category, so exclude it:

```bash
step ap execute -p . --excludeCategories=sub-plan
```

Everything else passes.

## What these samples teach

The controls provide the vocabulary; the following principles determine whether a plan is
well-designed:

1. **The plan orchestrates, the keyword acts.** The plan carries *business* data — the
   record, the amount, the confirmation. Technical context — a browser, a driver, a
   logged-in connection — lives in the keyword's `session` object and never appears in the
   plan. Getting that boundary right is most of what makes a plan readable.

2. **One run is one business transaction.** A `testCase` root models a single unit of work
   the business would recognise, which is what turns an execution report into an audit
   trail rather than a log.

3. **RPA is data-driven by default.** The usual shape is `forEach` over a work list, one
   transaction per row — `threads` to scale it, `maxFailedLoops` so one bad record does not
   strand the other 999.

4. **Unattended means resilience is designed, not hoped for.** Nobody is watching at 03:00:
   absorb transient failures with `retryIfFails`, put cleanup in an `after` block so it
   survives a crash, and give every wait both `maxIterations` and `timeout`.

5. **Business rules belong in the plan.** Routing and thresholds written as `if` and
   `switch` are visible in the report and changeable without touching keyword code — which
   is the reason to use a plan at all instead of one large script.

6. **Values come from outside the plan.** The same bot serves a person on demand
   (execution parameters) and a nightly `schedules` entry, with credentials held in
   protected `parameters` rather than written into the tree.

7. **Close the loop.** Writing the outcome back to the source — so a re-run skips what is
   done, and the business sees the confirmation next to its record — is much of what
   separates a bot from a script.

8. **Reuse sub-processes as Composite keywords.** Once there is more than one bot, "log in"
   and "look up a customer" want to be callable units with inputs and outputs, not copied
   blocks.

Each sample README calls out the pitfalls for its own controls — the ones that silently do
nothing, and the pairs that are easy to confuse.
