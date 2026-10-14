# Legacy plan exports

Two plans exported from the Step plan editor as JSON.

| File | Plan |
|------|------|
| `Demo_Google-search.json` | A sequence calling an Echo keyword and asserting on its output |
| `Demo_Data-driven.json` | A data-driven plan iterating over a data source |

JSON is the format Step uses to **import and export** plans. It is not meant for authoring —
these two files are kept only as a sample of the shape.

For how to produce and consume such files — exporting single or bulk entities, exporting a
plan recursively with the entities it references, and the import options — see
[Import/Export entities](https://step.dev/knowledgebase/userdocs/import-export-entities/) in
the Step documentation.

To write a plan, use YAML:

- [../reference/](../reference/) — the YAML syntax reference
- [../rpa/](../rpa/) — worked, runnable samples

See the [plans README](../README.md) for how the three plan formats relate.
