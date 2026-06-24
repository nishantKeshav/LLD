# Abstract Factory Design Pattern — Module 1

## Payment Provider Toolkit Abstract Factory

> Note: The correct standard design-pattern name is **Abstract Factory Design Pattern**.  
> The phrase “Absolute Factory” is commonly used by mistake, but the actual pattern is **Abstract Factory**.

---

# 1. Module Metadata

## Module Name

**Payment Provider Toolkit Abstract Factory**

## Difficulty Level

**Module 1 — Backend Foundation**

This is the first module in the Abstract Factory series, but it is still designed for a backend engineer with around **5–6 years of experience** working in a product-based company.

So this module is not just about writing classes that compile.

It focuses on:

```text
clean object family creation
interface-based design
request and result modeling
service orchestration
provider-specific behavior separation
validation
testable LLD structure
```

---

# 2. Pattern Focus

This module focuses on the **Abstract Factory Design Pattern**.

The purpose of Abstract Factory is to create a **family of related objects** without exposing concrete classes to the calling code.

In this module, the object family is a **payment provider toolkit**.

For each payment provider, you need multiple related components.

For example:

```text
Razorpay family:
- RazorpayPaymentClient
- RazorpayRefundClient
- RazorpayStatusClient
- RazorpayWebhookVerifier

Stripe family:
- StripePaymentClient
- StripeRefundClient
- StripeStatusClient
- StripeWebhookVerifier
```

The important point is:

```text
When the provider is RAZORPAY, all created components should be Razorpay-specific.

When the provider is STRIPE, all created components should be Stripe-specific.
```

This is the heart of Abstract Factory.

---

# 3. Why This Is Abstract Factory and Not Simple Factory

## Simple Factory / Factory Pattern

Factory Pattern usually creates **one object**.

Example:

```java
PaymentClient paymentClient = paymentClientFactory.getClient(PaymentProvider.RAZORPAY);
```

This gives you only one product:

```text
PaymentClient
```

Possible implementations:

```text
RazorpayPaymentClient
StripePaymentClient
```

So Factory Pattern answers:

```text
Which implementation of this one interface should I create?
```

---

## Abstract Factory Pattern

Abstract Factory creates a **family of related objects**.

Example:

```java
PaymentProviderFactory factory =
        PaymentProviderFactorySelector.getFactory(PaymentProvider.RAZORPAY);

PaymentClient paymentClient = factory.createPaymentClient();
RefundClient refundClient = factory.createRefundClient();
PaymentStatusClient statusClient = factory.createPaymentStatusClient();
WebhookVerifier webhookVerifier = factory.createWebhookVerifier();
```

Here, one selected factory creates multiple related products:

```text
RazorpayPaymentClient
RazorpayRefundClient
RazorpayStatusClient
RazorpayWebhookVerifier
```

So Abstract Factory answers:

```text
Which complete family of related objects should I create?
```

---

# 4. Business Context

You are building a backend payment integration system.

Your application currently supports two providers:

```text
RAZORPAY
STRIPE
```

Each provider supports multiple operations:

```text
payment processing
refund processing
payment status lookup
webhook verification
```

In a real backend system, payment provider integration is not just one class.

A provider integration usually contains a set of related components:

```text
Payment API client
Refund API client
Status lookup client
Webhook signature verifier
Credential validator
Payout client
Subscription client
Dispute client
```

For Module 1, we keep the family limited to four components:

```text
PaymentClient
RefundClient
PaymentStatusClient
WebhookVerifier
```

This is enough to understand the core Abstract Factory design.

---

# 5. Main Goal of the Module

Build a backend payment provider toolkit using the **Abstract Factory Design Pattern**.

The system should:

```text
1. Support RAZORPAY and STRIPE provider families.
2. Create a complete provider-specific toolkit.
3. Avoid direct concrete class creation in Main.
4. Keep payment/refund/status/webhook behavior separated.
5. Return structured result objects instead of only printing.
6. Prove that Razorpay and Stripe families are not mixed.
```

---

# 6. High-Level Design

The intended flow is:

```text
Main
  -> PaymentGatewayService
      -> PaymentProviderFactorySelector
          -> PaymentProviderFactory
              -> PaymentClient
              -> RefundClient
              -> PaymentStatusClient
              -> WebhookVerifier
```

For Razorpay:

```text
Main
  -> PaymentGatewayService
      -> PaymentProviderFactorySelector.getFactory(RAZORPAY)
          -> RazorpayPaymentProviderFactory
              -> RazorpayPaymentClient
              -> RazorpayRefundClient
              -> RazorpayStatusClient
              -> RazorpayWebhookVerifier
```

For Stripe:

```text
Main
  -> PaymentGatewayService
      -> PaymentProviderFactorySelector.getFactory(STRIPE)
          -> StripePaymentProviderFactory
              -> StripePaymentClient
              -> StripeRefundClient
              -> StripeStatusClient
              -> StripeWebhookVerifier
```

---

# 7. Required Classes

Create the following classes/interfaces/enums:

```text
PaymentProvider
PaymentStatus

PaymentRequest
RefundRequest
PaymentStatusRequest
WebhookPayload

PaymentResult
RefundResult
PaymentStatusResult
WebhookVerificationResult

PaymentClient
RefundClient
PaymentStatusClient
WebhookVerifier

RazorpayPaymentClient
RazorpayRefundClient
RazorpayStatusClient
RazorpayWebhookVerifier

StripePaymentClient
StripeRefundClient
StripeStatusClient
StripeWebhookVerifier

PaymentProviderFactory
RazorpayPaymentProviderFactory
StripePaymentProviderFactory

PaymentProviderFactorySelector
PaymentGatewayService
Main
```

---

# 8. Class-by-Class Responsibility Guide

This section explains why each class exists and what it should do.

---

## 8.1 `PaymentProvider`

## Type

Enum.

## Responsibility

Represents the payment provider family.

```java
public enum PaymentProvider {
    RAZORPAY,
    STRIPE
}
```

## Why it exists

It avoids raw string usage like:

```java
"razorpay"
"stripe"
"Stripe"
"STRIPE_PROVIDER"
```

Enums give compile-time safety.

## What it should not do

It should not contain payment/refund/status/webhook logic.

Do not do this:

```java
public enum PaymentProvider {
    RAZORPAY {
        public void pay() {}
    }
}
```

For this module, the enum should stay simple.

---

## 8.2 `PaymentStatus`

## Type

Enum.

## Responsibility

Represents payment status.

```java
public enum PaymentStatus {
    SUCCESS,
    FAILED,
    PENDING
}
```

## Why it exists

A status client should return a structured status instead of arbitrary strings.

Good:

```java
PaymentStatus.SUCCESS
```

Bad:

```java
"success"
"Success"
"SUCCESSFUL"
"done"
```

## What it should not do

It should not know about Razorpay or Stripe.

---

## 8.3 `PaymentRequest`

## Type

Request model.

## Responsibility

Represents the input needed to process a payment.

## Required fields

```text
paymentId
customerId
amount
currency
description
idempotencyKey
```

## Why it exists

Payment processing needs structured input.

Bad:

```java
processPayment("PAY-101", "CUST-101", 1500.0, "INR", "desc", "key");
```

Good:

```java
processPayment(paymentRequest);
```

This keeps method signatures clean and extensible.

## Expected validation

```text
paymentId cannot be null/blank
customerId cannot be null/blank
amount must be greater than 0
currency cannot be null/blank
description cannot be null/blank
idempotencyKey cannot be null/blank
```

## Suggested code

```java
public class PaymentRequest {

    private final String paymentId;
    private final String customerId;
    private final double amount;
    private final String currency;
    private final String description;
    private final String idempotencyKey;

    public PaymentRequest(
            String paymentId,
            String customerId,
            double amount,
            String currency,
            String description,
            String idempotencyKey
    ) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Payment ID cannot be null or blank.");
        }

        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank.");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank.");
        }

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("Idempotency key cannot be null or blank.");
        }

        this.paymentId = paymentId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.idempotencyKey = idempotencyKey;
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

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
```

---

## 8.4 `RefundRequest`

## Type

Request model.

## Responsibility

Represents the input needed to process a refund.

## Required fields

```text
refundId
paymentId
amount
reason
```

## Why it exists

Refunds are separate from payments.

Do not reuse `PaymentRequest` for refund operations.

A refund has a refund ID and reason. A payment has customer ID, currency, description, and idempotency key.

## Expected validation

```text
refundId cannot be null/blank
paymentId cannot be null/blank
amount must be greater than 0
reason cannot be null/blank
```

## Suggested code

```java
public class RefundRequest {

    private final String refundId;
    private final String paymentId;
    private final double amount;
    private final String reason;

    public RefundRequest(
            String refundId,
            String paymentId,
            double amount,
            String reason
    ) {
        if (refundId == null || refundId.isBlank()) {
            throw new IllegalArgumentException("Refund ID cannot be null or blank.");
        }

        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Payment ID cannot be null or blank.");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be null or blank.");
        }

        this.refundId = refundId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.reason = reason;
    }

    public String getRefundId() {
        return refundId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }
}
```

---

## 8.5 `PaymentStatusRequest`

## Type

Request model.

## Responsibility

Represents input needed to check payment status.

## Required fields

```text
paymentId
```

## Why it exists

Even though it has only one field today, it gives you a clean extension point.

Later, this can become:

```text
paymentId
tenantId
providerReferenceId
lookupSource
retryAttempt
```

## Suggested code

```java
public class PaymentStatusRequest {

    private final String paymentId;

    public PaymentStatusRequest(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Payment ID cannot be null or blank.");
        }

        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
```

---

## 8.6 `WebhookPayload`

## Type

Request model.

## Responsibility

Represents webhook data received from a payment provider.

## Required fields

```text
provider
payload
signature
eventType
```

## Why it exists

Payment providers send webhooks to your backend.

Example:

```text
Razorpay/Stripe -> Your Backend
```

A webhook tells your backend that something happened, such as:

```text
payment.captured
refund.processed
payment.failed
subscription.created
```

The backend should not blindly trust webhooks. It should verify that the webhook really came from the provider.

That is why this module has:

```text
WebhookPayload
WebhookVerifier
WebhookVerificationResult
```

## Important concept

`WebhookPayload` is the input to `WebhookVerifier`.

Just like:

```text
PaymentRequest       -> PaymentClient
RefundRequest        -> RefundClient
PaymentStatusRequest -> PaymentStatusClient
WebhookPayload       -> WebhookVerifier
```

## Suggested code

```java
public class WebhookPayload {

    private final PaymentProvider provider;
    private final String payload;
    private final String signature;
    private final String eventType;

    public WebhookPayload(
            PaymentProvider provider,
            String payload,
            String signature,
            String eventType
    ) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null.");
        }

        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("Payload cannot be null or blank.");
        }

        if (signature == null || signature.isBlank()) {
            throw new IllegalArgumentException("Signature cannot be null or blank.");
        }

        if (eventType == null || eventType.isBlank()) {
            throw new IllegalArgumentException("Event type cannot be null or blank.");
        }

        this.provider = provider;
        this.payload = payload;
        this.signature = signature;
        this.eventType = eventType;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public String getPayload() {
        return payload;
    }

    public String getSignature() {
        return signature;
    }

    public String getEventType() {
        return eventType;
    }
}
```

---

# 9. Result Classes

The module should return structured results.

Avoid using only `System.out.println()` inside business classes.

---

## 9.1 `PaymentResult`

## Responsibility

Represents the result of payment processing.

## Required fields

```text
paymentId
provider
success
message
providerReferenceId
```

## Suggested code

```java
public class PaymentResult {

    private final String paymentId;
    private final PaymentProvider provider;
    private final boolean success;
    private final String message;
    private final String providerReferenceId;

    public PaymentResult(
            String paymentId,
            PaymentProvider provider,
            boolean success,
            String message,
            String providerReferenceId
    ) {
        this.paymentId = paymentId;
        this.provider = provider;
        this.success = success;
        this.message = message;
        this.providerReferenceId = providerReferenceId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getProviderReferenceId() {
        return providerReferenceId;
    }
}
```

---

## 9.2 `RefundResult`

## Responsibility

Represents the result of refund processing.

## Required fields

```text
refundId
paymentId
provider
success
message
providerRefundReferenceId
```

---

## 9.3 `PaymentStatusResult`

## Responsibility

Represents the result of payment status lookup.

## Required fields

```text
paymentId
provider
status
message
```

Important naming note:

Use:

```java
PaymentStatusResult
```

Avoid:

```java
PaymentResultStatus
```

`PaymentStatusResult` is clearer because it means:

```text
Result of checking payment status
```

---

## 9.4 `WebhookVerificationResult`

## Responsibility

Represents the result of webhook verification.

## Required fields

```text
provider
valid
message
eventType
```

---

# 10. Product Interfaces

These are the product interfaces created by the abstract factory.

---

## 10.1 `PaymentClient`

```java
public interface PaymentClient {
    PaymentResult processPayment(PaymentRequest request);
}
```

## Responsibility

Only payment processing.

## Should not do

```text
Should not process refunds.
Should not check payment status.
Should not verify webhooks.
Should not select providers.
```

---

## 10.2 `RefundClient`

```java
public interface RefundClient {
    RefundResult processRefund(RefundRequest request);
}
```

## Responsibility

Only refund processing.

---

## 10.3 `PaymentStatusClient`

```java
public interface PaymentStatusClient {
    PaymentStatusResult checkStatus(PaymentStatusRequest request);
}
```

## Responsibility

Only payment status lookup.

---

## 10.4 `WebhookVerifier`

```java
public interface WebhookVerifier {
    WebhookVerificationResult verify(WebhookPayload payload);
}
```

## Responsibility

Only webhook verification.

---

# 11. Concrete Product Families

---

## 11.1 Razorpay Product Family

The Razorpay family should contain:

```text
RazorpayPaymentClient
RazorpayRefundClient
RazorpayStatusClient
RazorpayWebhookVerifier
```

Each class should return/use:

```java
PaymentProvider.RAZORPAY
```

## Example: `RazorpayPaymentClient`

```java
public class RazorpayPaymentClient implements PaymentClient {

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null.");
        }

        return new PaymentResult(
                request.getPaymentId(),
                PaymentProvider.RAZORPAY,
                true,
                "Payment successful through RAZORPAY",
                "RZP-PAY-" + request.getPaymentId() + "-" + request.getIdempotencyKey()
        );
    }
}
```

## Example: `RazorpayWebhookVerifier`

```java
public class RazorpayWebhookVerifier implements WebhookVerifier {

    @Override
    public WebhookVerificationResult verify(WebhookPayload payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Webhook payload cannot be null.");
        }

        if (payload.getProvider() != PaymentProvider.RAZORPAY) {
            return new WebhookVerificationResult(
                    PaymentProvider.RAZORPAY,
                    false,
                    "Invalid webhook provider for Razorpay verifier",
                    payload.getEventType()
            );
        }

        return new WebhookVerificationResult(
                PaymentProvider.RAZORPAY,
                true,
                "Webhook verified successfully for RAZORPAY",
                payload.getEventType()
        );
    }
}
```

---

## 11.2 Stripe Product Family

The Stripe family should contain:

```text
StripePaymentClient
StripeRefundClient
StripeStatusClient
StripeWebhookVerifier
```

Each class should return/use:

```java
PaymentProvider.STRIPE
```

## Example: `StripePaymentClient`

```java
public class StripePaymentClient implements PaymentClient {

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null.");
        }

        return new PaymentResult(
                request.getPaymentId(),
                PaymentProvider.STRIPE,
                true,
                "Payment successful through STRIPE",
                "STR-PAY-" + request.getPaymentId() + "-" + request.getIdempotencyKey()
        );
    }
}
```

---

# 12. Abstract Factory Interface

## `PaymentProviderFactory`

This is the main Abstract Factory interface.

```java
public interface PaymentProviderFactory {

    PaymentClient createPaymentClient();

    RefundClient createRefundClient();

    PaymentStatusClient createPaymentStatusClient();

    WebhookVerifier createWebhookVerifier();
}
```

## Why this exists

This interface guarantees that every provider factory can create the complete provider toolkit.

The key idea:

```text
One factory creates multiple related products.
```

Each provider-specific factory must implement this interface.

---

# 13. Concrete Factories

---

## 13.1 `RazorpayPaymentProviderFactory`

```java
public class RazorpayPaymentProviderFactory implements PaymentProviderFactory {

    @Override
    public PaymentClient createPaymentClient() {
        return new RazorpayPaymentClient();
    }

    @Override
    public RefundClient createRefundClient() {
        return new RazorpayRefundClient();
    }

    @Override
    public PaymentStatusClient createPaymentStatusClient() {
        return new RazorpayStatusClient();
    }

    @Override
    public WebhookVerifier createWebhookVerifier() {
        return new RazorpayWebhookVerifier();
    }
}
```

## Responsibility

Creates the Razorpay product family.

## Should not do

```text
Should not create Stripe objects.
Should not execute payment.
Should not validate payment requests.
Should not select provider.
```

---

## 13.2 `StripePaymentProviderFactory`

```java
public class StripePaymentProviderFactory implements PaymentProviderFactory {

    @Override
    public PaymentClient createPaymentClient() {
        return new StripePaymentClient();
    }

    @Override
    public RefundClient createRefundClient() {
        return new StripeRefundClient();
    }

    @Override
    public PaymentStatusClient createPaymentStatusClient() {
        return new StripeStatusClient();
    }

    @Override
    public WebhookVerifier createWebhookVerifier() {
        return new StripeWebhookVerifier();
    }
}
```

## Responsibility

Creates the Stripe product family.

---

# 14. Factory Selector

## `PaymentProviderFactorySelector`

## Responsibility

Selects the correct abstract factory based on provider.

## Suggested code

```java
public class PaymentProviderFactorySelector {

    private PaymentProviderFactorySelector() {
        // Utility class
    }

    public static PaymentProviderFactory getFactory(PaymentProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Payment provider cannot be null.");
        }

        return switch (provider) {
            case RAZORPAY -> new RazorpayPaymentProviderFactory();
            case STRIPE -> new StripePaymentProviderFactory();
        };
    }
}
```

## Why this exists

Main should not manually create provider factories.

Bad:

```java
PaymentProviderFactory factory = new RazorpayPaymentProviderFactory();
```

Better:

```java
PaymentProviderFactory factory =
        PaymentProviderFactorySelector.getFactory(PaymentProvider.RAZORPAY);
```

For Module 1, a switch-based selector is acceptable.

In later modules, this will evolve into a registry-based abstract factory.

---

# 15. Service Layer

## `PaymentGatewayService`

## Responsibility

Coordinates payment provider operations.

It should:

```text
1. Receive provider and request.
2. Get the correct provider factory.
3. Ask factory for the correct product.
4. Execute the operation.
5. Return structured result.
```

## Suggested code

```java
public class PaymentGatewayService {

    public PaymentResult processPayment(
            PaymentProvider provider,
            PaymentRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null.");
        }

        PaymentProviderFactory factory =
                PaymentProviderFactorySelector.getFactory(provider);

        return factory
                .createPaymentClient()
                .processPayment(request);
    }

    public RefundResult processRefund(
            PaymentProvider provider,
            RefundRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Refund request cannot be null.");
        }

        PaymentProviderFactory factory =
                PaymentProviderFactorySelector.getFactory(provider);

        return factory
                .createRefundClient()
                .processRefund(request);
    }

    public PaymentStatusResult checkStatus(
            PaymentProvider provider,
            PaymentStatusRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Payment status request cannot be null.");
        }

        PaymentProviderFactory factory =
                PaymentProviderFactorySelector.getFactory(provider);

        return factory
                .createPaymentStatusClient()
                .checkStatus(request);
    }

    public WebhookVerificationResult verifyWebhook(
            PaymentProvider provider,
            WebhookPayload payload
    ) {
        if (payload == null) {
            throw new IllegalArgumentException("Webhook payload cannot be null.");
        }

        PaymentProviderFactory factory =
                PaymentProviderFactorySelector.getFactory(provider);

        return factory
                .createWebhookVerifier()
                .verify(payload);
    }
}
```

## What this class should not do

```text
Should not directly instantiate RazorpayPaymentClient.
Should not directly instantiate StripeRefundClient.
Should not contain provider-specific if-else logic.
Should not verify webhook signatures itself.
Should not print output.
```

---

# 16. Main Test Cases

Your `Main` should test:

```text
1. Razorpay payment processing
2. Razorpay refund processing
3. Razorpay payment status check
4. Razorpay webhook verification

5. Stripe payment processing
6. Stripe refund processing
7. Stripe payment status check
8. Stripe webhook verification
```

---

# 17. Suggested `Main.java`

```java
public class Main {

    public static void main(String[] args) {

        PaymentGatewayService paymentGatewayService =
                new PaymentGatewayService();

        System.out.println("Abstract Factory Module 1 — Payment Provider Toolkit");

        PaymentRequest razorpayPaymentRequest = new PaymentRequest(
                "PAY-101",
                "CUST-101",
                1500.00,
                "INR",
                "Mobile recharge payment",
                "IDEMP-PAY-101"
        );

        RefundRequest razorpayRefundRequest = new RefundRequest(
                "REF-101",
                "PAY-101",
                500.00,
                "Customer requested partial refund"
        );

        PaymentStatusRequest razorpayStatusRequest =
                new PaymentStatusRequest("PAY-101");

        WebhookPayload razorpayWebhookPayload = new WebhookPayload(
                PaymentProvider.RAZORPAY,
                "{ \"paymentId\": \"PAY-101\", \"status\": \"captured\" }",
                "razorpay-signature-123",
                "payment.captured"
        );

        PaymentRequest stripePaymentRequest = new PaymentRequest(
                "PAY-102",
                "CUST-102",
                2500.00,
                "USD",
                "Subscription payment",
                "IDEMP-PAY-102"
        );

        RefundRequest stripeRefundRequest = new RefundRequest(
                "REF-102",
                "PAY-102",
                1000.00,
                "Subscription cancelled"
        );

        PaymentStatusRequest stripeStatusRequest =
                new PaymentStatusRequest("PAY-102");

        WebhookPayload stripeWebhookPayload = new WebhookPayload(
                PaymentProvider.STRIPE,
                "{ \"paymentId\": \"PAY-102\", \"status\": \"succeeded\" }",
                "stripe-signature-456",
                "payment_intent.succeeded"
        );

        printPaymentResult(
                paymentGatewayService.processPayment(
                        PaymentProvider.RAZORPAY,
                        razorpayPaymentRequest
                )
        );

        printRefundResult(
                paymentGatewayService.processRefund(
                        PaymentProvider.RAZORPAY,
                        razorpayRefundRequest
                )
        );

        printPaymentStatusResult(
                paymentGatewayService.checkStatus(
                        PaymentProvider.RAZORPAY,
                        razorpayStatusRequest
                )
        );

        printWebhookVerificationResult(
                paymentGatewayService.verifyWebhook(
                        PaymentProvider.RAZORPAY,
                        razorpayWebhookPayload
                )
        );

        printPaymentResult(
                paymentGatewayService.processPayment(
                        PaymentProvider.STRIPE,
                        stripePaymentRequest
                )
        );

        printRefundResult(
                paymentGatewayService.processRefund(
                        PaymentProvider.STRIPE,
                        stripeRefundRequest
                )
        );

        printPaymentStatusResult(
                paymentGatewayService.checkStatus(
                        PaymentProvider.STRIPE,
                        stripeStatusRequest
                )
        );

        printWebhookVerificationResult(
                paymentGatewayService.verifyWebhook(
                        PaymentProvider.STRIPE,
                        stripeWebhookPayload
                )
        );
    }

    private static void printPaymentResult(PaymentResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Provider: " + result.getProvider());
        System.out.println("Operation: PAYMENT");
        System.out.println("Payment ID: " + result.getPaymentId());
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Provider Reference ID: " + result.getProviderReferenceId());
        System.out.println("==================================================");
    }

    private static void printRefundResult(RefundResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Provider: " + result.getProvider());
        System.out.println("Operation: REFUND");
        System.out.println("Refund ID: " + result.getRefundId());
        System.out.println("Payment ID: " + result.getPaymentId());
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Provider Refund Reference ID: " + result.getProviderRefundReferenceId());
        System.out.println("==================================================");
    }

    private static void printPaymentStatusResult(PaymentStatusResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Provider: " + result.getProvider());
        System.out.println("Operation: STATUS");
        System.out.println("Payment ID: " + result.getPaymentId());
        System.out.println("Status: " + result.getStatus());
        System.out.println("Message: " + result.getMessage());
        System.out.println("==================================================");
    }

    private static void printWebhookVerificationResult(WebhookVerificationResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Provider: " + result.getProvider());
        System.out.println("Operation: WEBHOOK_VERIFICATION");
        System.out.println("Event Type: " + result.getEventType());
        System.out.println("Valid: " + result.isValid());
        System.out.println("Message: " + result.getMessage());
        System.out.println("==================================================");
    }
}
```

---

# 18. Expected Output Style

Your exact UUIDs/reference IDs may differ.

```text
Abstract Factory Module 1 — Payment Provider Toolkit

==================================================
Provider: RAZORPAY
Operation: PAYMENT
Payment ID: PAY-101
Success: true
Message: Payment successful through RAZORPAY
Provider Reference ID: RZP-PAY-PAY-101-IDEMP-PAY-101
==================================================

==================================================
Provider: RAZORPAY
Operation: REFUND
Refund ID: REF-101
Payment ID: PAY-101
Success: true
Message: Refund successful through RAZORPAY
Provider Refund Reference ID: RZP-REF-REF-101-<uuid>
==================================================

==================================================
Provider: RAZORPAY
Operation: STATUS
Payment ID: PAY-101
Status: PENDING
Message: RAZORPAY payment status fetched successfully
==================================================

==================================================
Provider: RAZORPAY
Operation: WEBHOOK_VERIFICATION
Event Type: payment.captured
Valid: true
Message: Webhook verified successfully for RAZORPAY
==================================================

==================================================
Provider: STRIPE
Operation: PAYMENT
Payment ID: PAY-102
Success: true
Message: Payment successful through STRIPE
Provider Reference ID: STR-PAY-PAY-102-IDEMP-PAY-102
==================================================
```

---

# 19. Important Design Rules

## Rule 1: Do not create concrete products directly in Main

Wrong:

```java
PaymentClient client = new RazorpayPaymentClient();
```

Correct:

```java
PaymentGatewayService service = new PaymentGatewayService();
service.processPayment(PaymentProvider.RAZORPAY, request);
```

---

## Rule 2: Do not mix provider families

Wrong:

```java
PaymentClient paymentClient = new RazorpayPaymentClient();
RefundClient refundClient = new StripeRefundClient();
```

Correct:

```java
PaymentProviderFactory factory =
        new RazorpayPaymentProviderFactory();

PaymentClient paymentClient = factory.createPaymentClient();
RefundClient refundClient = factory.createRefundClient();
```

Both are Razorpay products.

---

## Rule 3: Do not make one giant interface

Wrong:

```java
public interface PaymentProviderClient {
    PaymentResult pay();
    RefundResult refund();
    PaymentStatusResult status();
    WebhookVerificationResult verifyWebhook();
}
```

Correct:

```text
PaymentClient
RefundClient
PaymentStatusClient
WebhookVerifier
```

Separate product interfaces are important.

---

## Rule 4: Do not put provider-specific logic inside service

Wrong:

```java
if (provider == PaymentProvider.RAZORPAY) {
    System.out.println("Razorpay payment");
}
```

Correct:

```java
PaymentClient paymentClient = factory.createPaymentClient();
return paymentClient.processPayment(request);
```

Provider-specific logic belongs inside concrete product classes.

---

## Rule 5: Prefer structured results over printing from clients

Good backend-style client:

```java
return new PaymentResult(...);
```

Avoid doing this inside clients:

```java
System.out.println("Payment processed");
```

Printing belongs in `Main` for this exercise.

In real production code, use logging where appropriate.

---

# 20. Common Mistakes

```text
1. Creating concrete products directly in Main.
2. Creating only one object from the abstract factory.
3. Mixing Razorpay payment client with Stripe refund client.
4. Making one giant interface instead of multiple product interfaces.
5. Putting provider-specific if-else logic inside PaymentGatewayService.
6. Returning void everywhere instead of structured result objects.
7. Not validating request objects.
8. Not testing both provider families.
9. Not creating a factory selector.
10. Calling it Abstract Factory but only creating one product.
11. Using confusing class names like PaymentResultStatus instead of PaymentStatusResult.
12. Printing from concrete clients instead of returning structured results.
```

---

# 21. Scoring Rubric

| Area | Marks |
|---|---:|
| `PaymentProvider` and `PaymentStatus` enums | 1 |
| Request classes | 1 |
| Result classes | 1 |
| Product interfaces | 1.5 |
| Razorpay product family | 1.5 |
| Stripe product family | 1.5 |
| Abstract factory interface | 1.5 |
| Concrete factories | 1.5 |
| Factory selector | 1 |
| PaymentGatewayService orchestration | 1.5 |
| Main tests all operations for both providers | 1 |
| Validation and error handling | 1 |
| Output clarity | 0.5 |
| Code readability and naming | 1 |

Final score should be normalized to **10**.

---

# 22. What a Strong Solution Looks Like

A strong Module 1 solution should have:

```text
1. Clear Abstract Factory interface.
2. Concrete factory per provider.
3. Separate product interfaces.
4. Provider-specific concrete products.
5. No family mixing.
6. Clean request models.
7. Clean result models.
8. Service layer using the factory selector.
9. Main only used for testing/output.
10. Meaningful validation.
11. Meaningful output.
```

---

# 23. Final Learning Goal

By the end of Module 1, you should understand this difference clearly:

```text
Factory Pattern:
Creates one object.

Abstract Factory Pattern:
Creates a family of related objects.
```

In this module:

```text
RazorpayPaymentProviderFactory creates:
- RazorpayPaymentClient
- RazorpayRefundClient
- RazorpayStatusClient
- RazorpayWebhookVerifier

StripePaymentProviderFactory creates:
- StripePaymentClient
- StripeRefundClient
- StripeStatusClient
- StripeWebhookVerifier
```

That family consistency is the core of Abstract Factory.

---

# 24. Module 1 Completion Standard

You can consider Module 1 complete when:

```text
1. Razorpay payment works.
2. Razorpay refund works.
3. Razorpay status lookup works.
4. Razorpay webhook verification works.
5. Stripe payment works.
6. Stripe refund works.
7. Stripe status lookup works.
8. Stripe webhook verification works.
9. Main does not create concrete clients directly.
10. PaymentGatewayService does not contain provider-specific business logic.
11. Factories create complete provider families.
12. Output clearly shows correct provider-specific behavior.
```

If all of the above are true, then you have understood the first level of Abstract Factory Design Pattern.
