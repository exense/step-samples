---
use-case: load-testing
framework: grpc
language: java
target-platform: api
approach: keyword-driven
level: advanced
---

# Load Testing a gRPC Service (Java)

Demonstrates load testing a gRPC endpoint from Step using a Java keyword built with the gRPC Java library. Includes a performance assertion to verify response times under load.

## What this sample shows

- Writing a Step keyword in Java that calls a gRPC service (`SayHello`)
- Defining the service contract with a `.proto` file and generating stubs at build time
- Running the keyword under load with a Thread Group (10 VUs × 10 iterations)
- Using `performanceAssert` to fail the plan if average gRPC response time exceeds 500 ms

## Key files

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build with protobuf code generation and shaded jar packaging |
| `src/main/resources/automation-package.yaml` | Plan: Thread Group + performance assertion |
| `src/main/proto/helloworld.proto` | gRPC service definition (Protobuf) |
| `src/main/java/.../gRPCHelloWorldKeywords.java` | Step keyword wrapping the gRPC client call |
