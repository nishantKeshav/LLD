# Decorator Design Pattern Practice — Module 6

## HTTP Client Decorator

### Difficulty Level

**Advanced+**

This is the sixth practice module for the **Decorator Design Pattern**.

In the earlier modules, you practiced decorators through simpler and then increasingly backend-oriented examples:

```text
Module 1: Coffee add-ons
Module 2: Pizza toppings
Module 3: Notification sender pipeline
Module 4: Text/File reader transformation pipeline
Module 5: Payment processor backend pipeline
```

Module 6 moves into a realistic backend engineering scenario:

```text
HTTP Client Pipeline
```

In real backend systems, one service often calls another service through HTTP APIs. A service may call:

```text
Payment APIs
Order APIs
User APIs
Notification APIs
Banking APIs
Third-party vendor APIs
Internal microservices
```

A simple HTTP client can just send a request and return a response. But production HTTP clients usually need many additional behaviors around the request, such as:

```text
Authorization headers
Request IDs / correlation IDs
Logging
Timeout handling
Retry
Metrics
```

The goal of this module is to implement these behaviors using the **Decorator Design Pattern**.

---

# 1. Problem Introduction

You are building a small backend-style HTTP client system.

The base client should only execute an HTTP request.

However, in real backend applications, every outgoing HTTP request usually needs extra features:

```text
1. Add Authorization header before sending the request
2. Add Request ID for tracing
3. Log request and response details
4. Stop the request if it exceeds timeout rules
5. Retry failed requests
6. Track metrics such as success and failure counts
```

Instead of putting all these responsibilities into one huge `BasicHttpClient`, you will create separate decorators.

Each decorator will wrap an existing `HttpClient` and add exactly one extra responsibility.

---

# 2. Why This Problem Matters

This problem is important because Decorator Pattern is commonly used in backend and infrastructure code.

Real backend HTTP clients are often wrapped with behaviors such as:

```text
Authentication
Tracing
Request/response logging
Retry policies
Timeout policies
Circuit breakers
Rate limiting
Metrics
Observability
```

For example, a real API client may behave like this internally:

```text
Metrics wrapper
    Retry wrapper
        Timeout wrapper
            Logging wrapper
                Auth wrapper
                    Real HTTP client
```

Your implementation in this module simulates that style using plain Java.

---

# 3. Context and Real-World Analogy

Imagine your backend service needs to call:

```text
GET https://api.example.com/orders
```

A basic client can do this:

```text
Send GET request
Return response
```

But a production-like client needs more:

```text
Add Authorization header:
Authorization: Bearer test-token

Add Request ID:
X-Request-Id: some-uuid

Log outgoing request:
LOG: Sending GET request to https://api.example.com/orders

Apply timeout:
If request takes too long, return timeout response

Retry temporary failures:
Attempt 1 failed
Attempt 2 failed
Attempt 3 succeeded

Track metrics:
Total requests = 1
Success responses = 1
Failed responses = 0
```

Putting all of this in one class would make that class messy. Decorator Pattern keeps the design clean.

---

# 4. Potential Impact of This Design

Using Decorator Pattern for an HTTP client gives several benefits.

## 4.1 Clean Separation of Responsibilities

Each class does one thing:

```text
BasicHttpClient -> executes request
AuthHeaderHttpClientDecorator -> adds Authorization header
RequestIdHttpClientDecorator -> adds X-Request-Id
LoggingHttpClientDecorator -> logs request and response
TimeoutHttpClientDecorator -> applies timeout rule
RetryHttpClientDecorator -> retries failed calls
MetricsHttpClientDecorator -> updates metrics
```

This follows the **Single Responsibility Principle**.

## 4.2 Easy Extensibility

If tomorrow you want to add:

```text
RateLimitHttpClientDecorator
CircuitBreakerHttpClientDecorator
CacheHttpClientDecorator
CompressionHttpClientDecorator
```

you can add new decorators without modifying the old classes.

This follows the **Open/Closed Principle**.

## 4.3 Flexible Runtime Composition

You can decide at runtime which decorators should be applied:

```java
HttpClient client = new BasicHttpClient();

if (authEnabled) {
    client = new AuthHeaderHttpClientDecorator(client, token);
}

if (loggingEnabled) {
    client = new LoggingHttpClientDecorator(client);
}

if (metricsEnabled) {
    client = new MetricsHttpClientDecorator(client, metrics);
}
```

This makes the design flexible.

---

# 5. Core Problem Statement

Build an HTTP client system using the **Decorator Design Pattern**.

The base HTTP client should only execute the request.

Extra behaviors should be added using decorators:

```text
1. Add Authorization header
2. Add Request ID header
3. Log request and response
4. Apply timeout rule
5. Retry failed requests
6. Track metrics
```

You must not put all logic inside `BasicHttpClient`.

Each decorator should handle exactly one responsibility.

---

# 6. Decorator Pattern Mapping

| Decorator Pattern Term | Meaning in Module 6 |
|---|---|
| Component | `HttpClient` interface |
| Concrete Component | `BasicHttpClient` |
| Base Decorator | `HttpClientDecorator` |
| Concrete Decorators | `AuthHeaderHttpClientDecorator`, `RequestIdHttpClientDecorator`, `LoggingHttpClientDecorator`, `TimeoutHttpClientDecorator`, `RetryHttpClientDecorator`, `MetricsHttpClientDecorator` |
| Request Object | `HttpRequest` |
| Response Object | `HttpResponse` |
| Metrics Object | `HttpClientMetrics` |
| Test Client | `UnstableHttpClient` |

---

# 7. Required Classes

You need to implement:

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
Main
```

---

# 8. `HttpClient` Interface

Create:

```java
public interface HttpClient {
    HttpResponse execute(HttpRequest request);
}
```

## Purpose

This is the **Component interface**.

Both the base client and every decorator must implement this interface.

Because all decorators are also `HttpClient`, this works:

```java
HttpClient client = new BasicHttpClient();

client = new AuthHeaderHttpClientDecorator(client, "Bearer test-token");
client = new LoggingHttpClientDecorator(client);
client = new MetricsHttpClientDecorator(client, metrics);
```

The variable remains type:

```java
HttpClient
```

but behavior increases as wrappers are added.

---

# 9. `HttpRequest`

This class represents an outgoing HTTP request.

It should contain:

```text
url
method
headers
body
simulatedDelayMs
```

## Why headers should be a Map

Headers should be:

```java
Map<String, String>
```

not:

```java
String
```

because decorators need to add headers:

```java
request.addHeader("Authorization", "Bearer test-token");
request.addHeader("X-Request-Id", requestId);
```

## Why `simulatedDelayMs` exists

We are not making real network calls.

So to test timeout behavior, we simulate request delay using:

```java
simulatedDelayMs
```

Example:

```text
simulatedDelayMs = 1000
timeoutMs = 3000
```

This request is allowed.

But:

```text
simulatedDelayMs = 5000
timeoutMs = 3000
```

This request should be blocked by the timeout decorator.

## Expected Code

```java
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final String body;
    private final int simulatedDelayMs;

    public HttpRequest(
            String url,
            String method,
            Map<String, String> headers,
            String body,
            int simulatedDelayMs
    ) {
        this.url = url;
        this.method = method;
        this.headers = headers == null ? new HashMap<>() : new HashMap<>(headers);
        this.body = body;
        this.simulatedDelayMs = simulatedDelayMs;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public String getBody() {
        return body;
    }

    public int getSimulatedDelayMs() {
        return simulatedDelayMs;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
}
```

---

# 10. `HttpResponse`

Create:

```java
public class HttpResponse {

    private final int statusCode;
    private final String body;
    private final boolean success;

    public HttpResponse(int statusCode, String body, boolean success) {
        this.statusCode = statusCode;
        this.body = body;
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public boolean isSuccess() {
        return success;
    }
}
```

## Purpose

This represents the response from the HTTP client.

Examples:

```java
new HttpResponse(200, "Response from https://api.example.com/orders", true);
new HttpResponse(408, "Request timed out", false);
new HttpResponse(500, "Request failed after retries", false);
```

---

# 11. `BasicHttpClient`

Create:

```java
public class BasicHttpClient implements HttpClient {

    @Override
    public HttpResponse execute(HttpRequest request) {
        System.out.println("Executing HTTP request: "
                + request.getMethod()
                + " "
                + request.getUrl());

        return new HttpResponse(
                200,
                "Response from " + request.getUrl(),
                true
        );
    }
}
```

## Purpose

This is the **Concrete Component**.

It only executes the request.

It should not know about:

```text
Authorization
Request ID
Logging
Timeout
Retry
Metrics
```

## Important Rule

`BasicHttpClient` should not wrap another `HttpClient`.

This is wrong inside `BasicHttpClient`:

```java
private final HttpClient httpClient;
```

Why?

Because `BasicHttpClient` is the starting point. Decorators wrap the basic client, not the other way around.

---

# 12. `HttpClientDecorator`

Create:

```java
public abstract class HttpClientDecorator implements HttpClient {

    private final HttpClient httpClient;

    protected HttpClientDecorator(HttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("HttpClient cannot be null.");
        }

        this.httpClient = httpClient;
    }

    protected HttpClient getWrappedClient() {
        return httpClient;
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        return httpClient.execute(request);
    }
}
```

## Purpose

This is the **Base Decorator**.

It stores the wrapped `HttpClient`.

Child decorators can add behavior before or after:

```java
super.execute(request);
```

---

# 13. `AuthHeaderHttpClientDecorator`

Create:

```java
public class AuthHeaderHttpClientDecorator extends HttpClientDecorator {

    private final String token;

    public AuthHeaderHttpClientDecorator(HttpClient httpClient, String token) {
        super(httpClient);

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Auth token cannot be null or blank.");
        }

        this.token = token;
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        request.addHeader("Authorization", token);

        System.out.println("AUTH: Authorization header added");

        return super.execute(request);
    }
}
```

## Purpose

This decorator adds:

```text
Authorization: Bearer test-token
```

to the request headers.

## Important

Do not only print the token.

Wrong:

```java
System.out.println("Authorization: " + token);
```

Correct:

```java
request.addHeader("Authorization", token);
```

---

# 14. `RequestIdHttpClientDecorator`

Create:

```java
import java.util.UUID;

public class RequestIdHttpClientDecorator extends HttpClientDecorator {

    public RequestIdHttpClientDecorator(HttpClient httpClient) {
        super(httpClient);
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        String requestId = UUID.randomUUID().toString();

        request.addHeader("X-Request-Id", requestId);

        System.out.println("REQUEST-ID: Added request id " + requestId);

        return super.execute(request);
    }
}
```

## Purpose

This decorator adds a unique request ID header:

```text
X-Request-Id: <uuid>
```

This is useful for tracing requests across multiple services.

---

# 15. `LoggingHttpClientDecorator`

Create:

```java
public class LoggingHttpClientDecorator extends HttpClientDecorator {

    public LoggingHttpClientDecorator(HttpClient httpClient) {
        super(httpClient);
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        System.out.println("LOG: Sending "
                + request.getMethod()
                + " request to "
                + request.getUrl());

        System.out.println("LOG: Headers: " + request.getHeaders());

        HttpResponse response = super.execute(request);

        System.out.println("LOG: Received response status "
                + response.getStatusCode());

        return response;
    }
}
```

## Purpose

This decorator logs:

```text
HTTP method
URL
headers
response status
```

---

# 16. `TimeoutHttpClientDecorator`

Create:

```java
public class TimeoutHttpClientDecorator extends HttpClientDecorator {

    private final int timeoutMs;

    public TimeoutHttpClientDecorator(HttpClient httpClient, int timeoutMs) {
        super(httpClient);

        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("Timeout must be greater than zero.");
        }

        this.timeoutMs = timeoutMs;
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        if (request.getSimulatedDelayMs() > timeoutMs) {
            System.out.println("TIMEOUT: Request timed out before execution");

            return new HttpResponse(
                    408,
                    "Request timed out",
                    false
            );
        }

        System.out.println("TIMEOUT: Request within timeout limit");

        return super.execute(request);
    }
}
```

## Purpose

This decorator blocks slow requests.

If:

```text
request simulated delay > configured timeout
```

then it returns:

```text
408 Request timed out
```

and does not call the wrapped client.

This is called **short-circuiting the chain**.

---

# 17. `RetryHttpClientDecorator`

Create:

```java
public class RetryHttpClientDecorator extends HttpClientDecorator {

    private final int maxRetries;
    private final HttpClientMetrics metrics;

    public RetryHttpClientDecorator(HttpClient httpClient, int maxRetries) {
        this(httpClient, maxRetries, null);
    }

    public RetryHttpClientDecorator(HttpClient httpClient, int maxRetries, HttpClientMetrics metrics) {
        super(httpClient);

        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative.");
        }

        this.maxRetries = maxRetries;
        this.metrics = metrics;
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        int attempts = 0;
        int totalAttempts = maxRetries + 1;

        HttpResponse lastResponse = null;
        Exception lastException = null;

        while (attempts < totalAttempts) {
            attempts++;

            try {
                System.out.println("RETRY: Attempt "
                        + attempts
                        + " for "
                        + request.getMethod()
                        + " "
                        + request.getUrl());

                HttpResponse response = super.execute(request);

                if (response.isSuccess()) {
                    return response;
                }

                lastResponse = response;

                System.out.println("RETRY: Attempt "
                        + attempts
                        + " returned status "
                        + response.getStatusCode());

                if (attempts < totalAttempts) {
                    incrementRetryableFailure();
                }

            } catch (Exception e) {
                lastException = e;

                System.out.println("RETRY: Attempt "
                        + attempts
                        + " failed: "
                        + e.getMessage());

                if (attempts < totalAttempts) {
                    incrementRetryableFailure();
                }
            }
        }

        if (lastResponse != null) {
            return lastResponse;
        }

        return new HttpResponse(
                500,
                "Request failed after retries: " + lastException.getMessage(),
                false
        );
    }

    private void incrementRetryableFailure() {
        if (metrics != null) {
            metrics.incrementRetryableFailures();
        }
    }
}
```

## Purpose

This decorator retries failed HTTP calls.

Retry should happen when:

```text
1. Wrapped client throws an exception
2. Wrapped client returns an unsuccessful response
```

If:

```text
maxRetries = 2
```

then:

```text
total attempts = 3
```

because:

```text
1 original attempt + 2 retries
```

---

# 18. `HttpClientMetrics`

Create:

```java
public class HttpClientMetrics {

    private int totalRequests;
    private int successResponses;
    private int failedResponses;
    private int retryableFailures;

    public void incrementTotalRequests() {
        totalRequests++;
    }

    public void incrementSuccessResponses() {
        successResponses++;
    }

    public void incrementFailedResponses() {
        failedResponses++;
    }

    public void incrementRetryableFailures() {
        retryableFailures++;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getSuccessResponses() {
        return successResponses;
    }

    public int getFailedResponses() {
        return failedResponses;
    }

    public int getRetryableFailures() {
        return retryableFailures;
    }
}
```

## Purpose

Tracks HTTP client metrics:

```text
totalRequests
successResponses
failedResponses
retryableFailures
```

---

# 19. `MetricsHttpClientDecorator`

Create:

```java
public class MetricsHttpClientDecorator extends HttpClientDecorator {

    private final HttpClientMetrics metrics;

    public MetricsHttpClientDecorator(HttpClient httpClient, HttpClientMetrics metrics) {
        super(httpClient);

        if (metrics == null) {
            throw new IllegalArgumentException("HttpClientMetrics cannot be null.");
        }

        this.metrics = metrics;
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        metrics.incrementTotalRequests();

        try {
            HttpResponse response = super.execute(request);

            if (response.isSuccess()) {
                metrics.incrementSuccessResponses();
            } else {
                metrics.incrementFailedResponses();
            }

            System.out.println("METRICS: HTTP metrics updated");

            return response;

        } catch (Exception e) {
            metrics.incrementFailedResponses();
            System.out.println("METRICS: HTTP metrics updated after failure");
            throw e;
        }
    }
}
```

## Purpose

Tracks total requests and final response status.

---

# 20. `UnstableHttpClient`

Create:

```java
public class UnstableHttpClient implements HttpClient {

    private int attemptCount = 0;

    @Override
    public HttpResponse execute(HttpRequest request) {
        attemptCount++;

        if (attemptCount < 3) {
            throw new RuntimeException("Temporary external service failure");
        }

        System.out.println("Executing HTTP request after retries: "
                + request.getMethod()
                + " "
                + request.getUrl());

        return new HttpResponse(
                200,
                "Successful response after retries",
                true
        );
    }
}
```

## Purpose

This is a test client.

It fails first two times and succeeds on the third attempt.

It is used to verify that retry logic works.

---

# 21. Main Class

Create:

```java
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        runNormalRequestExample();
        System.out.println();

        runTimeoutRequestExample();
        System.out.println();

        runRetryRequestExample();
        System.out.println();

        runFullChainRetryRequestExample();
    }

    private static void runNormalRequestExample() {
        System.out.println("Module 6 — HTTP Client Decorator Ex-1: Normal Request");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClient client = buildStandardClient(metrics);

        HttpRequest request = new HttpRequest(
                "https://api.example.com/orders",
                "GET",
                new HashMap<>(),
                null,
                1000
        );

        HttpResponse response = client.execute(request);

        printResponse(response);
        printMetrics(metrics);
    }

    private static void runTimeoutRequestExample() {
        System.out.println("Module 6 — HTTP Client Decorator Ex-2: Timeout Request");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClient client = buildStandardClient(metrics);

        HttpRequest timeoutRequest = new HttpRequest(
                "https://api.example.com/slow",
                "GET",
                new HashMap<>(),
                null,
                5000
        );

        HttpResponse response = client.execute(timeoutRequest);

        printResponse(response);
        printMetrics(metrics);
    }

    private static void runRetryRequestExample() {
        System.out.println("Module 6 — HTTP Client Decorator Ex-3: Retry Request Only");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClient client = new UnstableHttpClient();
        client = new RetryHttpClientDecorator(client, 2, metrics);

        HttpRequest retryRequest = new HttpRequest(
                "https://api.example.com/retry",
                "POST",
                new HashMap<>(),
                "{\"orderId\":\"ORD-101\"}",
                1000
        );

        HttpResponse response = client.execute(retryRequest);

        printResponse(response);
        printMetrics(metrics);
    }

    private static void runFullChainRetryRequestExample() {
        System.out.println("Module 6 — HTTP Client Decorator Ex-4: Full Chain Retry Request");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClient client = new UnstableHttpClient();

        client = new TimeoutHttpClientDecorator(client, 3000);
        client = new LoggingHttpClientDecorator(client);
        client = new AuthHeaderHttpClientDecorator(client, "Bearer test-token");
        client = new RequestIdHttpClientDecorator(client);
        client = new RetryHttpClientDecorator(client, 2, metrics);
        client = new MetricsHttpClientDecorator(client, metrics);

        HttpRequest retryRequest = new HttpRequest(
                "https://api.example.com/full-chain-retry",
                "POST",
                new HashMap<>(),
                "{\"orderId\":\"ORD-202\"}",
                1000
        );

        HttpResponse response = client.execute(retryRequest);

        printResponse(response);
        printMetrics(metrics);
    }

    private static HttpClient buildStandardClient(HttpClientMetrics metrics) {
        HttpClient client = new BasicHttpClient();

        client = new TimeoutHttpClientDecorator(client, 3000);
        client = new LoggingHttpClientDecorator(client);
        client = new AuthHeaderHttpClientDecorator(client, "Bearer test-token");
        client = new RequestIdHttpClientDecorator(client);
        client = new MetricsHttpClientDecorator(client, metrics);

        return client;
    }

    private static void printResponse(HttpResponse response) {
        System.out.println("Final Response: " + response.getBody());
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Success: " + response.isSuccess());
    }

    private static void printMetrics(HttpClientMetrics metrics) {
        System.out.println("Total Requests: " + metrics.getTotalRequests());
        System.out.println("Success Responses: " + metrics.getSuccessResponses());
        System.out.println("Failed Responses: " + metrics.getFailedResponses());
        System.out.println("Retryable Failures: " + metrics.getRetryableFailures());
    }
}
```

---

# 22. Expected Outputs

## Example 1 — Normal Request

Expected style:

```text
Module 6 — HTTP Client Decorator Ex-1: Normal Request
REQUEST-ID: Added request id <uuid>
AUTH: Authorization header added
LOG: Sending GET request to https://api.example.com/orders
LOG: Headers: {X-Request-Id=<uuid>, Authorization=Bearer test-token}
TIMEOUT: Request within timeout limit
Executing HTTP request: GET https://api.example.com/orders
LOG: Received response status 200
METRICS: HTTP metrics updated
Final Response: Response from https://api.example.com/orders
Status Code: 200
Success: true
Total Requests: 1
Success Responses: 1
Failed Responses: 0
Retryable Failures: 0
```

---

## Example 2 — Timeout Request

Expected style:

```text
Module 6 — HTTP Client Decorator Ex-2: Timeout Request
REQUEST-ID: Added request id <uuid>
AUTH: Authorization header added
LOG: Sending GET request to https://api.example.com/slow
LOG: Headers: {X-Request-Id=<uuid>, Authorization=Bearer test-token}
TIMEOUT: Request timed out before execution
LOG: Received response status 408
METRICS: HTTP metrics updated
Final Response: Request timed out
Status Code: 408
Success: false
Total Requests: 1
Success Responses: 0
Failed Responses: 1
Retryable Failures: 0
```

Important:

This should not appear:

```text
Executing HTTP request: GET https://api.example.com/slow
```

Because timeout stops the chain.

---

## Example 3 — Retry Only

Expected style:

```text
Module 6 — HTTP Client Decorator Ex-3: Retry Request Only
RETRY: Attempt 1 for POST https://api.example.com/retry
RETRY: Attempt 1 failed: Temporary external service failure
RETRY: Attempt 2 for POST https://api.example.com/retry
RETRY: Attempt 2 failed: Temporary external service failure
RETRY: Attempt 3 for POST https://api.example.com/retry
Executing HTTP request after retries: POST https://api.example.com/retry
Final Response: Successful response after retries
Status Code: 200
Success: true
Total Requests: 0
Success Responses: 0
Failed Responses: 0
Retryable Failures: 2
```

Note:

`Total Requests` is `0` here because this example does not use `MetricsHttpClientDecorator`.

It only passes metrics into retry for retryable failure count.

---

## Example 4 — Full Chain Retry

Expected style:

```text
Module 6 — HTTP Client Decorator Ex-4: Full Chain Retry Request
RETRY: Attempt 1 for POST https://api.example.com/full-chain-retry
REQUEST-ID: Added request id <uuid>
AUTH: Authorization header added
LOG: Sending POST request to https://api.example.com/full-chain-retry
LOG: Headers: {X-Request-Id=<uuid>, Authorization=Bearer test-token}
TIMEOUT: Request within timeout limit
RETRY: Attempt 1 failed: Temporary external service failure

RETRY: Attempt 2 for POST https://api.example.com/full-chain-retry
REQUEST-ID: Added request id <uuid>
AUTH: Authorization header added
LOG: Sending POST request to https://api.example.com/full-chain-retry
LOG: Headers: {X-Request-Id=<uuid>, Authorization=Bearer test-token}
TIMEOUT: Request within timeout limit
RETRY: Attempt 2 failed: Temporary external service failure

RETRY: Attempt 3 for POST https://api.example.com/full-chain-retry
REQUEST-ID: Added request id <uuid>
AUTH: Authorization header added
LOG: Sending POST request to https://api.example.com/full-chain-retry
LOG: Headers: {X-Request-Id=<uuid>, Authorization=Bearer test-token}
TIMEOUT: Request within timeout limit
Executing HTTP request after retries: POST https://api.example.com/full-chain-retry
LOG: Received response status 200
METRICS: HTTP metrics updated
Final Response: Successful response after retries
Status Code: 200
Success: true
Total Requests: 1
Success Responses: 1
Failed Responses: 0
Retryable Failures: 2
```

---

# 23. Important Challenge: Decorator Order

Decorator order is the biggest concept in this module.

This order:

```java
client = new AuthHeaderHttpClientDecorator(client, "Bearer test-token");
client = new RequestIdHttpClientDecorator(client);
client = new LoggingHttpClientDecorator(client);
```

creates:

```text
Logging
    -> RequestId
        -> Auth
            -> Basic
```

So logging runs before request ID and auth headers are added.

That may produce:

```text
LOG: Headers: {}
```

But this order:

```java
client = new LoggingHttpClientDecorator(client);
client = new AuthHeaderHttpClientDecorator(client, "Bearer test-token");
client = new RequestIdHttpClientDecorator(client);
```

creates:

```text
RequestId
    -> Auth
        -> Logging
            -> Basic
```

So request ID and auth are added before logging.

That means logging can show:

```text
LOG: Headers: {X-Request-Id=..., Authorization=Bearer test-token}
```

This is why order matters.

---

# 24. Common Mistakes to Avoid

## Mistake 1: Headers as String

Wrong:

```java
private final String headers;
```

Correct:

```java
private final Map<String, String> headers;
```

---

## Mistake 2: Printing header but not adding it

Wrong:

```java
System.out.println("Authorization: " + token);
```

Correct:

```java
request.addHeader("Authorization", token);
```

---

## Mistake 3: Basic client wrapping another client

Wrong:

```java
public class BasicHttpClient implements HttpClient {
    private final HttpClient httpClient;
}
```

Correct:

```text
Only decorators wrap another HttpClient.
BasicHttpClient is the base component.
```

---

## Mistake 4: Timeout still executing wrapped client

Wrong:

```java
if (timeout) {
    return super.execute(request);
}
```

Correct:

```java
if (timeout) {
    return new HttpResponse(408, "Request timed out", false);
}
```

---

## Mistake 5: Retry only handling exceptions

This module expects retry for:

```text
exceptions
failed responses
```

So this should be checked:

```java
if (!response.isSuccess()) {
    // retry
}
```

---

## Mistake 6: Not testing full chain retry

Retry alone is useful.

But to prove full understanding, also test retry with:

```text
RequestId
Auth
Logging
Timeout
Metrics
```

---

# 25. Scoring Rubric

| Area | Marks |
|---|---:|
| `HttpClient` interface | 1 |
| `HttpRequest` and `HttpResponse` | 1.5 |
| `BasicHttpClient` | 1 |
| `HttpClientDecorator` | 1.5 |
| Auth and Request ID decorators | 1.5 |
| Logging decorator | 1 |
| Timeout decorator | 1 |
| Retry decorator | 1.5 |
| Metrics decorator and metrics class | 1 |
| Main tests and readability | 0.5 |

Total: **10 marks**

---

# 26. Final Goal

By the end of Module 6, your HTTP client should support:

```text
Basic HTTP execution
Authorization header
Request ID / tracing
Logging
Timeout short-circuiting
Retry on failure
Metrics tracking
```

The final mental model is:

```java
HttpClient client = new BasicHttpClient();

client = new TimeoutHttpClientDecorator(client, 3000);
client = new LoggingHttpClientDecorator(client);
client = new AuthHeaderHttpClientDecorator(client, "Bearer test-token");
client = new RequestIdHttpClientDecorator(client);
client = new MetricsHttpClientDecorator(client, metrics);
```

This means:

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

Decorator Pattern is:

```text
Same interface
+ wrapped object
+ one extra behavior
+ delegation
```

In simple words:

> Decorator Pattern lets you add backend HTTP client features like auth, request ID, logging, timeout, retry, and metrics without modifying the original HTTP client.
