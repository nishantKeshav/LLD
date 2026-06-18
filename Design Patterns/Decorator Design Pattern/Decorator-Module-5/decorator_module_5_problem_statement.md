# Decorator Design Pattern Practice — Module 5

## Payment Processor Decorator

### Difficulty Level

**Advanced**

This is the fifth practice module for the **Decorator Design Pattern**.

In earlier modules, you practiced decorators using:

```text
Module 1: Coffee add-ons
Module 2: Pizza toppings
Module 3: Notification sender pipeline
Module 4: Text/File reader transformation pipeline
```

Module 5 moves into a more realistic backend-style use case:

```text
Payment Processing
```

In real backend systems, payment processing is rarely just one simple method call.

A payment flow usually has multiple additional responsibilities around the core payment action:

```text
Validation
Fraud check
Audit logging
Metrics
Retry
```

The goal of this module is to implement those responsibilities using the **Decorator Design Pattern**, while keeping the base payment processor simple.

---

## 1. Problem Statement

You are building a backend payment processing system.

The system should process a payment request and return a payment response.

The base processor should only do the core payment processing.

Extra behavior should be added using decorators.

The extra behaviors are:

```text
1. Validate the payment request
2. Check whether the payment is suspicious
3. Log audit information before and after payment processing
4. Track payment metrics
5. Retry payment processing if temporary failures happen
```

You must implement this using the **Decorator Design Pattern**.

Do not put all behavior inside `BasicPaymentProcessor`.

Each decorator should have one responsibility.

---

## 2. Real Backend Analogy

Imagine a user is making a payment from your application.

The basic action is:

```text
Process payment
```

But a real payment backend may need to do many things around that action:

```text
Check if the request is valid
Check if the amount is suspicious
Call payment gateway
Log the request and response
Track success and failure count
Retry temporary gateway failures
Return a clean response
```

A bad design would put everything inside one class:

```java
public class BasicPaymentProcessor {
    public PaymentResponse process(PaymentRequest request) {
        // validate request
        // check fraud
        // log audit start
        // call payment gateway
        // update metrics
        // retry failures
        // log audit end
        // return response
    }
}
```

This makes the class too large and gives it too many responsibilities.

Decorator Pattern solves this by separating each responsibility into its own wrapper.

---

## 3. Decorator Pattern Mapping

| Decorator Pattern Term | Meaning in Module 5 |
|---|---|
| Component | `PaymentProcessor` interface |
| Concrete Component | `BasicPaymentProcessor` |
| Base Decorator | `PaymentProcessorDecorator` |
| Concrete Decorators | `ValidationPaymentDecorator`, `FraudCheckPaymentDecorator`, `AuditLogPaymentDecorator`, `MetricsPaymentDecorator`, `RetryPaymentDecorator` |
| Request Object | `PaymentRequest` |
| Response Object | `PaymentResponse` |
| Metrics Object | `PaymentMetrics` |
| Optional Test Processor | `UnstablePaymentProcessor` |

---

## 4. Key Learning of This Module

The main learning is:

```text
Decorator Pattern can be used to build backend processing pipelines.
```

Instead of this:

```java
BasicPaymentProcessor processor = new BasicPaymentProcessor();
```

You build:

```java
PaymentProcessor processor = new BasicPaymentProcessor();

processor = new ValidationPaymentDecorator(processor);
processor = new FraudCheckPaymentDecorator(processor);
processor = new AuditLogPaymentDecorator(processor);
processor = new MetricsPaymentDecorator(processor, metrics);
```

This creates a chain:

```text
MetricsPaymentDecorator
    wraps AuditLogPaymentDecorator
        wraps FraudCheckPaymentDecorator
            wraps ValidationPaymentDecorator
                wraps BasicPaymentProcessor
```

When you call:

```java
processor.process(request);
```

the call enters the outermost decorator first and then moves inward.

---

## 5. Required Classes

You need to create:

```text
PaymentProcessor
PaymentRequest
PaymentResponse
BasicPaymentProcessor
PaymentProcessorDecorator
ValidationPaymentDecorator
FraudCheckPaymentDecorator
AuditLogPaymentDecorator
PaymentMetrics
MetricsPaymentDecorator
RetryPaymentDecorator
UnstablePaymentProcessor
Main
```

---

## 6. Class 1 — `PaymentProcessor` Interface

Create:

```java
public interface PaymentProcessor {
    PaymentResponse process(PaymentRequest request);
}
```

### Purpose

This is the **Component interface**.

Both the base processor and all decorators must implement this interface.

Because all decorators are also `PaymentProcessor`, you can keep writing:

```java
PaymentProcessor processor = new BasicPaymentProcessor();

processor = new ValidationPaymentDecorator(processor);
processor = new FraudCheckPaymentDecorator(processor);
```

The variable type remains:

```java
PaymentProcessor
```

But the behavior keeps increasing as decorators wrap it.

---

## 7. Class 2 — `PaymentRequest`

Create:

```java
public class PaymentRequest {

    private final String paymentId;
    private final String customerId;
    private final double amount;
    private final String paymentMode;
    private final String currency;

    public PaymentRequest(
            String paymentId,
            String customerId,
            double amount,
            String paymentMode,
            String currency
    ) {
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.currency = currency;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getCurrency() {
        return currency;
    }
}
```

### Purpose

This class represents the payment request coming into the system.

Example:

```java
PaymentRequest request = new PaymentRequest(
        "PAY-101",
        "CUST-101",
        2500.0,
        "UPI",
        "INR"
);
```

---

## 8. Class 3 — `PaymentResponse`

Create:

```java
public class PaymentResponse {

    private final String paymentId;
    private final boolean success;
    private final String message;

    public PaymentResponse(String paymentId, boolean success, String message) {
        this.paymentId = paymentId;
        this.success = success;
        this.message = message;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
```

### Purpose

This class represents the result of payment processing.

Example success response:

```text
paymentId = PAY-101
success = true
message = Payment processed successfully
```

Example failed response:

```text
paymentId = PAY-102
success = false
message = Payment blocked due to fraud suspicion
```

---

## 9. Class 4 — `BasicPaymentProcessor`

Create:

```java
public class BasicPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResponse process(PaymentRequest request) {
        System.out.println("Processing payment: " + request.getPaymentId());

        return new PaymentResponse(
                request.getPaymentId(),
                true,
                "Payment processed successfully"
        );
    }
}
```

### Purpose

This is the **Concrete Component**.

It should only process the payment.

It should not know about:

```text
Validation
Fraud check
Audit logging
Metrics
Retry
```

That extra behavior belongs in decorators.

---

## 10. Class 5 — `PaymentProcessorDecorator`

Create:

```java
public abstract class PaymentProcessorDecorator implements PaymentProcessor {

    private final PaymentProcessor paymentProcessor;

    protected PaymentProcessorDecorator(PaymentProcessor paymentProcessor) {
        if (paymentProcessor == null) {
            throw new IllegalArgumentException("PaymentProcessor cannot be null.");
        }

        this.paymentProcessor = paymentProcessor;
    }

    protected PaymentProcessor getWrappedProcessor() {
        return paymentProcessor;
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        return paymentProcessor.process(request);
    }
}
```

### Purpose

This is the **Base Decorator**.

It wraps another `PaymentProcessor`.

This line stores the wrapped object:

```java
private final PaymentProcessor paymentProcessor;
```

The protected getter allows child decorators to use the wrapped processor:

```java
protected PaymentProcessor getWrappedProcessor() {
    return paymentProcessor;
}
```

You may also use:

```java
protected final PaymentProcessor wrappedProcessor;
```

Both approaches are acceptable.

### Why is this class abstract?

Because `PaymentProcessorDecorator` is not a real payment behavior by itself.

Real decorators are:

```text
ValidationPaymentDecorator
FraudCheckPaymentDecorator
AuditLogPaymentDecorator
MetricsPaymentDecorator
RetryPaymentDecorator
```

The base decorator only exists to hold common wrapping logic.

---

## 11. Class 6 — `ValidationPaymentDecorator`

Create:

```java
public class ValidationPaymentDecorator extends PaymentProcessorDecorator {

    public ValidationPaymentDecorator(PaymentProcessor paymentProcessor) {
        super(paymentProcessor);
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        validatePaymentRequest(request);
        System.out.println("VALIDATION: Payment request validated for " + request.getPaymentId());
        return super.process(request);
    }

    private void validatePaymentRequest(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null.");
        }

        if (request.getPaymentId() == null || request.getPaymentId().isBlank()) {
            throw new IllegalArgumentException("Payment ID is required.");
        }

        if (request.getCustomerId() == null || request.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("Customer ID is required.");
        }

        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (request.getCurrency() == null || request.getCurrency().isBlank()) {
            throw new IllegalArgumentException("Currency is required.");
        }

        if (request.getPaymentMode() == null || request.getPaymentMode().isBlank()) {
            throw new IllegalArgumentException("Payment mode is required.");
        }
    }
}
```

### Purpose

This decorator validates the payment request before allowing the chain to continue.

Validation rules:

```text
payment request cannot be null
paymentId cannot be null or blank
customerId cannot be null or blank
amount must be greater than 0
currency cannot be null or blank
paymentMode cannot be null or blank
```

If validation fails, this decorator should throw:

```java
IllegalArgumentException
```

If validation passes, it should delegate:

```java
return super.process(request);
```

---

## 12. Class 7 — `FraudCheckPaymentDecorator`

Create:

```java
public class FraudCheckPaymentDecorator extends PaymentProcessorDecorator {

    private final double maxAmount;

    public FraudCheckPaymentDecorator(PaymentProcessor paymentProcessor) {
        super(paymentProcessor);
        this.maxAmount = 100000.0;
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        if (request.getAmount() > maxAmount) {
            System.out.println("FRAUD CHECK: Suspicious payment blocked: " + request.getPaymentId());

            return new PaymentResponse(
                    request.getPaymentId(),
                    false,
                    "Payment blocked due to fraud suspicion"
            );
        }

        System.out.println("FRAUD CHECK: Payment passed fraud check for " + request.getPaymentId());
        return super.process(request);
    }
}
```

### Purpose

This decorator checks whether a payment is suspicious.

Fraud rule:

```text
If amount > 100000, block payment.
```

If fraud is detected, this decorator should stop the chain.

That means it should not call:

```java
super.process(request);
```

Instead, it should return:

```java
new PaymentResponse(..., false, "Payment blocked due to fraud suspicion")
```

### Important Design Point

Fraud detection is a **business rejection**, not necessarily a system exception.

So it is cleaner to return a failed `PaymentResponse` instead of throwing an exception.

---

## 13. Class 8 — `AuditLogPaymentDecorator`

Create:

```java
public class AuditLogPaymentDecorator extends PaymentProcessorDecorator {

    public AuditLogPaymentDecorator(PaymentProcessor paymentProcessor) {
        super(paymentProcessor);
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        System.out.println("AUDIT: Starting payment " + request.getPaymentId());

        PaymentResponse response = super.process(request);

        System.out.println("AUDIT: Finished payment "
                + request.getPaymentId()
                + " with status "
                + (response.isSuccess() ? "SUCCESS" : "FAILED"));

        return response;
    }
}
```

### Purpose

This decorator logs before and after payment processing.

It should:

```text
1. Log before calling wrapped processor
2. Call wrapped processor
3. Log after receiving response
4. Return the response
```

Expected behavior:

```text
AUDIT: Starting payment PAY-101
...
AUDIT: Finished payment PAY-101 with status SUCCESS
```

---

## 14. Class 9 — `PaymentMetrics`

Create:

```java
public class PaymentMetrics {

    private int totalAttempts;
    private int successCount;
    private int failureCount;

    public void incrementTotalAttempts() {
        totalAttempts++;
    }

    public void incrementSuccessCount() {
        successCount++;
    }

    public void incrementFailureCount() {
        failureCount++;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }
}
```

### Purpose

This class stores payment metrics.

For this module, track:

```text
totalAttempts
successCount
failureCount
```

---

## 15. Class 10 — `MetricsPaymentDecorator`

Create:

```java
public class MetricsPaymentDecorator extends PaymentProcessorDecorator {

    private final PaymentMetrics metrics;

    public MetricsPaymentDecorator(
            PaymentProcessor paymentProcessor,
            PaymentMetrics metrics
    ) {
        super(paymentProcessor);

        if (metrics == null) {
            throw new IllegalArgumentException("Metrics cannot be null.");
        }

        this.metrics = metrics;
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        metrics.incrementTotalAttempts();

        try {
            PaymentResponse response = super.process(request);

            if (response.isSuccess()) {
                metrics.incrementSuccessCount();
            } else {
                metrics.incrementFailureCount();
            }

            System.out.println("METRICS: Payment metrics updated for " + request.getPaymentId());
            return response;

        } catch (RuntimeException e) {
            metrics.incrementFailureCount();
            throw e;
        }
    }
}
```

### Purpose

This decorator tracks metrics.

It should count:

```text
Total attempts
Successful payments
Failed payments
```

### Why catch exceptions?

If validation throws an exception, or some wrapped processor throws an exception, the payment failed.

So a production-style metrics decorator can count failure before rethrowing.

If you do not catch exceptions, failure metrics may not increment for exception cases.

---

## 16. Class 11 — `RetryPaymentDecorator`

Create:

```java
public class RetryPaymentDecorator extends PaymentProcessorDecorator {

    private final int maxRetries;

    public RetryPaymentDecorator(PaymentProcessor paymentProcessor, int maxRetries) {
        super(paymentProcessor);

        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative.");
        }

        this.maxRetries = maxRetries;
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        int attempts = 0;
        int totalAttempts = maxRetries + 1;
        String failedMessage = null;

        while (attempts < totalAttempts) {
            attempts++;

            try {
                System.out.println("RETRY: Attempt " + attempts + " for payment " + request.getPaymentId());
                return super.process(request);

            } catch (Exception e) {
                failedMessage = e.getMessage();
                System.out.println("RETRY: Attempt " + attempts + " failed: " + failedMessage);
            }
        }

        return new PaymentResponse(
                request.getPaymentId(),
                false,
                "Payment failed after retries: " + failedMessage
        );
    }
}
```

### Purpose

This decorator retries payment processing if the wrapped processor throws an exception.

If:

```text
maxRetries = 2
```

Then total attempts are:

```text
1 original attempt + 2 retries = 3 attempts
```

Important:

```text
Retry should happen only for exceptions.
```

If the wrapped processor returns a failed `PaymentResponse`, that is not automatically an exception.

---

## 17. Class 12 — `UnstablePaymentProcessor`

Create:

```java
public class UnstablePaymentProcessor implements PaymentProcessor {

    private int attemptCount = 0;

    @Override
    public PaymentResponse process(PaymentRequest request) {
        attemptCount++;

        if (attemptCount < 3) {
            throw new RuntimeException("Temporary payment gateway failure");
        }

        System.out.println("Processing payment after retries: " + request.getPaymentId());

        return new PaymentResponse(
                request.getPaymentId(),
                true,
                "Payment processed successfully after retries"
        );
    }
}
```

### Purpose

This class is used only to test retry behavior.

It fails for the first two attempts and succeeds on the third attempt.

Expected behavior:

```text
Attempt 1 -> fail
Attempt 2 -> fail
Attempt 3 -> success
```

---

## 18. Main Class With All Examples

Create:

```java
public class Main {

    public static void main(String[] args) {
        runNormalPaymentExample();
        System.out.println();

        runFraudPaymentExample();
        System.out.println();

        runInvalidPaymentExample();
        System.out.println();

        runRetryPaymentExample();
    }

    private static void runNormalPaymentExample() {
        System.out.println("Module 5 — Payment Processor Decorator Ex-1: Normal Payment");

        PaymentMetrics metrics = new PaymentMetrics();

        PaymentProcessor processor = new BasicPaymentProcessor();
        processor = new ValidationPaymentDecorator(processor);
        processor = new FraudCheckPaymentDecorator(processor);
        processor = new AuditLogPaymentDecorator(processor);
        processor = new MetricsPaymentDecorator(processor, metrics);

        PaymentRequest request = new PaymentRequest(
                "PAY-101",
                "CUST-101",
                2500.0,
                "UPI",
                "INR"
        );

        PaymentResponse response = processor.process(request);

        printResponseAndMetrics(response, metrics);
    }

    private static void runFraudPaymentExample() {
        System.out.println("Module 5 — Payment Processor Decorator Ex-2: Fraud Payment");

        PaymentMetrics metrics = new PaymentMetrics();

        PaymentProcessor processor = new BasicPaymentProcessor();
        processor = new ValidationPaymentDecorator(processor);
        processor = new FraudCheckPaymentDecorator(processor);
        processor = new AuditLogPaymentDecorator(processor);
        processor = new MetricsPaymentDecorator(processor, metrics);

        PaymentRequest fraudRequest = new PaymentRequest(
                "PAY-102",
                "CUST-999",
                150000.0,
                "CARD",
                "INR"
        );

        try {
            PaymentResponse response = processor.process(fraudRequest);
            printResponseAndMetrics(response, metrics);
        } catch (Exception e) {
            System.out.println("Final Exception: " + e.getMessage());
            printMetrics(metrics);
        }
    }

    private static void runInvalidPaymentExample() {
        System.out.println("Module 5 — Payment Processor Decorator Ex-3: Invalid Payment");

        PaymentMetrics metrics = new PaymentMetrics();

        PaymentProcessor processor = new BasicPaymentProcessor();
        processor = new ValidationPaymentDecorator(processor);
        processor = new FraudCheckPaymentDecorator(processor);
        processor = new AuditLogPaymentDecorator(processor);
        processor = new MetricsPaymentDecorator(processor, metrics);

        PaymentRequest invalidRequest = new PaymentRequest(
                "PAY-103",
                "CUST-101",
                -500.0,
                "UPI",
                "INR"
        );

        try {
            PaymentResponse response = processor.process(invalidRequest);
            printResponseAndMetrics(response, metrics);
        } catch (Exception e) {
            System.out.println("Final Exception: " + e.getMessage());
            printMetrics(metrics);
        }
    }

    private static void runRetryPaymentExample() {
        System.out.println("Module 5 — Payment Processor Decorator Ex-4: Retry Payment");

        PaymentProcessor retryProcessor = new UnstablePaymentProcessor();
        retryProcessor = new RetryPaymentDecorator(retryProcessor, 2);

        PaymentRequest retryRequest = new PaymentRequest(
                "PAY-104",
                "CUST-404",
                999.0,
                "UPI",
                "INR"
        );

        PaymentResponse response = retryProcessor.process(retryRequest);

        System.out.println("Final Response: " + response.getMessage());
    }

    private static void printResponseAndMetrics(PaymentResponse response, PaymentMetrics metrics) {
        System.out.println("Final Response: " + response.getMessage());
        printMetrics(metrics);
    }

    private static void printMetrics(PaymentMetrics metrics) {
        System.out.println("Total Attempts: " + metrics.getTotalAttempts());
        System.out.println("Success Count: " + metrics.getSuccessCount());
        System.out.println("Failure Count: " + metrics.getFailureCount());
    }
}
```

---

## 19. Expected Example 1 — Normal Payment

Request:

```java
new PaymentRequest(
        "PAY-101",
        "CUST-101",
        2500.0,
        "UPI",
        "INR"
);
```

Expected output style:

```text
Module 5 — Payment Processor Decorator Ex-1: Normal Payment
AUDIT: Starting payment PAY-101
FRAUD CHECK: Payment passed fraud check for PAY-101
VALIDATION: Payment request validated for PAY-101
Processing payment: PAY-101
AUDIT: Finished payment PAY-101 with status SUCCESS
METRICS: Payment metrics updated for PAY-101
Final Response: Payment processed successfully
Total Attempts: 1
Success Count: 1
Failure Count: 0
```

The exact order can differ depending on decorator chain order.

---

## 20. Expected Example 2 — Fraud Payment

Request:

```java
new PaymentRequest(
        "PAY-102",
        "CUST-999",
        150000.0,
        "CARD",
        "INR"
);
```

Expected output style:

```text
Module 5 — Payment Processor Decorator Ex-2: Fraud Payment
AUDIT: Starting payment PAY-102
FRAUD CHECK: Suspicious payment blocked: PAY-102
AUDIT: Finished payment PAY-102 with status FAILED
METRICS: Payment metrics updated for PAY-102
Final Response: Payment blocked due to fraud suspicion
Total Attempts: 1
Success Count: 0
Failure Count: 1
```

Important:

```text
BasicPaymentProcessor should not run.
```

So this should not appear:

```text
Processing payment: PAY-102
```

---

## 21. Expected Example 3 — Invalid Payment

Request:

```java
new PaymentRequest(
        "PAY-103",
        "CUST-101",
        -500.0,
        "UPI",
        "INR"
);
```

Expected output style:

```text
Module 5 — Payment Processor Decorator Ex-3: Invalid Payment
AUDIT: Starting payment PAY-103
Final Exception: Amount must be greater than zero.
Total Attempts: 1
Success Count: 0
Failure Count: 1
```

Since validation throws an exception, decorators inside validation should not run.

Depending on your chain and metrics implementation, audit after-log may or may not run. A more advanced version can use `try-finally` in audit, but it is not required for this module.

---

## 22. Expected Example 4 — Retry Payment

Request:

```java
new PaymentRequest(
        "PAY-104",
        "CUST-404",
        999.0,
        "UPI",
        "INR"
);
```

Expected output:

```text
Module 5 — Payment Processor Decorator Ex-4: Retry Payment
RETRY: Attempt 1 for payment PAY-104
RETRY: Attempt 1 failed: Temporary payment gateway failure
RETRY: Attempt 2 for payment PAY-104
RETRY: Attempt 2 failed: Temporary payment gateway failure
RETRY: Attempt 3 for payment PAY-104
Processing payment after retries: PAY-104
Final Response: Payment processed successfully after retries
```

---

## 23. Important: Decorator Order Matters

This chain:

```java
processor = new ValidationPaymentDecorator(processor);
processor = new FraudCheckPaymentDecorator(processor);
processor = new AuditLogPaymentDecorator(processor);
processor = new MetricsPaymentDecorator(processor, metrics);
```

creates:

```text
MetricsPaymentDecorator
    wraps AuditLogPaymentDecorator
        wraps FraudCheckPaymentDecorator
            wraps ValidationPaymentDecorator
                wraps BasicPaymentProcessor
```

The outermost decorator runs first.

So the call starts from:

```text
MetricsPaymentDecorator
```

Then moves inward.

If you change the order, the output and behavior can change.

---

## 24. Why Fraud Check Should Return a Failed Response

Fraud detection is usually a business decision.

For this module:

```text
Fraud payment should be rejected cleanly.
```

So this is preferred:

```java
return new PaymentResponse(
        request.getPaymentId(),
        false,
        "Payment blocked due to fraud suspicion"
);
```

Instead of:

```java
throw new IllegalArgumentException("Fraud check failed");
```

Why?

Because throwing makes it harder for audit and metrics decorators to handle the response cleanly.

---

## 25. Why Validation Throws Exception

Validation failure means the request itself is invalid.

Example:

```text
amount = -500
```

This is not a valid payment.

So throwing is acceptable:

```java
throw new IllegalArgumentException("Amount must be greater than zero.");
```

Simple distinction:

| Case | Recommended Handling |
|---|---|
| Invalid request | Throw exception |
| Fraud/suspicious payment | Return failed `PaymentResponse` |
| Temporary gateway failure | Throw exception and retry |

---

## 26. Common Mistakes to Avoid

### Mistake 1: Putting all logic inside `BasicPaymentProcessor`

Bad:

```java
public class BasicPaymentProcessor {
    public PaymentResponse process(PaymentRequest request) {
        validate();
        fraudCheck();
        audit();
        metrics();
        retry();
    }
}
```

Correct:

```text
Keep BasicPaymentProcessor simple.
Use decorators for extra behavior.
```

---

### Mistake 2: Forgetting to call `super.process(request)`

Bad:

```java
public PaymentResponse process(PaymentRequest request) {
    System.out.println("AUDIT: Starting");
    return null;
}
```

Correct:

```java
PaymentResponse response = super.process(request);
```

---

### Mistake 3: Fraud decorator throwing instead of returning failed response

Less ideal:

```java
throw new IllegalArgumentException("Fraud check failed");
```

Preferred:

```java
return new PaymentResponse(
        request.getPaymentId(),
        false,
        "Payment blocked due to fraud suspicion"
);
```

---

### Mistake 4: Confusing max retries and max attempts

If:

```text
maxRetries = 2
```

Then:

```text
total attempts = 3
```

Because:

```text
1 original attempt + 2 retries
```

---

### Mistake 5: Missing success logs

For readability, add logs like:

```text
VALIDATION: Payment request validated for PAY-101
FRAUD CHECK: Payment passed fraud check for PAY-101
```

This makes decorator execution visible.

---

### Mistake 6: Not understanding chain order

The last decorator assigned is the outermost one.

Example:

```java
processor = new MetricsPaymentDecorator(processor, metrics);
```

After this line, calls enter `MetricsPaymentDecorator` first.

---

## 27. What This Module Tests

This module checks whether you understand:

```text
Decorator Pattern in backend-style processing
Component interface
Concrete component
Base decorator
Concrete decorators
Request and response objects
Validation
Fraud checks
Audit logs
Metrics
Retry
Exception handling
Short-circuiting the chain
Decorator order
Single Responsibility Principle
Open/Closed Principle
```

---

## 28. Scoring Rubric

| Area | Marks |
|---|---:|
| `PaymentProcessor` interface | 1 |
| `PaymentRequest` and `PaymentResponse` | 1.5 |
| `BasicPaymentProcessor` | 1 |
| `PaymentProcessorDecorator` | 1.5 |
| `ValidationPaymentDecorator` | 1 |
| `FraudCheckPaymentDecorator` | 1 |
| `AuditLogPaymentDecorator` | 1 |
| `MetricsPaymentDecorator` and `PaymentMetrics` | 1 |
| `RetryPaymentDecorator` | 1.5 |
| Main tests and readability | 0.5 |

Total: **10 marks**

---

## 29. Key Learning

The key learning of Module 5 is:

```text
Decorator Pattern can build flexible backend processing pipelines.
```

Each decorator wraps the processor and adds one behavior:

```text
ValidationPaymentDecorator -> validates request
FraudCheckPaymentDecorator -> blocks suspicious payment
AuditLogPaymentDecorator -> logs before and after
MetricsPaymentDecorator -> counts attempts/success/failure
RetryPaymentDecorator -> retries exception-based failures
```

This keeps the system modular.

If tomorrow you want to add:

```text
RateLimitPaymentDecorator
CurrencyConversionDecorator
NotificationAfterPaymentDecorator
SecurityContextDecorator
TracingPaymentDecorator
```

you can add new decorators without modifying `BasicPaymentProcessor`.

That is the Open/Closed Principle in action.

---

## 30. Final Mental Model

Remember this chain:

```java
PaymentProcessor processor = new BasicPaymentProcessor();

processor = new ValidationPaymentDecorator(processor);
processor = new FraudCheckPaymentDecorator(processor);
processor = new AuditLogPaymentDecorator(processor);
processor = new MetricsPaymentDecorator(processor, metrics);
```

Means:

```text
MetricsPaymentDecorator(
    AuditLogPaymentDecorator(
        FraudCheckPaymentDecorator(
            ValidationPaymentDecorator(
                BasicPaymentProcessor
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

> Decorator Pattern lets you add backend features like validation, fraud check, audit, metrics, and retry without modifying the original processor class.
