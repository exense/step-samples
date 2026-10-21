# Functional testing plan samples

Nine small Automation Packages, each teaching one aspect of writing a **Step plan** for
functional testing. Read them in order — each builds on the one before.

The subject is the **plan**, not the keywords. Every keyword here is a 3-line Groovy
`GeneralScript` stub simulating a small web shop, so every package runs on any Java agent with
**no build, no browser and no external system**. Every plan asserts its own outcome.

These samples use Automation Package schema **1.3.0** (Step 31), including the `${...}` string
interpolation it introduces.

## The samples

| # | Sample | Level | Controls covered |
|---|--------|-------|------------------|
| 01 | [First tests](01-first-tests/) | beginner | `testCase`, `testSet`, `callKeyword`, `assert` |
| 02 | [Inputs, outputs and variables](02-inputs-outputs-and-variables/) | beginner | `set`, `output.`, `previous.`, `${...}`, `expression:` |
| 03 | [Parameters and environments](03-parameters-and-environments/) | intermediate | `parameters`, `activationScript`, `priority`, `protectedValue`, execution parameters, keyword properties |
| 04 | [Conditional blocks](04-conditional-blocks/) | intermediate | `if`, `switch` / `case` |
| 05 | [Assertions in depth](05-assertions-in-depth/) | intermediate | `assert` operators, `doNegate`, `customErrorMessage`, `check`, `continueOnError` |
| 06 | [Data-driven tests](06-data-driven-tests/) | intermediate | `forEach` + `csv`, one `testCase` per row |
| 07 | [Setup, teardown and test selection](07-setup-teardown-and-selection/) | intermediate | `before` / `after`, `categories` |
| 08 | [Retries and async testing](08-retries-and-async-testing/) | advanced | `retryIfFails`, `waitForEvent`, `EBC_Put` |
| 09 | [Modularity](09-modularity/) | advanced | Composite keyword, `return`, `callPlan` |

For what each control does and how to configure it, see the official
[controls documentation](https://step.dev/knowledgebase/userdocs/plans/controls/).

## Control coverage matrix

| Control | Sample |
|---------|--------|
| `testCase`, `testSet` | 01, 06, 07, 09 |
| `callKeyword` + `assert` | 01 and all others |
| `set`, `output.`, `previous.` | 02 |
| `${...}` interpolation, `expression:` | 02 and all others |
| `parameters`, `activationScript`, `priority`, `protectedValue` | 03 |
| execution parameters | 03 |
| `if`, `switch` / `case` | 04 |
| `check`, `doNegate`, `customErrorMessage` | 05 |
| `sequence` + `continueOnError` | 05 |
| `forEach` + `csv` | 06 |
| `before` / `after`, `categories` | 07 |
| `retryIfFails`, `waitForEvent` | 08 |
| Composite keyword + `return`, `callPlan` | 09 |

## Running any of them

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

Or point the Step MCP server at the directory and use `step_validate_plan` /
`step_execute_automation_package`.

[03](03-parameters-and-environments/) selects its environment with an execution parameter:
add `-ep env=DEV`, `TEST` or `PROD`.

Four plans are **expected to fail** — that is the lesson in them. Their names end with
"(intentionally fails)", and each sample README lists the expected outcome per plan:

- 01 C - One failing test case in a test set
- 05 D - Collect all failures
- 06 B - One failing data row
- 07 C - Cleanup after a failure

Everything else passes.

## What these samples teach

1. **One test case, one behaviour.** Name the test case after the behaviour it checks
   ("Wrong password is rejected"), and let a test set report each verdict separately.

2. **Assert on keyword outputs.** `assert` under the keyword call is the default; `check` is for
   conditions no single output can express.

3. **Business data in the plan, technical settings outside it.** Inputs carry what the test is
   about; URLs, endpoints and credentials come from parameters, selected per environment.

4. **Data drives the test, not the other way round.** Expected results live next to the inputs,
   in the data source, so a new case is a new row.

5. **Every test leaves the system as it found it.** Create test data in `before`, remove it in
   `after`, and keep test cases independent of each other.

6. **Reuse flows, don't copy them.** A flow used by several tests becomes a composite keyword or
   a plan called with `callPlan`.
