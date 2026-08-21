# step-samples

Sample projects for [Step](https://step.dev) — the open-source open-source automation platform that unifies software automation across the entire DevOps lifecycle.

---

## Start here: Automation Packages

The **[automation-packages](automation-packages/)** directory is the primary entry point for this repository. It contains ready-to-run samples covering the most common automation use cases, each structured as a self-contained Step Automation Package.

> **What is an Automation Package?**  
> An Automation Package is a single deployable artifact (JAR, zip, or folder) that bundles keywords, plans, schedules, and parameters together. It is the recommended way to organize and deploy automation with Step.

Browse the **[automation-packages/README.md](automation-packages/README.md)** for a full index of samples with filtering by use-case, framework, language, and complexity level.

### Quick overview of available samples

| Use case | Examples |
|----------|---------|
| **Load testing** | Playwright, Selenium, Cypress, JMeter, k6, OkHttp, gRPC, Appium, Serenity BDD |
| **E2E testing** | Playwright (Java, TypeScript, JavaScript), data-driven, shared library |
| **Monitoring** | Synthetic monitoring with Playwright and Cypress, advanced alerting |
| **RPA** | Selenium-based back-office automation with CSV data sources |

---

## Plan samples

The **[plans](plans/)** directory zooms in on one part of an Automation Package: the
**plan** — the control-flow tree of loops, branches, retries and keyword calls that turns
keywords into an automation that does something useful.

The two directories answer different questions, and most people need both:

- **[automation-packages](automation-packages/)** — *"what does a real project look like?"*
  Complete, real-world blueprints for a given use case and stack: load testing with
  Playwright/TypeScript, synthetic monitoring with Cypress, RPA with Selenium. Take one as
  the starting point for your own project.
- **[plans](plans/)** — *"how do I express this logic in a plan?"* One plan concept per
  sample, with keyword code deliberately reduced to 3-line stubs so the plan itself is the
  subject. Look things up here when you are writing the plan inside your package.

| Section | Contents |
|---------|----------|
| [plans/rpa](plans/rpa/) | Seven RPA plan samples: loops and data sources, branching, resilience, sessions, scheduling, reuse |
| [plans/load-testing](plans/load-testing/) | Six load-testing plan samples: thread groups, scenarios and mixed load, test data, measurements, SLA gates |
| [plans/reference](plans/reference/) | Standalone YAML plans illustrating the syntax — the shape of a plan, and static values vs expressions |

Samples for functional testing and monitoring will follow the same structure.

---

## Other directories

These directories contain older, lower-level samples that predate Automation Packages. They remain useful as reference material for individual keywords or Step client usage.

| Directory | Contents |
|-----------|----------|
| [keywords](keywords/) | Standalone keyword examples by technology (Java, .NET, Cypress, TypeScript/Playwright, JMeter, k6, Oryon, gRPC, SoapUI, …) |
| [step-client](step-client/) | Sample projects using the Step Controller API (Java and REST) |
| [plugins](plugins/) | Example Step plugin |
| [maven-plugins](maven-plugins/) | Sample for the Step Maven upload plugin |

---

## Setup

### Open-source samples

Samples of open-source features rely exclusively on public dependencies and build without any specific prerequisites.

### Enterprise samples

Enterprise samples require a Step Enterprise license and depend on artifacts hosted on the exense private Nexus. Add the following to your `~/.m2/settings.xml`:

```xml
<servers>
  <server>
    <id>nexus-exense</id>
    <username>your_step_enterprise_username</username>
    <password>your_password</password>
  </server>
</servers>
```

If your environment uses an HTTP proxy:

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

### Running Step

The quickest way to get started is **[stepcloud.ch](https://www.stepcloud.ch/)** — create a free account, get evaluation credits, and have a fully managed Step instance running in minutes with no infrastructure to set up. This is the recommended approach for trying out these samples.

For an on-premise installation, download the latest release from [github.com/exense/step/releases](https://github.com/exense/step/releases) and follow the [installation guide](https://step.dev/knowledgebase/setup/installation/binaries/quick-setup/).

---

## Help

- Documentation: [step.dev](https://step.dev)
- Support: [step.dev/contact](https://step.dev/contact/)
