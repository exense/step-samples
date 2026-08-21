---
use-case: rpa
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: beginner
---

# 01 — Linear bot

The baseline shape of an RPA plan: one unattended bot run that opens an application, reads a
record, submits it, verifies the result and closes down. Every other sample in this set builds
on this structure.

The subject of this sample is the **plan**. The keywords are 3-line Groovy stubs that simulate a
back-office application, so the package runs on any Java agent — no browser, no build, no
external system.

## What this sample shows

- The anatomy of an RPA plan: a `testCase` root wrapping a linear sequence of steps
- **Chaining one keyword's output into the next keyword's input** — the most-used idiom in RPA
  plans, and the main lesson here
- A `session` block, so every keyword runs on the same agent and shares that context
- `echo` for a cheap audit trail in the execution report of an unattended run
- `check` for verifying plan variables, and `assert` for verifying keyword outputs
- `sleep` for asynchronous processing in the system under test — and why it is the wrong
  tool for waiting on a UI

## Chaining keyword outputs: the three bindings

This is where most hand-written Step plans go wrong. Three bindings can carry a keyword's output
forward, and they do **not** have the same reach.

### The recommended idiom — a `set` nested inside the `callKeyword`

```yaml
- callKeyword:
    keyword: "Read Record"
    children:
      - set:                                    # child of the keyword call
          key: amount
          value:
            expression: "output.amount"
- callKeyword:
    keyword: "Submit Record"
    inputs:
      - amount:
          expression: "amount"                # still available here, and later
```

A `set` placed inside a keyword call is a special case: **its variable is promoted to the parent
scope**, so it stays readable by every following sibling — not just the next one.

### The full picture

| Binding | Where it is in scope | Use it for |
|---------|----------------------|------------|
| `output.<field>` | **Only inside the calling node's own `children`** | Feeding a nested `set` or a nested `assert` |
| `previous.<field>` | The **immediately preceding sibling** only — the next keyword call replaces it | A quick check right after a call |
| `expression: "myVar"` | The block the `set` belongs to, and everything nested below it | Anything that must survive further steps |

`previous` is shown once in the plan (the `check` after *Submit the record*) and is deliberately
flagged there as the fragile option.

### There is no string interpolation

`"${myVar}"` is **not** expanded anywhere in this YAML. A plain string is a static value and
is passed through verbatim, so the keyword receives the literal characters `${myVar}`. This
holds in keyword inputs, `echo` text and `return` outputs alike.

It fails silently: if the keyword ignores the input, or only checks that it is non-empty,
the plan goes green while the bot was fed nonsense. Always use
`expression:`, and concatenate to build strings:

```yaml
text:
  expression: "'Record ' + recordId + ' submitted'"
```

## What the plan does *not* carry: the application context

The bot opens an application, then several keywords work in it. The browser, the driver, the
logged-in connection — none of that appears in this plan.

It lives in the **keyword session object** instead. `Open Back Office` does:

```groovy
session.put("appContext", driver)
```

and every keyword after it on the same agent token reads it back with `session.get(...)`.
The plan's only job is to wrap them in a `session` block so they share that token.

Passing a `sessionId` from keyword to keyword as an input is a common mistake in
hand-written plans. It clutters every call, and for a real driver object it cannot work at
all — a Playwright or Selenium handle does not serialise into a plan variable.

The rule of thumb: **the plan carries business data, the session carries technical context.**

`Read Record` and `Submit Record` here fail with a business error if the context is
missing, so the sample proves the mechanism rather than just describing it. Sessions get
fuller treatment in [06-session-and-scheduling](../06-session-and-scheduling/).

## `assert` vs `check`

One distinction worth knowing before you write your first plan:

- **`assert`** reads the **output report of a keyword**. It is only valid as a **child of a
  `callKeyword`**. Its `actual` field is the *name of an output field*, not an expression.
  Used as a standalone sibling it fails the execution with
  `Keyword report unreachable. Asserts should be wrapped in Keyword nodes in the test plan.`
- **`check`** evaluates a Groovy expression against **plan variables**. This is what you want for
  verifying a value you captured with `set`.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The plan — heavily commented, this is what to read |
| `keywords/openBackOffice.groovy` | Parks the application context in the agent `session` |
| `keywords/readRecord.groovy` | Returns `customer`, `recordType`, `amount`, `status` |
| `keywords/submitRecord.groovy` | Returns a `confirmationId` |
| `keywords/closeBackOffice.groovy` | Cleanup |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

The execution report should show four passing keyword calls, four passing `Set` nodes and two
passing `Check` nodes.
