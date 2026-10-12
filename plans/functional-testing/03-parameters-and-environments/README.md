---
use-case: functional-testing
focus: plans
framework: none
language: groovy
target-platform: web
approach: keyword-driven
level: intermediate
---

# 03 — Parameters and environments

One test, several environments: parameters hold the environment-specific values, and an `env`
execution parameter selects which ones are used.

## Plans

| Plan | Shows | Expected |
|------|-------|----------|
| A - Environment-specific parameters | Selecting parameter values per environment with activation scripts and priorities | PASSED |
| B - Technical settings as keyword properties | Giving a keyword a technical setting through its properties instead of an input | PASSED |
| C - Protected parameters | Using a secret stored as a protected parameter | PASSED |

## Notes

- **Selecting the value.** A parameter key can have several entries. Step uses the entry with
  the highest `priority` among those whose `activationScript` is true. Here the DEV, TEST and
  PROD entries have priority 10 and are active when `env` matches; the default entry has no
  activation script and priority 1, so it is used only when `env` is not set or matches none.
- A parameter value can be built from another parameter: `shopApiUrl` is `"${shopUrl}/api"`
  and follows the selected environment.
- **Inputs or properties.** Pass business data (the URL under test, the user) as keyword inputs.
  Technical settings, such as an API endpoint, can stay out of the plan: parameters are also
  available to keywords as properties (`properties.get("shopApiUrl")` in a Groovy keyword).
- A protected value is masked in the execution report (`***shopPassword***`) and reaches the
  keyword in clear text.

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | The parameters, the three plans and the keyword declarations |
| `keywords/openApplication.groovy` | Opens the shop at the `url` input |
| `keywords/checkShopApi.groovy` | Reads `shopApiUrl` from its properties and reports it |
| `keywords/login.groovy` | Accepts only the demo credentials |

## Running it

```bash
step ap execute -p . -u <your-step-url> --token <your-token> --projectName <your-project> -ep env=TEST
```

`env` takes `DEV`, `TEST` or `PROD`. Without it, `shopUrl` is the default `http://localhost:8080`.
All three plans pass in every case.
