---
use-case: functional-testing
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: advanced
---

# 08 — Retries and async testing

Absorb a step that fails now and then, and wait for an outcome that the application produces
asynchronously: by polling it, or by waiting for an event.

## Plans

| Plan | Shows | Expected |
|------|-------|----------|
| A - Retry a flaky step | Retrying a step that fails intermittently | PASSED |
| B - Poll until a condition is met | Polling until an asynchronous operation reaches the expected state | PASSED |
| C - Wait for an event | Waiting for an event published to Step's event broker | PASSED |

## Notes

- **`retryIfFails`** runs its children again while they fail, at most `maxRetries` times, with
  `gracePeriod` milliseconds between attempts and an overall `timeout`. With
  `reportLastTryOnly: true` the report keeps only the last attempt.
- **Polling** uses the same control: the `assert` fails while the order is not shipped yet,
  which triggers the next attempt.
- **`waitForEvent`** polls Step's event broker every `pollingSpeed` milliseconds for an event
  matching `group` and `name` (or `eventId`), and fails after `timeout`. It consumes the event.
  Put the steps that depend on the event after `waitForEvent`.
- The event is published with the `EBC_Put` keyword (inputs `eventGroup`, `eventName`,
  `payloadAsJson`). In practice it is usually published by another plan running in parallel;
  here it is in the same test case so that the sample runs on its own. Without an `ebcUrl`
  input, `EBC_Put` uses the broker of the controller running the plan.
- The stubs keep their state in the session of the test case: "Load Dashboard" fails on its
  first two calls on purpose, "Get Order Status" reports `SHIPPED` from its third call on.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The three plans and the keyword declarations |
| `keywords/loadDashboard.groovy` | Fails twice, then succeeds |
| `keywords/placeOrder.groovy` | Returns a new order id |
| `keywords/getOrderStatus.groovy` | Returns `PROCESSING`, then `SHIPPED` from the third request on |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project>
```

All three plans pass. Plan A shows two failed attempts before the successful one.
