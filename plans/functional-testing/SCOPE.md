# Functional testing samples: scope (working document, delete before PR)

Nine automation packages, ordered from simple to complex, one plan per topic (see Naming and
structure below). Keywords are 3-line Groovy `GeneralScript` stubs that simulate a
small web shop (login, catalogue, orders). Each plan asserts its own outcome.

Overlap with RPA: each sample stands on its own and is written from a testing angle. No links
to the RPA samples.

**Artificial patterns.** These samples are read by AI agents as well as by people. Anything
added only to demonstrate a feature (a failure on purpose, a stub that fails twice, a forced
error) must be obvious as such:
- the plan or test case name says so, e.g. "Expected to fail - ..."
- a one-line comment in the YAML says why it is there and that it does not belong in a real plan
- the README lists the expected outcome of each plan

Out of scope: `testScenario` (load testing), plain-text plans (their own section later).

**Target: Step 31.** Descriptors declare schema `version: "1.3.0"`.
- Validation runs against stepee-master.stepcloud-test.ch, using the Step 31 CLI and MCP from
  PATH.
- Reference docs: the unreleased knowledgebase in `step-knowledgebase-website` (master).
- **String interpolation** (`${...}`) is the default way to put variables into text values
  (keyword inputs, control fields, parameters). `expression:` is used only where interpolation
  can't do the job: a non-string value (number, boolean, object), or logic containing braces
  (closures, map literals).

**Naming and structure** (same convention as rpa/ and load-testing/):
- The AP name and plan names are topic-specific. One plan per topic, named
  `"<letter> - <topic>"` ("A - If block"). A deliberately failing plan ends with
  " (intentionally fails)". No commas, because `--includePlans` splits on them.
- Exception: a plan that is not a demo but is called by another plan (`callPlan`) keeps a
  business name, e.g. rpa/07 "Archive processed records".
- Test cases and every other `nodeName` use business names, phrased as an expectation
  ("Cookie banner is closed when shown").
- The plan root is the `testCase` itself. A `testSet` root only where test sets are the topic
  (01 B/C, 06, 07 A); it has no `nodeName`, since the report shows the plan name there.

**Assert vs check**, a rule for every sample:
- `assert` is a child of a `callKeyword` and validates that keyword's output. Use it wherever
  it is possible.
- `check` is a general expression-based check anywhere in a plan. Use it only when there is no
  keyword output to assert on, or when the condition needs a custom expression.

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
| Create then read an order | `createOrder` returns `orderId`; `set` stores it; `getOrder` takes it as input; `assert` on `getOrder`'s output |
| Output scope | `output.<field>` is readable only under the calling node; `previous.<field>` is replaced by the next call; a `set` variable lasts for its whole block |
| Unique test data | Computed inputs: a UUID, a timestamp-based username, a delivery date set to today + 3 days in a given format |

Interpolation is shown first, for text built from variables, e.g. `"Order ${orderId}"` and
`"user-${runId}@test.local"`. Then `expression:` for the cases it is still needed for: a
non-string input (a number or a boolean), and logic containing braces.

**Controls:** `set`, `output.`, `previous.`, `${...}` interpolation, `expression:` inputs

## 03 Parameters and environments

**Concept:** One test runs against DEV, TEST and PROD. Which environment is used depends on an
`env` execution parameter, and the parameters change with it.

| Parameter | Entries |
|-----------|---------|
| `url` | one per environment with `activationScript: env == 'DEV'` (and `'TEST'`, `'PROD'`), plus one with no activation script as the default |
| `user` / `password` | protected value |
| A second key | shows `priority` choosing between two entries that both match |
| `apiUrl` | built from another parameter with interpolation: `"${url}/api"` |

| Test case | Shows |
|-----------|-------|
| Open the application | keyword input `url` taken from the parameter; `assert` on the keyword's output that the URL matches `env` |
| Log in | credentials from protected parameters |

**Controls:** `parameters`, `activationScript`, `priority`, `protectedValue`, execution
parameters

**To verify in phase 2:**
- the activation script can read the `env` execution parameter
- what happens when `env` is not set (the default entry is used)
- priority order between matching entries
- a parameter value referencing another parameter with `${...}`

The DEV/TEST/PROD runs pass `env` as an execution parameter, through the MCP (the Step 31
version supports this) or `step ap execute -ep env=...`.

## 04 Conditional blocks

**Concept:** Branch the test on data or on the environment.

| Test case | Shows |
|-----------|-------|
| Skip a destructive step on PROD | `if` on `env` |
| Verify by account type | `switch`/`case` on a keyword output (STANDARD / PREMIUM / BUSINESS), each case asserting its own expected discount; a fallback when no case matches |

**Controls:** `if`, `switch`, `case`

## 05 Assertions in depth

**Concept:** Check exactly what is expected, test that invalid input is rejected, and report
every failed check instead of only the first. Explains `assert` vs `check`: `assert` validates
a keyword's output and is preferred wherever possible; `check` evaluates any expression in the
plan.

| Plan | Shows | Expected |
|------|-------|----------|
| A, Assert operators | `assert` with `equals`, `contains`, `beginsWith`, `endsWith`, `matches`, `lessThan`, `greaterThan`, `isNull`, `doNegate`, `customErrorMessage`; one `check` for a condition across two keyword outputs, which `assert` cannot express | PASSED |
| B, Negative tests | Invalid login and an invalid order are rejected: the keyword returns a business error output, and the test asserts on it | PASSED |
| C, "Expected to fail - collect all failures" | Several asserts with `continueOnError`; two fail on purpose and both are reported | FAILED (on purpose) |

**Controls:** `assert`, `check`, `doNegate`, `customErrorMessage`, `continueOnError`

## 06 Data-driven tests

**Concept:** One test case per data row, each with its own verdict and named after its row.

| Test case | Shows |
|-----------|-------|
| One test case per row | `forEach` over `csv`, with a `testCase` inside the loop whose `nodeName` is interpolated from the row (`"Login as ${row.user}"`) |
| Rows with expected results | each row holds inputs and an expected value; `assert` on the keyword output against the row's expected value |

**Controls:** `forEach`, `csv`, `testCase` inside a loop, interpolated `nodeName`

| Plan | Expected |
|------|----------|
| A, the two test cases above | PASSED |
| B, "Expected to fail - one failing row": the same loop, with one row whose expected value is wrong on purpose (the row's own data says so). The other rows still run and report separately. | FAILED (on purpose) |


## 07 Setup, teardown and test selection

**Concept:** The test cases in a set share fixtures but stay independent of each other. Run a
subset of them by category.

| Test case | Shows |
|-----------|-------|
| Set level | `before` on the `testSet` creates the test customer; `after` deletes it, and still runs when a test case fails |
| Test-case level | `before` / `after` on a `testCase` for its own data |
| Selection | `categories` (`smoke`, `regression`) and `--includeCategories smoke` |

**Controls:** `before`, `after`, `continueOnError` on `after`, `categories`

**Selection:** in the Step 31 CLI, `--includeCategories` / `--excludeCategories` select whole
plans by their `categories`. There is no option to filter individual test cases. So the
selection part becomes two plans, "Smoke tests" (`smoke`) and "Regression tests"
(`regression`), plus the CLI command that runs only the smoke plan.

## 08 Retries and async testing

**Concept:** Absorb a flaky step, and wait for something the system completes asynchronously.

| Test case | Shows |
|-----------|-------|
| Flaky step | `retryIfFails` with `maxRetries` / `gracePeriod` around a keyword that fails twice |
| Poll for a status | `retryIfFails` around `getOrderStatus` + `assert` on `status` equals `SHIPPED`, with a `timeout` |
| Wait for an event | the event is published (built-in `EBC_Put` keyword, standing in for the system under test); `waitForEvent` on `group` / `name` with `timeout` / `pollingSpeed`; then a check on the received event (the docs say it is stored in a `result` variable) |

**Controls:** `retryIfFails`, `waitForEvent`

**To verify in phase 2:**
- `EBC_Put` / `EBC_Get` / `EBC_Peek` are available on stepee-master and how their `ebcUrl`
  input is set (a parameter, e.g. `"${controllerUrl}"`); if they are not available, publish
  from a Groovy stub instead
- the variable the received event is exposed in, and its shape (payload access)

## 09 Modularity

**Concept:** Reuse a business flow instead of copying its steps. Two mechanisms:
- a **composite keyword** packs several keyword calls into one keyword, with declared inputs and
  outputs. It is called and asserted like any other keyword.
- **`callPlan`** runs another complete plan, e.g. to build a regression suite from existing test
  plans. It returns nothing to the caller.

| Plan | Test case | Shows | Expected |
|------|-----------|-------|----------|
| A - Composite keyword | "Customer can place an order" | Composite "Place Order" (Login + Add To Cart + Checkout, inputs `username`, `sku`, `return` of `orderId`), called once and asserted on its output | PASSED |
| B - Call plans from a suite | test set with two test cases, each calling an existing plan | `callPlan` by name | PASSED |
| "Customer can log in" | (called plan, business name) | standalone test plan reused by B; also runs on its own | PASSED |
| "Customer can search the catalogue" | (called plan, business name) | idem | PASSED |

The called plans are complete test plans that also pass on their own, so no category is needed
to exclude them from a normal run.

**Controls:** Composite keyword (`schema`, `plan`, `return`), `callPlan` with `plan: "<name>"`

**To verify in phase 2:**
- how `callPlan` results appear in the caller's report (own branch, verdict propagation when the
  called plan fails)
- `${...}` interpolation inside the composite (`input.username`)
