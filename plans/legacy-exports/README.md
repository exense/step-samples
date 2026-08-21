# Legacy plan exports

Two plans exported from the Step plan editor as JSON.

| File | Plan |
|------|------|
| `Demo_Google-search.json` | A sequence calling an Echo keyword and asserting on its output |
| `Demo_Data-driven.json` | A data-driven plan iterating over a data source |

Plans built in the Step plan editor are stored serialized as JSON, and these two files are
what that serialization looks like: `step.core.plans.Plan` with its `_class` discriminators,
`DynamicValue` wrappers and null fields. They are kept here as a sample of the shape, since
it is also what the plan REST API returns.

**This format is not meant to be hand-written.** For authoring a plan, use YAML:

- [../reference/](../reference/) — the YAML syntax reference
- [../rpa/](../rpa/) — worked, runnable samples

See the [plans README](../README.md) for how the three plan formats relate.
