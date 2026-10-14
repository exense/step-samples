# Step Samples

Official samples for [Step](https://step.dev), the open-source automation platform that unifies software automation across the entire DevOps lifecycle.

Explore ready-to-run automation projects, learn Step's core concepts, and use these examples as starting points for your own projects.

---

## How the samples are organised

The repository is organized into three main categories. Each sample is designed to illustrate a specific use case, technology, or automation concept.

| Directory | Contents                                                                                           | Best for                                  |
|-----------|----------------------------------------------------------------------------------------------------|-------------------------------------------|
| **[automation-packages](automation-packages/)** | Blueprints: complete projects for a given use case and stack, with their build, keywords and plans | Starting a new automation project         |
| **[plans](plans/)** | Plan samples: focused examples of Step's automation logic and orchestration capabilities           | Learning how to build and structure plans |
| **[keywords](keywords/)** | Keyword samples: examples of integrating programming languages and automation frameworks with Step | Implementing and integrating keywords     |

> All these samples are packages as Automation Packages, the Step standard for packaging portable automation and everything that belongs to it: keywords, plans, schedules, and parameters together.

---

## Automation Package blueprints

The **[automation-packages](automation-packages/)** directory contains complete, real-world
blueprints. Each one is a self-contained project for a given use case and stack: load testing
with Playwright/TypeScript, synthetic monitoring with Cypress, RPA with Selenium. Take one as
the starting point for your own project.

Browse the **[automation-packages/README.md](automation-packages/README.md)** for a full index of blueprints with filtering by use-case, framework, language, and complexity level.

| Use case | Examples |
|----------|---------|
| **Load testing** | Playwright, Selenium, Cypress, JMeter, k6, OkHttp, gRPC, Appium, Serenity BDD |
| **E2E testing** | Playwright (Java, TypeScript, JavaScript), data-driven, shared library |
| **Monitoring** | Synthetic monitoring with Playwright and Cypress, advanced alerting |
| **RPA** | Selenium-based back-office automation with CSV data sources |

---

## Plan samples

The **[plans](plans/)** directory is about **plans** — the implementation of an automation
scenario, whether that is a functional test case, a load test, an RPA routine or a synthetic
monitoring probe. A plan combines keywords with the controls that build up its execution
logic: loops, branches, retries, waits.

Each sample covers one plan concept, with keyword code deliberately reduced to 3-line stubs so
the plan itself is the subject. Look things up here when you are writing a plan. See
**[plans/README.md](plans/README.md)** for the full index.

| Section | Contents |
|---------|----------|
| [plans/functional-testing](plans/functional-testing/) | Nine functional-testing plan samples: test sets, inputs and outputs, parameters, assertions, data-driven tests, setup and cleanup, async testing, modularity |
| [plans/load-testing](plans/load-testing/) | Six load-testing plan samples: thread groups, scenarios and mixed load, test data, measurements, SLA gates |
| [plans/rpa](plans/rpa/) | Seven RPA plan samples: loops and data sources, branching, resilience, sessions, scheduling, reuse |
| [plans/reference](plans/reference/) | Standalone YAML plans illustrating the syntax — the shape of a plan, and static values vs expressions |

Samples for monitoring will follow the same structure.

---

## Keyword samples

The **[keywords](keywords/)** directory holds keyword examples by technology (Java, .NET,
Cypress, TypeScript/Playwright, JMeter, k6, Oryon, gRPC, SoapUI, …).

New keyword samples will be added in the same form as `plans/`: one keyword concept per
sample, each shipped as a runnable automation package. The existing examples predate
automation packages and remain useful as reference until then.

---

## Other directories

| Directory | Contents |
|-----------|----------|
| [step-client](step-client/) | Sample projects using the Step Controller API (Java and REST) |
| [plugins](plugins/) | Example Step plugin |
| [maven-plugins](maven-plugins/) | Sample for the Step Maven upload plugin |

---

## Getting started

### 1. Get access to Step

The easiest way to get started is [Step Cloud](https://www.stepcloud.ch/). Create a free account and use the included evaluation credits to explore and run the samples without setting up infrastructure.

Alternatively, you can run Step on-premises:

- [Download Step from GitHub](https://github.com/exense/step-distribution/releases)
- [Follow the installation guide](https://step.dev/knowledgebase/setup/installation/binaries/quick-setup/)

### 2. Choose a sample

Browse the [Automation Package blueprints](automation-packages/README.md) for complete projects or the [Plan samples](plans/README.md) to learn specific automation concepts.

Each sample contains the relevant source files and configuration for its use case.

### 3. Build and run

Follow the instructions in the selected sample's README to build its Automation Package, deploy it to Step, and execute the corresponding plans.

### Enterprise dependencies

Open-source samples use publicly available dependencies and require no private repository credentials.

Some samples use Step Enterprise features and require a valid Enterprise license and access to the exense private Nexus repository.

Configure your Maven credentials in `~/.m2/settings.xml`:

```xml
<servers>
  <server>
    <id>nexus-exense</id>
    <username>your_step_enterprise_username</username>
    <password>your_password</password>
  </server>
</servers>
```

If your environment requires an HTTP proxy, add the following configuration:

```xml
<proxies>
  <proxy>
    <id>step-proxy</id>
    <active>true</active>
    <protocol>http</protocol>
    <host>your_proxy</host>
    <port>your_proxy_port</port>
    <nonProxyHosts>*.yourdomain</nonProxyHosts>
  </proxy>
</proxies>
```

## Documentation and support

- **Documentation:** [step.dev](https://step.dev)
- **Support and questions:** [step.dev/contact](https://step.dev/contact/)
