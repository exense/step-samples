---
use-case: e2e-testing
framework: playwright
language: csharp
target-platform: web
approach: keyword-driven
level: intermediate
---

# E2E Testing with Playwright (.NET / C#)

Demonstrates end-to-end test automation against the OpenCart demo shop using Playwright keywords written in C#, structured as a Step Automation Package with a proper test set / test case hierarchy.

This is the .NET counterpart of [e2e-testing-playwright](../e2e-testing-playwright/) (Java).

## What this sample shows

- Writing Step keywords in C# using the Playwright .NET API and the `StepApiKeyword` NuGet packages
- Declaring `DotNet` keywords in an automation package descriptor, including the `librariesFile` needed for dependencies
- Structuring a test plan with `testSet` and `testCase` nodes
- Cross-application testing: purchasing a product in OpenCart and verifying the order confirmation email in a webmail UI
- Attaching a Playwright trace to the Step report, replayable in the built-in trace viewer
- Reading a Step Parameter (`targetUrl`) from a keyword via `properties`
- Running the automation package locally with MSTest before deploying to Step

## Key files

| File | Purpose |
|------|---------|
| `automation-package.yaml` | Package definition: parameter, keywords, plan and test structure |
| `dotnet-keywords/e2e-testing-playwright-dotnet.csproj` | .NET build — produces the keyword assembly |
| `dotnet-keywords/NuGet.config` | Package sources: exense (Step Enterprise) for `StepApi*`, nuget.org for the rest |
| `dotnet-keywords/OpenCartPlaywrightKeywords.cs` | Keyword: add a product to the cart and complete the purchase |
| `dotnet-keywords/WebmailPlaywrightKeywords.cs` | Keyword: read and verify the order confirmation email |
| `dotnet-keywords/PlaywrightBrowsers.cs` | Prepares the Playwright runtime on the agent |
| `dotnet-keywords/AutomationPackageTest.cs` | MSTest runner for local execution |
| `.apignore` | Excludes build intermediates from the deployed package |

## Prerequisites

- .NET 8 SDK
- **Access to the exense NuGet repository.** The .NET keyword API (`StepApiFunction`, `StepApiKeyword`, `StepApiReporting`) is part of Step Enterprise and is *not* published on nuget.org. It is served from `https://nexus-enterprise.exense.ch/repository/exense-nuget/`, which requires Step Enterprise customer credentials:

  ```bash
  dotnet nuget update source exense --username <user> --password <password>
  ```

  `dotnet-keywords/NuGet.config` declares that feed alongside nuget.org and uses `packageSourceMapping` so only `StepApi*` is requested from it — without the mapping, NuGet queries every source for every package and the credentialed feed answers 401 for public packages such as `Microsoft.Playwright`, failing the whole restore.
- Playwright browsers, for local runs (see below)

## Build and run locally

The project ships the Playwright driver for the Step agent's platform (`linux-x64`). For a local run on Windows or macOS, override the driver platform:

```bash
cd dotnet-keywords

dotnet restore
dotnet build -c Debug -p:PlaywrightPlatform=win32_x64      # or osx / osx-arm64

# Install the browsers Playwright drives (once per machine)
pwsh bin/Debug/net8.0/playwright.ps1 install chromium

dotnet test --no-restore
```

Both keywords run headless by default. Pass `Headless: false` as a keyword input to watch the browser.

## Deploy to Step

Build for the agent platform first — the compiled assembly *and its dependencies* are what gets uploaded:

```bash
cd dotnet-keywords && dotnet build -c Debug     # defaults to PlaywrightPlatform=linux-x64
cd .. && step ap execute .
```

## Things worth knowing when running Playwright .NET on a Step agent

These are the non-obvious points this sample had to solve; they apply to any .NET keyword package.

**1. `librariesFile` is required.** A `DotNet` keyword declaration that only lists `dllFile` puts a single assembly on the agent, and loading the keyword then fails with `Could not load file or assembly 'Microsoft.Playwright'`. Point `librariesFile` at the directory holding the dependencies:

```yaml
- DotNet:
    name: "Purchase product in OpenCart"
    dllFile: dotnet-keywords/bin/Debug/net8.0/e2e-testing-playwright-dotnet.dll
    librariesFile: dotnet-keywords/bin/Debug/net8.0
```

**2. Ship only the driver platform you need.** Playwright drives browsers through a Node process, and each target platform needs its own ~110 MB Node runtime. `<PlaywrightPlatform>all</PlaywrightPlatform>` bundles five of them and pushes the package to ~536 MB, which is too large to upload. Pinning `linux-x64` keeps it at ~130 MB.

**3. The Node driver loses its executable bit.** An automation package zipped on Windows carries no Unix permission bits, so on a Linux agent the bundled `node` comes out non-executable and Playwright fails with `Permission denied`. `PlaywrightBrowsers.cs` restores it before first use.

**4. Agents have no browsers and no display.** The browsers are not part of the package, so the first keyword call downloads Chromium via `Microsoft.Playwright.Program.Main("install", "chromium")`. And since there is no display, the keywords default to `Headless = true`.

## Notes on the .NET keyword API

- Keyword classes extend `AbstractKeyword` (`Step.Handlers.NetHandler`) and annotate methods with `[Keyword(name = "...")]`.
- Keyword methods are invoked synchronously by the Step .NET handler, so the async Playwright API is driven with `GetAwaiter().GetResult()`.
- `input` is a `Newtonsoft.Json.Linq.JObject`, `properties` is a `Dictionary<string, string>` holding the Step Parameters, and results are written to `output`.
- A keyword declares the parameters it needs via `properties = new string[] { "targetUrl" }` on the `[Keyword]` attribute.
