# Functional testing samples: scope (working document, delete before PR)

Eight automation packages, ordered from simple to complex. Every plan has a `testSet` root with
one `testCase` per scenario. Keywords are 3-line Groovy `GeneralScript` stubs that simulate a
small web shop (login, catalogue, orders). Each plan asserts its own outcome.

Overlap with RPA: each sample stands on its own and is written from a testing angle. Where RPA
covers a control in more detail, the sample links to it.

**Artificial patterns.** These samples are read by AI agents as well as by people. Anything
added only to demonstrate a feature (a failure on purpose, a stub that fails twice, a forced
error) must be obvious as such:
- the plan or test case name says so, e.g. "Expected to fail - ..."
- a one-line comment in the YAML says why it is there and that it does not belong in a real plan
- the README lists the expected outcome of each plan

Out of scope: `testScenario` (load testing), composites and `callPlan` (rpa/07), plain-text
plans (their own section later).

---

## 01 First tests

**Concept:** A test case gives one verdict: call a keyword, then assert on its outputs. A test
set groups test cases, and each one gets its own verdict.

| Plan | Test cases | Expected |
|------|------------|----------|
| A, First test set | "Login succeeds", "Search returns results" | PASSED |
| B, "Expected to fail - one failing test case" | Same two test cases, with one assertion made to fail on purpose. The report shows one test case FAILED and the other PASSED. | FAILED (on purpose) |

**Controls:** `testSet`, `testCase`, `callKeyword` (inputs, outputs), `assert`, `check`

## 02 Inputs, outputs and variables

**Concept:** Where a keyword's input values come from: an earlier keyword's output, a variable,
or a value computed at run time.

| Test case | Shows |
|-----------|-------|
| Create then read an order | `createOrder` returns `orderId`; `set` stores it; `getOrder` takes it as input; `check` on the result |
| Output scope | `output.<field>` is readable only under the calling node; `previous.<field>` is replaced by the next call; a `set` variable lasts for its whole block |
| Unique test data | Inputs from `expression:`: a UUID, a timestamp-based username, a delivery date set to today + 3 days in a given format |

**Controls:** `set`, `output.`, `previous.`, `expression:` inputs
**Links:** rpa/01 for the binding table

## 03 Parameters and environments

**Concept:** One test runs against DEV, TEST and PROD. Which environment is used depends on an
`env` execution parameter, and the parameters change with it.

| Parameter | Entries |
|-----------|---------|
| `url` | one per environment with `activationScript: env == 'DEV'` (and `'TEST'`, `'PROD'`), plus one with no activation script as the default |
| `user` / `password` | protected value |
| A second key | shows `priority` choosing between two entries that both match |

| Test case | Shows |
|-----------|-------|
| Open the application | keyword input `url` taken from the parameter; `check` that the URL matches `env` |
| Log in | credentials from protected parameters |

**Controls:** `parameters`, `activationScript`, `priority`, `protectedValue`, execution
parameters

**To verify in phase 2:**
- the activation script can read the `env` execution parameter
- what happens when `env` is not set (the default entry is used)
- priority order between matching entries

The MCP execute tool does not pass execution parameters, so the DEV/TEST/PROD runs need the CLI
with `-ep env=...`. The user runs those.

## 04 Conditional blocks

**Concept:** Branch the test on data or on the environment.

| Test case | Shows |
|-----------|-------|
| Skip a destructive step on PROD | `if` on `env` |
| Verify by account type | `switch`/`case` on a keyword output (STANDARD / PREMIUM / BUSINESS), each case asserting its own expected discount; a fallback when no case matches |

**Controls:** `if`, `switch`, `case`
**Links:** rpa/04 for switch notes

## 05 Assertions in depth

**Concept:** Check exactly what is expected, test that invalid input is rejected, and report
every failed check instead of only the first.

| Plan | Shows | Expected |
|------|-------|----------|
| A, Assert operators | `equals`, `contains`, `beginsWith`, `endsWith`, `matches`, `lessThan`, `greaterThan`, `doNegate`, `check` with an expression | PASSED |
| B, Negative tests: business errors (priority) | Invalid login and an invalid order are rejected: the keyword returns a business error output, and the test asserts on it | PASSED |
| C, Negative tests: technical errors | A keyword that raises a technical error (exception, unreachable system). Shows what the report looks like and how to handle the error path in the plan. | PASSED (if the expected error can be asserted) |
| D, "Expected to fail - collect all failures" | Several asserts with `continueOnError`; two fail on purpose and both are reported | FAILED (on purpose) |

**Controls:** `assert`, `check`, `doNegate`, `continueOnError`

**To verify in phase 2 (plan C):** which mechanism lets a plan expect and assert a technical
error: `continueOnError` / `continueParentNodeExecutionOnError` followed by a check on the error,
a keyword-side business/technical error distinction, or something else. Choose the cleanest
working pattern and show only that one.

## 06 Data-driven tests

**Concept:** One test case per data row, each with its own verdict and named after its row.

| Test case | Shows |
|-----------|-------|
| One test case per row | `forEach` over `csv`, with a `testCase` inside the loop whose `nodeName` is set from the row |
| Rows with expected results | each row holds inputs and an expected value; the test asserts against the expected value |

**Controls:** `forEach`, `csv`, `testCase` inside a loop, dynamic `nodeName`
| Plan | Expected |
|------|----------|
| A, the two test cases above | PASSED |
| B, "Expected to fail - one failing row": the same loop, with one row whose expected value is wrong on purpose (the row's own data says so). The other rows still run and report separately. | FAILED (on purpose) |

**Links:** rpa/03 for data sources

## 07 Setup, teardown and test selection

**Concept:** The test cases in a set share fixtures but stay independent of each other. Run a
subset of them by category.

| Test case | Shows |
|-----------|-------|
| Set level | `before` on the `testSet` creates the test customer; `after` deletes it, and still runs when a test case fails |
| Test-case level | `before` / `after` on a `testCase` for its own data |
| Selection | `categories` (`smoke`, `regression`) and `--includeCategories smoke` |

**Controls:** `before`, `after`, `continueOnError` on `after`, `categories`

**To verify in phase 2:** whether `--includeCategories` filters test cases inside a plan or
only whole plans. If it filters only plans, split this into a smoke plan and a regression plan.

## 08 Retries and async testing

**Concept:** Absorb a flaky step, and wait for something the system completes asynchronously.

| Test case | Shows |
|-----------|-------|
| Flaky step | `retryIfFails` with `maxRetries` / `gracePeriod` around a keyword that fails twice |
| Poll for a status | `retryIfFails` around `getOrderStatus` + `assert status == SHIPPED`, with a `timeout` |
| Wait for an event | a keyword publishes an event; `waitForEvent` on `group` / `name` with `timeout`; `check` on the received event |

**Controls:** `retryIfFails`, `waitForEvent`
**Links:** rpa/05 for retries and `while`

**To verify in phase 2:**
- how a stub keyword publishes the event: Step's built-in event-broker Put keyword if the
  instance has it, otherwise the REST API
- how the received event is exposed to the following nodes (a `result` variable?)
