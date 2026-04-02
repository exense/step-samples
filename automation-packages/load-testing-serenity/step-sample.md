---
use-case: load-testing
framework: serenity-bdd
language: java
target-platform: web
approach: project-integration
level: advanced
---

# Load Testing with Serenity BDD + Cucumber (Java)

Integrates a Serenity BDD / Cucumber test suite into Step by wrapping the Serenity runner as a keyword. This enables running BDD-style acceptance tests at scale as a load test.

## What this sample shows

- Wrapping a Serenity BDD test runner (Cucumber feature files + step definitions) as a single Step keyword
- Scaling BDD test execution across 10 VUs × 1000 iterations via a Thread Group
- Generating Serenity HTML reports alongside Step execution reports
- Running the package locally with the Step JUnit runner (`AutomationPackageRunnerTest`)

## Key files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build with Serenity, Cucumber, and Step dependencies |
| `src/main/resources/automation-package.yaml` | Package definition: Thread Group load plan |
| `src/main/resources/features/visit_opencart.feature` | Cucumber feature file describing the OpenCart journey |
| `src/main/java/.../SerenityKeyword.java` | Step keyword that invokes the Serenity/Cucumber runner |
| `src/main/java/.../OpencartStepDefinition.java` | Cucumber step definitions using Serenity page objects |
