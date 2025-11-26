# Playwright Automation Packages with Library

This project demonstrates how to structure a multi-module Maven project for Step automation packages using a shared library approach. It illustrates best practices for creating reusable, versioned code and sharing resources (like Playwright drivers) across multiple automation packages.

## Overview

This project consists of four modules that work together to demonstrate a scalable approach to automation package development:

### 1. playwright-automation-package-library

The **Library** module serves as the foundation for all automation packages in this project. It:

- Provides an `AbstractPlaywrightKeyword` base class that extends Step's `AbstractKeyword`
- Implements **driver sharing** across keywords from different Automation Packages through Step's session management
- Ensures proper resource lifecycle management with automatic cleanup
- Is built as an **uber-jar** (fat jar) packaging all dependencies including Playwright
- Can be versioned independently and reused across multiple projects

**Key Feature**: The library demonstrates how to share a Playwright driver instance between keywords, even when those keywords are deployed from different Automation Packages. This is achieved using Step's session storage mechanism (`session.put()` / `session.get()`), which ensures the same browser instance is reused across keyword executions within the same session.

### 2. playwright-automation-package-opencart

The **OpenCart** module is a concrete Automation Package that:

- Depends on the shared library module
- Contains Keywords declared in Java code for testing the OpenCart demo application
- Extends `AbstractPlaywrightKeyword` to leverage shared driver functionality
- Is built as an **uber-jar** (fat jar) excluding the library (scope "provided")

Example keyword: `"OpenCart - Buy MacBook in OpenCart"`

### 3. playwright-automation-package-exense-website

The **Exense Website** module is another concrete Automation Package that:

- Depends on the same shared library module
- Contains Keywords declared in Java code for testing the Exense website
- Extends `AbstractPlaywrightKeyword` to leverage shared driver functionality
- Is built as an **uber-jar** (fat jar) excluding the library (scope "provided")

Example keyword: `"Exense Website - Simple navigation"`

### 4. playwright-automation-package-test

The **Test** module demonstrates integration testing by:

- Declaring an automation package plan (in `automation-package.yaml`) that invokes keywords from both the OpenCart and Exense Website packages
- Sharing the same Playwright driver from one keyword to the next, demonstrating efficient resource usage
- **NOT** building as an uber-jar (regular jar packaging)
- Can be executed either:
  - **Locally** using JUnit with the Step runner (`RunAutomationPackageTest`)
  - **On a Step instance** after deploying all three packages (library, opencart, and exense-website)

## Project Structure

```
playwright-automation-packages-with-library/
├── pom.xml                                           # Parent POM with dependency management (BOM)
├── README.md
├── playwright-automation-package-library/            # Shared library (uber-jar)
│   ├── pom.xml
│   └── src/main/java/.../AbstractPlaywrightKeyword.java
├── playwright-automation-package-opencart/           # OpenCart keywords (uber-jar excluding the library)
│   ├── pom.xml
│   └── src/main/java/.../PlaywrightKeywordOpenCart.java
├── playwright-automation-package-exense-website/     # Exense Website keywords (uber-jar excluding the library)
│   ├── pom.xml
│   └── src/main/java/.../PlaywrightKeywordExenseWebsite.java
└── playwright-automation-package-test/               # Integration test (regular jar)
    ├── pom.xml
    ├── src/main/resources/automation-package.yaml
    └── src/test/java/.../RunAutomationPackageTest.java
```

## Key Concepts

### Dependency Management (BOM Pattern)

The parent POM acts as a Bill of Materials (BOM), centralizing:
- Version properties for all dependencies (Playwright, Step, JUnit)
- Dependency management for common libraries
- Plugin configuration management

This ensures consistency across all modules and simplifies version updates.

### Uber-jar (Fat Jar) Packaging

The library module is built as uber-jars using the `maven-shade-plugin`. This:
- Packages all dependencies into a single JAR file
- Simplifies deployment to Step (single file per library that can be shared by multiple Automation Packages)
- Ensures all required dependencies are available at runtime

### Playwright Driver Sharing

The `AbstractPlaywrightKeyword` class implements a crucial pattern for resource efficiency:

```java
protected Page getOrCreatePlaywrightPage() {
    PlaywrightWrapper playwrightWrapper = session.get(PlaywrightWrapper.class);
    if (playwrightWrapper == null) {
        // Create new Playwright, Browser, and Page instances
        // Store in session for reuse
        session.put(playwrightWrapper);
    }
    return playwrightWrapper.page;
}
```

This approach:
- Creates the Playwright driver only once per session
- Reuses the same browser instance across multiple keywords
- Automatically closes resources when the session ends (via `AutoCloseable`)
- Works across keywords from different Automation Packages

## Building the Project

### Build all modules

```bash
cd playwright-automation-packages-with-library
mvn clean package
```

This will create:
- `playwright-automation-package-library/target/playwright-automation-package-library-1.0.0.jar` (uber-jar)
- `playwright-automation-package-opencart/target/playwright-automation-package-opencart-1.0.0.jar` (uber-jar excluding the library)
- `playwright-automation-package-exense-website/target/playwright-automation-package-exense-website-1.0.0.jar` (uber-jar excluding the library)
- `playwright-automation-package-test/target/playwright-automation-package-test-1.0.0.jar` (regular jar)

### Build specific modules

```bash
# Build only the library
mvn clean package -pl playwright-automation-package-library

# Build only OpenCart package
mvn clean package -pl playwright-automation-package-opencart -am

# Build only Exense Website package
mvn clean package -pl playwright-automation-package-exense-website -am
```

## Running Tests

### Local Execution (JUnit)

Run the integration test locally using JUnit:

```bash
mvn test -pl playwright-automation-package-test
```

This executes the automation package defined in `automation-package.yaml` using the Step JUnit runner.

### Execution on Step Instance

1. **Deploy the library and Automation Packages to Step:**
   - Upload `playwright-automation-package-library-1.0.0.jar`
   - Upload `playwright-automation-package-opencart-1.0.0.jar`
   - Upload `playwright-automation-package-exense-website-1.0.0.jar`
   - Upload `playwright-automation-package-test-1.0.0.jar`

Using the Step maven plugin:

```bash
# Deploy the library
mvn install -pl playwright-automation-package-library
# Deploy the Automation Package for Opencart
mvn install -pl playwright-automation-package-opencart
# Deploy the Automation Package for the Exense Website
mvn install -pl playwright-automation-package-exense-website
# Deploy the Automation Package containing the Step plan
mvn install -pl playwright-automation-package-test
```

**Note**: Before using the maven plugin for deployments, configure Step connection in the parent POM:
```xml
<properties>
    <step.url>http://localhost:8080</step.url>
    <step.auth-token>your-token-here</step.auth-token>
    <step.step-project-name>your-project-name</step.step-project-name>
</properties>
```

2. **Execute the test plan *Playwright Multi-App Test*:**
   - The plan defined in `playwright-automation-package-test/src/main/resources/automation-package.yaml` can be executed once deployed to Step
   - Keywords from both OpenCart and Exense Website packages will share the same Playwright driver

## Key Benefits of This Architecture

1. **Code Reusability**: Common functionality is centralized in the library module
2. **Version Management**: The Library can be versioned independently and shared across projects
3. **Resource Efficiency**: Playwright driver is shared across keywords, reducing overhead
4. **Modularity**: Each application package is independent and can be deployed separately
5. **Testability**: Integration testing is possible both locally and on Step instances
6. **Maintainability**: Changes to shared functionality only require updating the library
7. **Scalability**: New automation packages can easily be added by depending on the library

## Dependencies

- **Java**: 11 or higher
- **Maven**: 3.6 or higher
- **Step**: 3.29.0
- **Step API**: 1.5.0
- **Playwright**: 1.56.0
- **JUnit**: 4.13.1

## License

This project is licensed under the GNU Affero General Public License v3.0. See the file headers for details.

## Additional Resources

- [Step Documentation](https://step.dev/)
- [Playwright Java Documentation](https://playwright.dev/java/)
- [Maven Multi-Module Projects](https://maven.apache.org/guides/mini/guide-multiple-modules.html)
