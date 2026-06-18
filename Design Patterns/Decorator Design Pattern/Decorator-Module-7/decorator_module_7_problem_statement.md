# Decorator Design Pattern Practice — Module 7

## Configurable HTTP Client Decorator Chain Builder

### Difficulty Level

**Final Module / Advanced+ / Final Boss**

This is the seventh and final module in the Decorator Design Pattern practice series.

In the earlier modules, you practiced Decorator Pattern through gradually harder examples:

```text
Module 1: Coffee Add-ons
Module 2: Pizza Toppings
Module 3: Notification Sender Pipeline
Module 4: Text/File Reader Transformation Pipeline
Module 5: Payment Processor Backend Pipeline
Module 6: HTTP Client Decorator Pipeline
```

Module 7 is the final step.

In Module 6, you manually created an HTTP client decorator chain. In Module 7, you will improve that design by creating a **configuration-driven decorator chain builder**.

Instead of manually wrapping decorators one by one in `Main`, your code should build the decorator chain dynamically from:

```text
1. A base HttpClient
2. A list of selected decorator types
3. A config object
4. A priority-based ordering system
```

---

# 1. Problem Introduction

In Module 6, you created a backend-style HTTP client pipeline like this:

```java
HttpClient client = new BasicHttpClient();

client = new TimeoutHttpClientDecorator(client, 3000);
client = new LoggingHttpClientDecorator(client);
client = new AuthHeaderHttpClientDecorator(client, "Bearer test-token");
client = new RequestIdHttpClientDecorator(client);
client = new MetricsHttpClientDecorator(client, metrics);
```

This works, but it has a limitation:

```text
The decorator chain is manually created every time.
```

If the same chain is needed in multiple places, this wrapping logic gets repeated.

If someone changes the order accidentally, the output and behavior can change.

If more decorators are added later, the manual setup becomes harder to maintain.

Module 7 solves this by introducing a builder:

```java
HttpClient client = DecoratorChainBuilder.build(
        new BasicHttpClient(),
        decorators,
        config
);
```

The builder should apply decorators automatically based on:

```text
1. What decorators were requested
2. What configuration values are available
3. What priority/order each decorator should have
```

---

# 2. Background Information

## 2.1 What is the Decorator Pattern?

The Decorator Pattern lets you add behavior to an object without modifying its original class.

The common structure is:

```text
Component Interface
Concrete Component
Base Decorator
Concrete Decorators
```

In this module:

| Decorator Pattern Concept | Module 7 Class |
|---|---|
| Component Interface | `HttpClient` |
| Concrete Component | `BasicHttpClient` |
| Base Decorator | `HttpClientDecorator` |
| Concrete Decorators | `AuthHeaderHttpClientDecorator`, `RequestIdHttpClientDecorator`, `LoggingHttpClientDecorator`, `TimeoutHttpClientDecorator`, `RetryHttpClientDecorator`, `MetricsHttpClientDecorator` |
| Builder / Factory | `DecoratorChainBuilder` |
| Configuration | `HttpClientConfig` |
| Decorator Enum | `DecoratorType` |

---

## 2.2 Why Module 7 Exists

Module 6 focused on manually applying decorators.

Module 7 focuses on creating a **clean reusable builder** that can apply decorators dynamically.

This is closer to how backend systems are designed in real projects.

For example, a production system may have config like:

```yaml
httpClient:
  authEnabled: true
  loggingEnabled: true
  requestIdEnabled: true
  timeoutEnabled: true
  timeoutMs: 3000
  retryEnabled: true
  maxRetries: 2
  metricsEnabled: true
```

The application can read this configuration and build the correct HTTP client automatically.

---

# 3. Why This Problem Matters

This module is important because it teaches how Decorator Pattern can be used in a scalable backend-friendly way.

You are not just decorating an object anymore.

You are creating a system that can:

```text
1. Dynamically choose decorators
2. Enforce a safe order
3. Avoid repeated chain-building code
4. Support configuration-driven behavior
5. Prevent accidental output changes due to wrong decorator order
6. Make the client pipeline easier to extend
```

This makes the design more realistic and closer to production code.

---

# 4. Real-World Context

Imagine your backend service needs to call multiple APIs:

```text
Order API
Payment API
User API
Notification API
Bank API
Third-party vendor API
Internal microservices
```

Each outgoing API call may need some combination of:

```text
Authorization
Request ID
Logging
Timeout
Retry
Metrics
```

Different services may require different client setups.

## Service A

```text
Auth + Request ID + Logging + Metrics
```

## Service B

```text
Auth + Timeout + Retry + Metrics
```

## Service C

```text
Request ID + Logging only
```

Without a builder, each service manually creates its own chain.

With a builder, each service only declares what it needs:

```java
List<DecoratorType> decorators = List.of(
        DecoratorType.TIMEOUT,
        DecoratorType.LOGGING,
        DecoratorType.AUTH,
        DecoratorType.REQUEST_ID,
        DecoratorType.METRICS
);

HttpClient client = DecoratorChainBuilder.build(
        new BasicHttpClient(),
        decorators,
        config
);
```

The builder handles the chain construction.

---

# 5. Core Problem Statement

Build a **Configurable HTTP Client Decorator Chain Builder**.

The system should:

```text
1. Start with a base HttpClient.
2. Accept a list of requested decorators.
3. Accept a configuration object.
4. Sort decorators using predefined priority.
5. Apply decorators in the correct order.
6. Return the final decorated HttpClient.
7. Ensure output remains predictable even if the input list order changes.
```

The final API should look like this:

```java
HttpClient client = DecoratorChainBuilder.build(
        new BasicHttpClient(),
        decorators,
        config
);
```

After building the client, this should work:

```java
HttpResponse response = client.execute(request);
```

---

# 6. Main Objective

The main objective of Module 7 is:

```text
Move from manual decorator wrapping to configuration-driven decorator chain building.
```

In Module 6, the developer controlled the order manually.

In Module 7, the builder should control the order.

This prevents accidental changes in behavior when someone passes decorators in a different order.

---

# 7. Key Design Requirement: Order Must Be Controlled

Decorator order matters.

For example:

```java
client = new AuthHeaderHttpClientDecorator(client, "Bearer token");
client = new RequestIdHttpClientDecorator(client);
client = new LoggingHttpClientDecorator(client);
```

This creates:

```text
Logging
    -> RequestId
        -> Auth
            -> BasicHttpClient
```

Execution order:

```text
Logging -> RequestId -> Auth -> BasicHttpClient
```

So logging runs before headers are added.

Output may be:

```text
LOG: Headers: {}
REQUEST-ID: Added request id abc-123
AUTH: Authorization header added
```

But if the order is:

```java
client = new LoggingHttpClientDecorator(client);
client = new AuthHeaderHttpClientDecorator(client, "Bearer token");
client = new RequestIdHttpClientDecorator(client);
```

This creates:

```text
RequestId
    -> Auth
        -> Logging
            -> BasicHttpClient
```

Execution order:

```text
RequestId -> Auth -> Logging -> BasicHttpClient
```

Output becomes:

```text
REQUEST-ID: Added request id abc-123
AUTH: Authorization header added
LOG: Headers: {X-Request-Id=abc-123, Authorization=Bearer token}
```

This is better for observability.

Therefore, Module 7 should use a **priority-based order** so the final chain is stable.

---

# 8. Required Classes

You will reuse the HTTP client classes from Module 6:

```text
HttpClient
HttpRequest
HttpResponse
BasicHttpClient
HttpClientDecorator
AuthHeaderHttpClientDecorator
RequestIdHttpClientDecorator
LoggingHttpClientDecorator
TimeoutHttpClientDecorator
RetryHttpClientDecorator
HttpClientMetrics
MetricsHttpClientDecorator
UnstableHttpClient
```

You will add these new Module 7 classes:

```text
DecoratorType
HttpClientConfig
DecoratorChainBuilder
Main
```

---

# 9. `DecoratorType`

## Purpose

`DecoratorType` represents the decorators that can be selected.

Instead of directly writing:

```java
new LoggingHttpClientDecorator(client)
```

you represent that choice as:

```java
DecoratorType.LOGGING
```

## Required Enum

```java
public enum DecoratorType {

    TIMEOUT(10),
    LOGGING(20),
    AUTH(30),
    REQUEST_ID(40),
    RETRY(50),
    METRICS(60);

    private final int priority;

    DecoratorType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
```

## Why Priority Is Needed

The priority defines safe construction order.

Lower priority decorators are applied earlier and stay closer to the base client.

Higher priority decorators are applied later and become outer wrappers.

For this priority setup:

```text
TIMEOUT = 10
LOGGING = 20
AUTH = 30
REQUEST_ID = 40
RETRY = 50
METRICS = 60
```

The sorted construction order becomes:

```text
TIMEOUT -> LOGGING -> AUTH -> REQUEST_ID -> RETRY -> METRICS
```

The runtime execution order becomes:

```text
METRICS -> RETRY -> REQUEST_ID -> AUTH -> LOGGING -> TIMEOUT -> BASIC
```

---

# 10. `HttpClientConfig`

## Purpose

Some decorators need extra values.

For example:

| Decorator | Required Config |
|---|---|
| `AuthHeaderHttpClientDecorator` | auth token |
| `TimeoutHttpClientDecorator` | timeout in milliseconds |
| `RetryHttpClientDecorator` | max retries |
| `MetricsHttpClientDecorator` | metrics object |
| `LoggingHttpClientDecorator` | no extra config |
| `RequestIdHttpClientDecorator` | no extra config |

Instead of passing all these values separately, store them in one config class.

## Required Fields

```text
authToken
timeoutMs
maxRetries
metrics
```

## Expected Code

```java
public class HttpClientConfig {

    private final int timeoutMs;
    private final int maxRetries;
    private final String authToken;
    private final HttpClientMetrics metrics;

    public HttpClientConfig(String authToken, int timeoutMs, int maxRetries, HttpClientMetrics metrics) {
        if (authToken == null || authToken.isBlank()) {
            throw new IllegalArgumentException("authToken cannot be null or blank.");
        }

        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs must be greater than zero.");
        }

        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries cannot be negative.");
        }

        if (metrics == null) {
            throw new IllegalArgumentException("HttpClientMetrics cannot be null.");
        }

        this.authToken = authToken;
        this.timeoutMs = timeoutMs;
        this.maxRetries = maxRetries;
        this.metrics = metrics;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public String getAuthToken() {
        return authToken;
    }

    public HttpClientMetrics getMetrics() {
        return metrics;
    }
}
```

---

# 11. `DecoratorChainBuilder`

## Purpose

This is the most important class of Module 7.

It is responsible for:

```text
1. Accepting the base client
2. Accepting requested decorators
3. Sorting decorators by priority
4. Applying decorators one by one
5. Returning the final decorated client
```

The builder should not execute HTTP requests.

It should only build the client.

## Expected Code

```java
import java.util.Comparator;
import java.util.List;

public class DecoratorChainBuilder {

    private DecoratorChainBuilder() {
        /*
         * Utility class.
         * Should not be instantiated.
         */
    }

    public static HttpClient build(
            HttpClient baseClient,
            List<DecoratorType> decoratorTypes,
            HttpClientConfig config
    ) {
        if (baseClient == null) {
            throw new IllegalArgumentException("Base HttpClient cannot be null.");
        }

        if (decoratorTypes == null) {
            throw new IllegalArgumentException("Decorator list cannot be null.");
        }

        if (config == null) {
            throw new IllegalArgumentException("HttpClientConfig cannot be null.");
        }

        List<DecoratorType> sortedDecorators = decoratorTypes.stream()
                .distinct()
                .sorted(Comparator.comparingInt(DecoratorType::getPriority))
                .toList();

        HttpClient client = baseClient;

        for (DecoratorType decoratorType : sortedDecorators) {
            client = applyDecorator(client, decoratorType, config);
        }

        return client;
    }

    private static HttpClient applyDecorator(
            HttpClient client,
            DecoratorType decoratorType,
            HttpClientConfig config
    ) {
        return switch (decoratorType) {
            case TIMEOUT -> new TimeoutHttpClientDecorator(client, config.getTimeoutMs());
            case LOGGING -> new LoggingHttpClientDecorator(client);
            case AUTH -> new AuthHeaderHttpClientDecorator(client, config.getAuthToken());
            case REQUEST_ID -> new RequestIdHttpClientDecorator(client);
            case RETRY -> new RetryHttpClientDecorator(client, config.getMaxRetries(), config.getMetrics());
            case METRICS -> new MetricsHttpClientDecorator(client, config.getMetrics());
        };
    }
}
```

## Why `.distinct()` Is Useful

If someone passes:

```java
List.of(
        DecoratorType.AUTH,
        DecoratorType.AUTH,
        DecoratorType.REQUEST_ID,
        DecoratorType.REQUEST_ID
)
```

without `.distinct()`, the same decorator may be applied multiple times.

Using:

```java
.distinct()
```

ensures each decorator is applied only once.

---

# 12. Runtime Chain Example

Suppose the input list is intentionally random:

```java
List<DecoratorType> decorators = List.of(
        DecoratorType.METRICS,
        DecoratorType.AUTH,
        DecoratorType.TIMEOUT,
        DecoratorType.REQUEST_ID,
        DecoratorType.LOGGING
);
```

The builder sorts it by priority:

```text
TIMEOUT -> LOGGING -> AUTH -> REQUEST_ID -> METRICS
```

It then applies:

```java
client = new TimeoutHttpClientDecorator(baseClient, timeoutMs);
client = new LoggingHttpClientDecorator(client);
client = new AuthHeaderHttpClientDecorator(client, authToken);
client = new RequestIdHttpClientDecorator(client);
client = new MetricsHttpClientDecorator(client, metrics);
```

Final structure:

```text
MetricsHttpClientDecorator
    -> RequestIdHttpClientDecorator
        -> AuthHeaderHttpClientDecorator
            -> LoggingHttpClientDecorator
                -> TimeoutHttpClientDecorator
                    -> BasicHttpClient
```

Runtime execution:

```text
Metrics
    -> RequestId
        -> Auth
            -> Logging
                -> Timeout
                    -> BasicHttpClient
```

This means:

```text
1. Metrics starts tracking.
2. Request ID is added.
3. Authorization header is added.
4. Logging sees headers already added.
5. Timeout checks delay.
6. Basic client executes if timeout passes.
7. Logging logs response.
8. Metrics updates success/failure.
```

---

# 13. Required Test Cases

Your `Main` should test the following:

```text
1. Normal request
2. Timeout request
3. Retry-only request
4. Full-chain retry request
5. Random decorator order
6. Duplicate decorator list
```

---

# 14. Test Case 1: Normal Request

## Purpose

Tests the basic full decorator chain without timeout or retry failure.

## Decorators

```java
List<DecoratorType> decorators = List.of(
        DecoratorType.TIMEOUT,
        DecoratorType.LOGGING,
        DecoratorType.AUTH,
        DecoratorType.REQUEST_ID,
        DecoratorType.METRICS
);
```

## Request

```java
HttpRequest request = new HttpRequest(
        "https://api.example.com/orders",
        "GET",
        new HashMap<>(),
        null,
        1000
);
```

## Expected Behavior

```text
REQUEST-ID is added.
Authorization header is added.
Logging prints request and final headers.
Timeout passes.
BasicHttpClient executes.
Logging prints response status.
Metrics records success.
```

---

# 15. Test Case 2: Timeout Request

## Purpose

Tests that timeout can short-circuit the chain.

## Request

```java
HttpRequest timeoutRequest = new HttpRequest(
        "https://api.example.com/slow",
        "GET",
        new HashMap<>(),
        null,
        5000
);
```

## Expected Behavior

Because:

```text
simulatedDelayMs = 5000
timeoutMs = 3000
```

timeout should happen.

`BasicHttpClient` should not execute.

This line should not appear:

```text
Executing HTTP request: GET https://api.example.com/slow
```

---

# 16. Test Case 3: Retry-Only Request

## Purpose

Tests retry behavior in isolation.

## Base Client

Use:

```java
new UnstableHttpClient()
```

This client fails first two attempts and succeeds on the third.

## Decorators

```java
List<DecoratorType> decorators = List.of(
        DecoratorType.RETRY
);
```

## Request

```java
HttpRequest retryRequest = new HttpRequest(
        "https://api.example.com/retry",
        "POST",
        new HashMap<>(),
        "{\"orderId\":\"ORD-101\"}",
        1000
);
```

## Expected Behavior

```text
Attempt 1 fails.
Attempt 2 fails.
Attempt 3 succeeds.
Retryable failures = 2.
```

---

# 17. Test Case 4: Full-Chain Retry Request

## Purpose

Tests retry inside the full decorator chain.

## Base Client

Use:

```java
new UnstableHttpClient()
```

## Decorators

```java
List<DecoratorType> decorators = List.of(
        DecoratorType.TIMEOUT,
        DecoratorType.LOGGING,
        DecoratorType.AUTH,
        DecoratorType.REQUEST_ID,
        DecoratorType.RETRY,
        DecoratorType.METRICS
);
```

## Expected Behavior

```text
Metrics starts.
Retry attempt 1 starts.
Request ID added.
Authorization header added.
Logging logs request.
Timeout passes.
UnstableHttpClient fails.
Retry catches failure.

Retry attempt 2 starts.
Request ID added.
Authorization header added.
Logging logs request.
Timeout passes.
UnstableHttpClient fails.
Retry catches failure.

Retry attempt 3 starts.
Request ID added.
Authorization header added.
Logging logs request.
Timeout passes.
UnstableHttpClient succeeds.

Metrics records success.
Retryable failures = 2.
```

---

# 18. Test Case 5: Random Decorator Order

## Purpose

Tests that the builder controls order even when the user gives decorators in a random order.

## Input List

```java
List<DecoratorType> decorators = List.of(
        DecoratorType.METRICS,
        DecoratorType.REQUEST_ID,
        DecoratorType.AUTH,
        DecoratorType.TIMEOUT,
        DecoratorType.LOGGING
);
```

## Expected Behavior

The builder should sort this internally into:

```text
TIMEOUT -> LOGGING -> AUTH -> REQUEST_ID -> METRICS
```

So behavior should still be stable.

Output should still show:

```text
REQUEST-ID added before LOGGING
AUTH added before LOGGING
LOG: Headers contains X-Request-Id and Authorization
```

---

# 19. Test Case 6: Duplicate Decorator List

## Purpose

Tests that duplicates are not applied multiple times.

## Input List

```java
List<DecoratorType> decorators = List.of(
        DecoratorType.TIMEOUT,
        DecoratorType.LOGGING,
        DecoratorType.AUTH,
        DecoratorType.AUTH,
        DecoratorType.REQUEST_ID,
        DecoratorType.REQUEST_ID,
        DecoratorType.METRICS
);
```

## Expected Behavior

If the builder uses:

```java
.distinct()
```

then duplicate decorators are ignored.

So `AUTH` should run once, and `REQUEST_ID` should run once.

---

# 20. Common Mistakes to Avoid

## Mistake 1: Still Manually Building the Chain in Main

Wrong for Module 7:

```java
client = new TimeoutHttpClientDecorator(client, 3000);
client = new LoggingHttpClientDecorator(client);
client = new AuthHeaderHttpClientDecorator(client, "Bearer test-token");
client = new RequestIdHttpClientDecorator(client);
client = new MetricsHttpClientDecorator(client, metrics);
```

Correct for Module 7:

```java
HttpClient client = DecoratorChainBuilder.build(
        new BasicHttpClient(),
        decorators,
        config
);
```

---

## Mistake 2: Not Sorting by Priority

If you do not sort by priority, then random input order can change behavior.

Required:

```java
.sorted(Comparator.comparingInt(DecoratorType::getPriority))
```

---

## Mistake 3: Not Removing Duplicates

Without:

```java
.distinct()
```

duplicate decorators can be applied multiple times.

---

## Mistake 4: Builder Executing the Request

The builder should not call:

```java
client.execute(request)
```

The builder should only return the decorated client.

Correct responsibility:

```text
Input: base client + decorator list + config
Output: final decorated HttpClient
```

---

## Mistake 5: Missing Full-Chain Retry Test

Retry-only is not enough for the final module.

You should also test:

```text
Retry + Timeout + Logging + Auth + RequestId + Metrics
```

---

# 21. Scoring Rubric

| Area | Marks |
|---|---:|
| Reuses Module 6 classes correctly | 1 |
| Correct `DecoratorType` enum with priority | 1 |
| Correct `HttpClientConfig` | 1.5 |
| Correct `DecoratorChainBuilder` | 2 |
| Correct priority-based order handling | 1 |
| Normal request test | 1 |
| Timeout request test | 1 |
| Retry-only test | 1 |
| Full-chain retry test | 1 |
| Random order and duplicate handling | 0.5 |
| Code readability and structure | 0.5 |

Total: **11 marks normalized to 10**

---

# 22. Final Objective

By the end of Module 7, you should be able to say:

```text
I can build a Decorator Pattern pipeline dynamically from configuration.
```

That means you understand Decorator Pattern beyond simple examples.

You have practiced:

```text
Basic decoration
Multiple decorators
Order-sensitive behavior
Priority-based ordering
Short-circuiting decorators
Metrics decorators
Retry decorators
Config-driven chain creation
Factory-style builder
Production-style backend design
```

This is the final step before completing the Decorator Pattern practice series.

---

# 23. Final Mental Model

Manual Module 6 style:

```java
HttpClient client = new BasicHttpClient();

client = new TimeoutHttpClientDecorator(client, 3000);
client = new LoggingHttpClientDecorator(client);
client = new AuthHeaderHttpClientDecorator(client, "Bearer test-token");
client = new RequestIdHttpClientDecorator(client);
client = new MetricsHttpClientDecorator(client, metrics);
```

Config-driven Module 7 style:

```java
List<DecoratorType> decorators = List.of(
        DecoratorType.TIMEOUT,
        DecoratorType.LOGGING,
        DecoratorType.AUTH,
        DecoratorType.REQUEST_ID,
        DecoratorType.METRICS
);

HttpClient client = DecoratorChainBuilder.build(
        new BasicHttpClient(),
        decorators,
        config
);
```

This creates:

```text
MetricsHttpClientDecorator(
    RequestIdHttpClientDecorator(
        AuthHeaderHttpClientDecorator(
            LoggingHttpClientDecorator(
                TimeoutHttpClientDecorator(
                    BasicHttpClient
                )
            )
        )
    )
)
```

In simple words:

> Module 7 teaches you how to build a Decorator Pattern pipeline dynamically, safely, and predictably using configuration and priority-based ordering.
