# Abstract Factory Design Pattern — Module 2

## Notification Provider Suite Abstract Factory

> Note: The correct standard design-pattern name is **Abstract Factory Design Pattern**.  
> The phrase “Absolute Factory” is commonly used by mistake, but the actual pattern is **Abstract Factory**.

---

# 1. Module Metadata

## Module Name

**Notification Provider Suite Abstract Factory**

## Difficulty Level

**Module 2 — Backend Intermediate**

This module is intentionally harder than Module 1.

It is tailored for a backend engineer with around **5–6 years of experience** working in a product-based company.

This module is not a toy notification example. It focuses on:

```text
provider-specific object families
service orchestration
recipient validation
template rendering
notification sending
delivery tracking
retry decisions
failure handling
clean LLD separation
backend-grade result modeling
```

---

# 2. What You Are Building

You are building a backend notification dispatch system.

Your product supports three notification providers/channels:

```text
EMAIL
SMS
WHATSAPP
```

Each provider is not represented by only one class. Each provider has a complete provider-specific toolkit.

For `EMAIL`, the family is:

```text
EmailRecipientValidator
EmailTemplateRenderer
EmailNotificationSender
EmailDeliveryTracker
EmailRetryPolicy
```

For `SMS`, the family is:

```text
SmsRecipientValidator
SmsTemplateRenderer
SmsNotificationSender
SmsDeliveryTracker
SmsRetryPolicy
```

For `WHATSAPP`, the family is:

```text
WhatsAppRecipientValidator
WhatsAppTemplateRenderer
WhatsAppNotificationSender
WhatsAppDeliveryTracker
WhatsAppRetryPolicy
```

The main goal is:

```text
When provider = EMAIL, every component used in the flow should be EMAIL-specific.
When provider = SMS, every component used in the flow should be SMS-specific.
When provider = WHATSAPP, every component used in the flow should be WHATSAPP-specific.
```

That is the core idea of **Abstract Factory**.

---

# 3. Why This Is Abstract Factory

A normal Factory Pattern usually creates one object.

Example:

```java
NotificationSender sender = notificationSenderFactory.getSender(NotificationProvider.EMAIL);
```

That creates only one product:

```text
NotificationSender
```

But in this module, a provider needs multiple related products:

```text
RecipientValidator
TemplateRenderer
NotificationSender
DeliveryTracker
RetryPolicy
```

So one selected factory should create the complete family.

Example:

```java
NotificationProviderFactory factory =
        notificationProviderFactorySelector.getFactory(NotificationProvider.EMAIL);

RecipientValidator validator = factory.createRecipientValidator();
TemplateRenderer renderer = factory.createTemplateRenderer();
NotificationSender sender = factory.createNotificationSender();
DeliveryTracker tracker = factory.createDeliveryTracker();
RetryPolicy retryPolicy = factory.createRetryPolicy();
```

If the provider is `EMAIL`, all these objects must belong to the EMAIL family.

This is exactly Abstract Factory.

---

# 4. Why Module 2 Is Harder Than Module 1

Module 1 focused on creating a payment provider toolkit:

```text
PaymentClient
RefundClient
PaymentStatusClient
WebhookVerifier
```

Each operation was mostly independent.

Module 2 is harder because the selected family is used together inside one dispatch workflow.

The flow is:

```text
NotificationRequest
    -> RecipientValidator
    -> TemplateRenderer
    -> NotificationSender
    -> RetryPolicy
    -> DeliveryTracker
    -> NotificationDispatchResult
```

So you are not only creating classes. You are designing an orchestration pipeline.

This is closer to real backend product code.


---

# 5. Business Context

In a real product backend, notifications are sent for many use cases:

```text
OTP login
payment success
refund processed
security alert
account update
promotional campaign
password reset
device login alert
transaction failure
subscription reminder
```

Different channels behave differently.

## EMAIL

```text
Requires email address
Supports subject
Supports long body
Can support HTML content
Usually delivery can be tracked as DELIVERED
```

## SMS

```text
Requires phone number and country code
Does not use subject
Body should be short
Often provider initially confirms SENT, not DELIVERED
```

## WHATSAPP

```text
Requires phone number and country code
Usually template-based
Does not use subject
Often used for transactional and security alerts
Can be tracked as DELIVERED in this simulation
```

Since each provider has different validation, rendering, sending, tracking, and retry behavior, Abstract Factory is a good fit.

---

# 6. High-Level Design

The intended flow is:

```text
Main
  -> NotificationDispatchService.dispatch(request)
      -> NotificationProviderFactorySelector.getFactory(provider)
          -> NotificationProviderFactory
              -> RecipientValidator
              -> TemplateRenderer
              -> NotificationSender
              -> RetryPolicy
              -> DeliveryTracker
      -> NotificationDispatchResult
```

For EMAIL:

```text
NotificationDispatchService
  -> EmailNotificationProviderFactory
      -> EmailRecipientValidator
      -> EmailTemplateRenderer
      -> EmailNotificationSender
      -> EmailRetryPolicy
      -> EmailDeliveryTracker
```

For SMS:

```text
NotificationDispatchService
  -> SmsNotificationProviderFactory
      -> SmsRecipientValidator
      -> SmsTemplateRenderer
      -> SmsNotificationSender
      -> SmsRetryPolicy
      -> SmsDeliveryTracker
```

For WHATSAPP:

```text
NotificationDispatchService
  -> WhatsAppNotificationProviderFactory
      -> WhatsAppRecipientValidator
      -> WhatsAppTemplateRenderer
      -> WhatsAppNotificationSender
      -> WhatsAppRetryPolicy
      -> WhatsAppDeliveryTracker
```

---

# 7. Required Classes

Create the following classes/interfaces/enums:

```text
NotificationProvider
NotificationType
NotificationPriority
DeliveryStatus

RecipientDetails
NotificationRequest
RenderedNotification
SendResult
DeliveryTrackingRequest
DeliveryTrackingResult
RetryDecision
NotificationDispatchResult

RecipientValidator
TemplateRenderer
NotificationSender
DeliveryTracker
RetryPolicy

EmailRecipientValidator
EmailTemplateRenderer
EmailNotificationSender
EmailDeliveryTracker
EmailRetryPolicy

SmsRecipientValidator
SmsTemplateRenderer
SmsNotificationSender
SmsDeliveryTracker
SmsRetryPolicy

WhatsAppRecipientValidator
WhatsAppTemplateRenderer
WhatsAppNotificationSender
WhatsAppDeliveryTracker
WhatsAppRetryPolicy

NotificationProviderFactory
EmailNotificationProviderFactory
SmsNotificationProviderFactory
WhatsAppNotificationProviderFactory

NotificationProviderFactorySelector
NotificationDispatchService
Main
```

For a backend engineer with 5–6 years of experience, this module tests not only the pattern, but also service design and flow correctness.


---

# 8. Required Enums

## 8.1 `NotificationProvider`

Represents the notification provider/channel family.

```java
public enum NotificationProvider {
    EMAIL,
    SMS,
    WHATSAPP
}
```

It avoids raw strings like `"email"`, `"sms"`, or `"whatsapp"`.

It should not contain validation, sending, rendering, or retry logic.

## 8.2 `NotificationType`

Represents the notification business use case.

```java
public enum NotificationType {
    OTP,
    TRANSACTIONAL,
    PROMOTIONAL,
    SECURITY_ALERT
}
```

Different notification types may later have different rules.

## 8.3 `NotificationPriority`

Represents the importance of notification.

```java
public enum NotificationPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
```

Retry policy depends on priority.

## 8.4 `DeliveryStatus`

Represents delivery state.

```java
public enum DeliveryStatus {
    ACCEPTED,
    SENT,
    DELIVERED,
    FAILED,
    RETRY_SCHEDULED
}
```

Meaning:

```text
ACCEPTED         -> provider accepted the message
SENT             -> provider sent message to carrier/channel
DELIVERED        -> final delivery confirmation simulated
FAILED           -> dispatch failed and no retry is scheduled
RETRY_SCHEDULED  -> dispatch failed but retry is scheduled
```

For this module:

```text
EMAIL     -> DELIVERED on success
SMS       -> SENT on success
WHATSAPP  -> DELIVERED on success
```

---

# 9. Request and Result Models

## 9.1 `RecipientDetails`

Represents recipient contact details.

Fields:

```text
customerId
email
phoneNumber
countryCode
```

Different providers require different recipient fields:

```text
EMAIL     -> email
SMS       -> phoneNumber + countryCode
WHATSAPP  -> phoneNumber + countryCode
```

This class should perform only generic recipient validation:

```text
customerId cannot be null/blank
at least one of email or phoneNumber must be present
```

Provider-specific validation belongs in provider validators.

Suggested code:

```java
public class RecipientDetails {

    private final String customerId;
    private final String email;
    private final String phoneNumber;
    private final String countryCode;

    public RecipientDetails(
            String customerId,
            String email,
            String phoneNumber,
            String countryCode
    ) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank");
        }

        if ((email == null || email.isBlank())
                && (phoneNumber == null || phoneNumber.isBlank())) {
            throw new IllegalArgumentException("Either email or phone number must be present");
        }

        this.customerId = customerId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
```


## 9.2 `NotificationRequest`

Represents a request to dispatch one notification.

Fields:

```text
notificationId
provider
type
recipientDetails
templateCode
priority
templateVariables
createdAt
```

Suggested constructor order:

```java
NotificationRequest(
        String notificationId,
        NotificationProvider provider,
        NotificationType type,
        RecipientDetails recipientDetails,
        String templateCode,
        NotificationPriority priority,
        Map<String, String> templateVariables
)
```

Validation expectations:

```text
notificationId cannot be null/blank
provider cannot be null
type cannot be null
recipientDetails cannot be null
templateCode cannot be null/blank
priority cannot be null
templateVariables cannot be null
```

Use defensive copy:

```java
Map.copyOf(templateVariables)
```

But validate before calling `Map.copyOf()`.

Suggested code:

```java
import java.time.LocalDateTime;
import java.util.Map;

public class NotificationRequest {

    private final String notificationId;
    private final NotificationProvider provider;
    private final NotificationType type;
    private final RecipientDetails recipientDetails;
    private final String templateCode;
    private final NotificationPriority priority;
    private final Map<String, String> templateVariables;
    private final LocalDateTime createdAt;

    public NotificationRequest(
            String notificationId,
            NotificationProvider provider,
            NotificationType type,
            RecipientDetails recipientDetails,
            String templateCode,
            NotificationPriority priority,
            Map<String, String> templateVariables
    ) {
        if (notificationId == null || notificationId.isBlank()) {
            throw new IllegalArgumentException("Notification ID cannot be null or blank");
        }

        if (provider == null) {
            throw new IllegalArgumentException("Notification provider cannot be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }

        if (recipientDetails == null) {
            throw new IllegalArgumentException("Recipient details cannot be null");
        }

        if (templateCode == null || templateCode.isBlank()) {
            throw new IllegalArgumentException("Template code cannot be null or blank");
        }

        if (priority == null) {
            throw new IllegalArgumentException("Notification priority cannot be null");
        }

        if (templateVariables == null) {
            throw new IllegalArgumentException("Template variables cannot be null");
        }

        this.notificationId = notificationId;
        this.provider = provider;
        this.type = type;
        this.recipientDetails = recipientDetails;
        this.templateCode = templateCode;
        this.priority = priority;
        this.templateVariables = Map.copyOf(templateVariables);
        this.createdAt = LocalDateTime.now();
    }

    public String getNotificationId() {
        return notificationId;
    }

    public NotificationProvider getProvider() {
        return provider;
    }

    public NotificationType getType() {
        return type;
    }

    public RecipientDetails getRecipientDetails() {
        return recipientDetails;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public Map<String, String> getTemplateVariables() {
        return templateVariables;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
```

---

## 9.3 `RenderedNotification`

Represents rendered notification content.

Fields:

```text
notificationId
provider
recipientDetails
subject
body
```

The renderer builds final content. The sender should not build message content.

Provider behavior:

```text
EMAIL     -> subject should be non-null
SMS       -> subject should be null
WHATSAPP  -> subject should be null
```


## 9.4 `SendResult`

Represents the result of provider send operation.

Fields:

```text
notificationId
provider
success
providerMessageId
message
failureReason
```

Success semantics:

```text
success = true
providerMessageId = generated provider message ID
failureReason = null
```

Failure semantics:

```text
success = false
providerMessageId = null
failureReason = actual reason
```

## 9.5 `DeliveryTrackingRequest`

Represents input needed to track delivery.

Fields:

```text
notificationId
providerMessageId
provider
```

## 9.6 `DeliveryTrackingResult`

Represents delivery tracking result.

Fields:

```text
notificationId
provider
providerMessageId
deliveryStatus
message
```

## 9.7 `RetryDecision`

Represents whether a failed send should be retried.

Fields:

```text
shouldRetry
maxAttempts
delayInSeconds
reason
```

Suggested code:

```java
public class RetryDecision {

    private final boolean shouldRetry;
    private final int maxAttempts;
    private final int delayInSeconds;
    private final String reason;

    public RetryDecision(
            boolean shouldRetry,
            int maxAttempts,
            int delayInSeconds,
            String reason
    ) {
        this.shouldRetry = shouldRetry;
        this.maxAttempts = maxAttempts;
        this.delayInSeconds = delayInSeconds;
        this.reason = reason;
    }

    public boolean isShouldRetry() {
        return shouldRetry;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public int getDelayInSeconds() {
        return delayInSeconds;
    }

    public String getReason() {
        return reason;
    }
}
```

## 9.8 `NotificationDispatchResult`

Represents final result returned by dispatch service.

Fields:

```text
notificationId
provider
success
deliveryStatus
providerMessageId
message
retryDecision
failureReason
```

Success semantics:

```text
success = true
deliveryStatus = SENT/DELIVERED
providerMessageId = not null
failureReason = null
```

Failure before send example:

```text
success = false
deliveryStatus = FAILED
providerMessageId = null
retryDecision.shouldRetry = false
failureReason = validation error
```

Failure after send example:

```text
success = false
deliveryStatus = RETRY_SCHEDULED or FAILED
providerMessageId = null
retryDecision based on priority
failureReason = provider failure reason
```


---

# 10. Product Interfaces

These are the products created by the abstract factory.

## 10.1 `RecipientValidator`

```java
public interface RecipientValidator {
    void validate(NotificationRequest request);
}
```

Responsibility:

```text
Validate recipient details for provider.
```

Examples:

```text
EmailRecipientValidator -> validates email
SmsRecipientValidator -> validates phone number and country code
WhatsAppRecipientValidator -> validates phone number and country code
```

## 10.2 `TemplateRenderer`

```java
public interface TemplateRenderer {
    RenderedNotification render(NotificationRequest request);
}
```

Responsibility:

```text
Create provider-specific message content.
```

It should not send notification.

## 10.3 `NotificationSender`

```java
public interface NotificationSender {
    SendResult send(RenderedNotification renderedNotification);
}
```

Responsibility:

```text
Send already-rendered notification.
```

It should not validate recipient and should not render templates.

## 10.4 `DeliveryTracker`

```java
public interface DeliveryTracker {
    DeliveryTrackingResult track(DeliveryTrackingRequest request);
}
```

Responsibility:

```text
Track provider delivery status.
```

## 10.5 `RetryPolicy`

```java
public interface RetryPolicy {
    RetryDecision evaluate(NotificationRequest request, SendResult sendResult);
}
```

Responsibility:

```text
Decide whether a failed send should be retried.
```

It should not send notification, render content, or track delivery.

---

# 11. Abstract Factory Interface

## `NotificationProviderFactory`

This is the main Abstract Factory interface.

```java
public interface NotificationProviderFactory {

    RecipientValidator createRecipientValidator();

    TemplateRenderer createTemplateRenderer();

    NotificationSender createNotificationSender();

    DeliveryTracker createDeliveryTracker();

    RetryPolicy createRetryPolicy();
}
```

This ensures every provider factory can create the complete provider family.

For EMAIL:

```text
EmailRecipientValidator
EmailTemplateRenderer
EmailNotificationSender
EmailDeliveryTracker
EmailRetryPolicy
```

For SMS:

```text
SmsRecipientValidator
SmsTemplateRenderer
SmsNotificationSender
SmsDeliveryTracker
SmsRetryPolicy
```

For WHATSAPP:

```text
WhatsAppRecipientValidator
WhatsAppTemplateRenderer
WhatsAppNotificationSender
WhatsAppDeliveryTracker
WhatsAppRetryPolicy
```


---

# 12. Concrete Provider Families

## 12.1 EMAIL Product Family

### `EmailRecipientValidator`

Checks:

```text
email is not null/blank
email contains "@"
email contains "."
```

Suggested code:

```java
public class EmailRecipientValidator implements RecipientValidator {

    @Override
    public void validate(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Notification request cannot be null");
        }

        RecipientDetails recipientDetails = request.getRecipientDetails();

        if (recipientDetails == null) {
            throw new IllegalArgumentException("Email recipient details cannot be null");
        }

        String email = recipientDetails.getEmail();

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email address for EMAIL notification");
        }
    }
}
```

### `EmailTemplateRenderer`

Should create:

```text
subject = "Email Notification: <templateCode>"
body = content using templateVariables
```

### `EmailNotificationSender`

Should return success normally.

Should simulate failure if body contains `FAIL_SEND`.

Provider message ID format:

```text
EMAIL-<notificationId>-<uuid>
```

### `EmailDeliveryTracker`

Returns:

```text
DeliveryStatus.DELIVERED
```

### `EmailRetryPolicy`

Rules:

```text
If send success -> no retry
If failed + CRITICAL -> retry, maxAttempts = 5, delay = 30
If failed + HIGH -> retry, maxAttempts = 3, delay = 60
Else no retry
```

## 12.2 SMS Product Family

### `SmsRecipientValidator`

Checks:

```text
phoneNumber is not null/blank
countryCode is not null/blank
```

### `SmsTemplateRenderer`

SMS should not have subject.

```java
public class SmsTemplateRenderer implements TemplateRenderer {

    @Override
    public RenderedNotification render(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Notification request cannot be null");
        }

        String body = "SMS Template: "
                + request.getTemplateCode()
                + " | Details: "
                + request.getTemplateVariables();

        return new RenderedNotification(
                request.getNotificationId(),
                NotificationProvider.SMS,
                request.getRecipientDetails(),
                null,
                body
        );
    }
}
```

### `SmsNotificationSender`

Should return success.

Provider message ID format:

```text
SMS-<notificationId>-<uuid>
```

### `SmsDeliveryTracker`

Should return:

```text
DeliveryStatus.SENT
```

Reason:

```text
SMS provider may initially confirm sent-to-carrier, not final handset delivery.
```

### `SmsRetryPolicy`

Rules:

```text
If send success -> no retry
If failed + CRITICAL -> retry, maxAttempts = 3, delay = 20
Else no retry
```

## 12.3 WHATSAPP Product Family

### `WhatsAppRecipientValidator`

Checks:

```text
phoneNumber is not null/blank
countryCode is not null/blank
```

### `WhatsAppTemplateRenderer`

WhatsApp should not have subject.

Body should look like a template message.

### `WhatsAppNotificationSender`

Should return success.

Provider message ID format:

```text
WHATSAPP-<notificationId>-<uuid>
```

### `WhatsAppDeliveryTracker`

Should return:

```text
DeliveryStatus.DELIVERED
```

### `WhatsAppRetryPolicy`

Rules:

```text
If send success -> no retry
If failed + CRITICAL -> retry, maxAttempts = 4, delay = 30
If failed + HIGH -> retry, maxAttempts = 2, delay = 60
Else no retry
```


---

# 13. Concrete Provider Factories

## 13.1 `EmailNotificationProviderFactory`

```java
public class EmailNotificationProviderFactory implements NotificationProviderFactory {

    @Override
    public RecipientValidator createRecipientValidator() {
        return new EmailRecipientValidator();
    }

    @Override
    public TemplateRenderer createTemplateRenderer() {
        return new EmailTemplateRenderer();
    }

    @Override
    public NotificationSender createNotificationSender() {
        return new EmailNotificationSender();
    }

    @Override
    public DeliveryTracker createDeliveryTracker() {
        return new EmailDeliveryTracker();
    }

    @Override
    public RetryPolicy createRetryPolicy() {
        return new EmailRetryPolicy();
    }
}
```

## 13.2 `SmsNotificationProviderFactory`

```java
public class SmsNotificationProviderFactory implements NotificationProviderFactory {

    @Override
    public RecipientValidator createRecipientValidator() {
        return new SmsRecipientValidator();
    }

    @Override
    public TemplateRenderer createTemplateRenderer() {
        return new SmsTemplateRenderer();
    }

    @Override
    public NotificationSender createNotificationSender() {
        return new SmsNotificationSender();
    }

    @Override
    public DeliveryTracker createDeliveryTracker() {
        return new SmsDeliveryTracker();
    }

    @Override
    public RetryPolicy createRetryPolicy() {
        return new SmsRetryPolicy();
    }
}
```

## 13.3 `WhatsAppNotificationProviderFactory`

```java
public class WhatsAppNotificationProviderFactory implements NotificationProviderFactory {

    @Override
    public RecipientValidator createRecipientValidator() {
        return new WhatsAppRecipientValidator();
    }

    @Override
    public TemplateRenderer createTemplateRenderer() {
        return new WhatsAppTemplateRenderer();
    }

    @Override
    public NotificationSender createNotificationSender() {
        return new WhatsAppNotificationSender();
    }

    @Override
    public DeliveryTracker createDeliveryTracker() {
        return new WhatsAppDeliveryTracker();
    }

    @Override
    public RetryPolicy createRetryPolicy() {
        return new WhatsAppRetryPolicy();
    }
}
```

---

# 14. Factory Selector

## `NotificationProviderFactorySelector`

Selects the correct abstract factory for the notification provider.

Suggested code:

```java
public class NotificationProviderFactorySelector {

    public NotificationProviderFactory getFactory(NotificationProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Notification provider cannot be null");
        }

        return switch (provider) {
            case EMAIL -> new EmailNotificationProviderFactory();
            case SMS -> new SmsNotificationProviderFactory();
            case WHATSAPP -> new WhatsAppNotificationProviderFactory();
        };
    }
}
```

Do not add unnecessary `default` when all enum cases are handled.

Why?

Because if you add a new enum later, the compiler can force you to update the switch.


---

# 15. Service Layer

## `NotificationDispatchService`

Coordinates the full notification dispatch pipeline.

It should:

```text
1. Validate basic request.
2. Get provider factory.
3. Get provider-specific validator.
4. Validate recipient.
5. Get provider-specific renderer.
6. Render notification.
7. Get provider-specific sender.
8. Send notification.
9. Get provider-specific retry policy.
10. Evaluate retry decision.
11. If send failed, return failure result.
12. If send succeeded, track delivery.
13. Return final NotificationDispatchResult.
```

Suggested code:

```java
public class NotificationDispatchService {

    private final NotificationProviderFactorySelector notificationProviderFactorySelector;

    public NotificationDispatchService(
            NotificationProviderFactorySelector notificationProviderFactorySelector
    ) {
        if (notificationProviderFactorySelector == null) {
            throw new IllegalArgumentException("notificationProviderFactorySelector cannot be null");
        }

        this.notificationProviderFactorySelector = notificationProviderFactorySelector;
    }

    public NotificationDispatchResult dispatch(NotificationRequest request) {
        validateRequest(request);

        NotificationProvider provider = request.getProvider();

        try {
            NotificationProviderFactory factory =
                    notificationProviderFactorySelector.getFactory(provider);

            RecipientValidator recipientValidator =
                    factory.createRecipientValidator();

            TemplateRenderer templateRenderer =
                    factory.createTemplateRenderer();

            NotificationSender notificationSender =
                    factory.createNotificationSender();

            RetryPolicy retryPolicy =
                    factory.createRetryPolicy();

            DeliveryTracker deliveryTracker =
                    factory.createDeliveryTracker();

            recipientValidator.validate(request);

            RenderedNotification renderedNotification =
                    templateRenderer.render(request);

            SendResult sendResult =
                    notificationSender.send(renderedNotification);

            RetryDecision retryDecision =
                    retryPolicy.evaluate(request, sendResult);

            if (!sendResult.isSuccess()) {
                DeliveryStatus finalStatus = retryDecision.isShouldRetry()
                        ? DeliveryStatus.RETRY_SCHEDULED
                        : DeliveryStatus.FAILED;

                String message = retryDecision.isShouldRetry()
                        ? "Notification dispatch failed, retry scheduled"
                        : "Notification dispatch failed";

                return new NotificationDispatchResult(
                        request.getNotificationId(),
                        provider,
                        false,
                        finalStatus,
                        null,
                        message,
                        retryDecision,
                        sendResult.getFailureReason()
                );
            }

            DeliveryTrackingRequest trackingRequest =
                    new DeliveryTrackingRequest(
                            request.getNotificationId(),
                            sendResult.getProviderMessageId(),
                            provider
                    );

            DeliveryTrackingResult trackingResult =
                    deliveryTracker.track(trackingRequest);

            return new NotificationDispatchResult(
                    request.getNotificationId(),
                    provider,
                    true,
                    trackingResult.getDeliveryStatus(),
                    sendResult.getProviderMessageId(),
                    provider + " notification dispatched successfully",
                    retryDecision,
                    null
            );

        } catch (IllegalArgumentException exception) {
            RetryDecision retryDecision = new RetryDecision(
                    false,
                    0,
                    0,
                    "No retry because dispatch failed before send stage"
            );

            return new NotificationDispatchResult(
                    request.getNotificationId(),
                    provider,
                    false,
                    DeliveryStatus.FAILED,
                    null,
                    "Notification dispatch failed",
                    retryDecision,
                    exception.getMessage()
            );
        }
    }

    private void validateRequest(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Notification request cannot be null");
        }

        if (request.getNotificationId() == null || request.getNotificationId().isBlank()) {
            throw new IllegalArgumentException("Notification ID cannot be null or blank");
        }

        if (request.getProvider() == null) {
            throw new IllegalArgumentException("Notification provider cannot be null");
        }

        if (request.getType() == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }

        if (request.getPriority() == null) {
            throw new IllegalArgumentException("Notification priority cannot be null");
        }

        if (request.getRecipientDetails() == null) {
            throw new IllegalArgumentException("Recipient details cannot be null");
        }

        if (request.getTemplateCode() == null || request.getTemplateCode().isBlank()) {
            throw new IllegalArgumentException("Template code cannot be null or blank");
        }

        if (request.getTemplateVariables() == null) {
            throw new IllegalArgumentException("Template variables cannot be null");
        }
    }
}
```

What this service should not do:

```text
Should not directly create EmailRecipientValidator.
Should not directly create SmsTemplateRenderer.
Should not contain provider-specific if-else validation.
Should not render template itself.
Should not decide provider-specific retry logic itself.
```

The service should orchestrate interfaces only.


---

# 16. Required Main Test Cases

Your `Main` should test at least:

```text
1. EMAIL success
2. SMS success
3. WHATSAPP success
4. EMAIL invalid recipient failure
5. EMAIL send failure with retry scheduled
```

---

# 17. Suggested `Main.java`

```java
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        NotificationProviderFactorySelector selector =
                new NotificationProviderFactorySelector();

        NotificationDispatchService dispatchService =
                new NotificationDispatchService(selector);

        System.out.println("Abstract Factory Module 2 — Notification Provider Suite");

        NotificationRequest emailSuccessRequest = new NotificationRequest(
                "NOTIF-101",
                NotificationProvider.EMAIL,
                NotificationType.TRANSACTIONAL,
                new RecipientDetails(
                        "CUST-101",
                        "customer101@gmail.com",
                        null,
                        null
                ),
                "PAYMENT_SUCCESS",
                NotificationPriority.HIGH,
                Map.of(
                        "userName", "Shashwat",
                        "amount", "1500",
                        "paymentId", "PAY-101"
                )
        );

        NotificationRequest smsSuccessRequest = new NotificationRequest(
                "NOTIF-102",
                NotificationProvider.SMS,
                NotificationType.OTP,
                new RecipientDetails(
                        "CUST-102",
                        null,
                        "9876543210",
                        "+91"
                ),
                "LOGIN_OTP",
                NotificationPriority.CRITICAL,
                Map.of(
                        "userName", "Shashwat",
                        "otp", "123456"
                )
        );

        NotificationRequest whatsappSuccessRequest = new NotificationRequest(
                "NOTIF-103",
                NotificationProvider.WHATSAPP,
                NotificationType.SECURITY_ALERT,
                new RecipientDetails(
                        "CUST-103",
                        null,
                        "9876543211",
                        "+91"
                ),
                "LOGIN_ALERT",
                NotificationPriority.HIGH,
                Map.of(
                        "userName", "Shashwat",
                        "location", "Chennai",
                        "time", "10:30 PM"
                )
        );

        NotificationRequest invalidEmailRequest = new NotificationRequest(
                "NOTIF-104",
                NotificationProvider.EMAIL,
                NotificationType.TRANSACTIONAL,
                new RecipientDetails(
                        "CUST-104",
                        "invalid-email",
                        null,
                        null
                ),
                "PAYMENT_FAILED",
                NotificationPriority.HIGH,
                Map.of(
                        "userName", "Shashwat",
                        "paymentId", "PAY-404"
                )
        );

        NotificationRequest emailRetryRequest = new NotificationRequest(
                "NOTIF-105",
                NotificationProvider.EMAIL,
                NotificationType.SECURITY_ALERT,
                new RecipientDetails(
                        "CUST-105",
                        "customer105@gmail.com",
                        null,
                        null
                ),
                "FAIL_SEND_SECURITY_ALERT",
                NotificationPriority.CRITICAL,
                Map.of(
                        "userName", "Shashwat",
                        "reason", "FAIL_SEND"
                )
        );

        printResult(dispatchService.dispatch(emailSuccessRequest));
        printResult(dispatchService.dispatch(smsSuccessRequest));
        printResult(dispatchService.dispatch(whatsappSuccessRequest));
        printResult(dispatchService.dispatch(invalidEmailRequest));
        printResult(dispatchService.dispatch(emailRetryRequest));
    }

    private static void printResult(NotificationDispatchResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Notification ID: " + result.getNotificationId());
        System.out.println("Provider: " + result.getProvider());
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Delivery Status: " + result.getDeliveryStatus());
        System.out.println("Provider Message ID: " + result.getProviderMessageId());
        System.out.println("Message: " + result.getMessage());

        if (result.getRetryDecision() != null) {
            System.out.println("Retry Required: " + result.getRetryDecision().isShouldRetry());
            System.out.println("Max Attempts: " + result.getRetryDecision().getMaxAttempts());
            System.out.println("Retry Delay Seconds: " + result.getRetryDecision().getDelayInSeconds());
            System.out.println("Retry Reason: " + result.getRetryDecision().getReason());
        }

        System.out.println("Failure Reason: " + result.getFailureReason());
        System.out.println("==================================================");
    }
}
```


---

# 18. Expected Output Behavior

Your output should prove:

```text
NOTIF-101 -> EMAIL success, DELIVERED
NOTIF-102 -> SMS success, SENT
NOTIF-103 -> WHATSAPP success, DELIVERED
NOTIF-104 -> EMAIL failure, FAILED, invalid email
NOTIF-105 -> EMAIL send failure, RETRY_SCHEDULED, retry required
```

Example:

```text
==================================================
Notification ID: NOTIF-104
Provider: EMAIL
Success: false
Delivery Status: FAILED
Provider Message ID: null
Message: Notification dispatch failed
Retry Required: false
Max Attempts: 0
Retry Delay Seconds: 0
Retry Reason: No retry because dispatch failed before send stage
Failure Reason: Invalid email address for EMAIL notification
==================================================

==================================================
Notification ID: NOTIF-105
Provider: EMAIL
Success: false
Delivery Status: RETRY_SCHEDULED
Provider Message ID: null
Message: Notification dispatch failed, retry scheduled
Retry Required: true
Max Attempts: 5
Retry Delay Seconds: 30
Retry Reason: CRITICAL priority EMAIL notification failed, retry required
Failure Reason: Simulated EMAIL provider failure
==================================================
```

---

# 19. Important Design Rules

## Rule 1: Do not create provider-specific classes in `Main`

Wrong:

```java
new EmailNotificationSender()
new SmsTemplateRenderer()
```

Correct:

```java
NotificationDispatchService service = new NotificationDispatchService(selector);
service.dispatch(request);
```

## Rule 2: Do not mix families

Wrong:

```java
RecipientValidator validator = new EmailRecipientValidator();
TemplateRenderer renderer = new SmsTemplateRenderer();
NotificationSender sender = new WhatsAppNotificationSender();
```

Correct:

```java
NotificationProviderFactory factory =
        notificationProviderFactorySelector.getFactory(NotificationProvider.EMAIL);

RecipientValidator validator = factory.createRecipientValidator();
TemplateRenderer renderer = factory.createTemplateRenderer();
NotificationSender sender = factory.createNotificationSender();
```

## Rule 3: Service should orchestrate, not contain provider logic

Wrong:

```java
if (request.getProvider() == NotificationProvider.EMAIL) {
    if (!request.getRecipientDetails().getEmail().contains("@")) {
        throw new IllegalArgumentException("Invalid email");
    }
}
```

Correct:

```java
RecipientValidator validator = factory.createRecipientValidator();
validator.validate(request);
```

## Rule 4: Renderer should render; sender should send

Wrong:

```java
public SendResult send(NotificationRequest request) {
    String body = "Hello " + request.getTemplateVariables().get("name");
    return send(body);
}
```

Correct:

```java
RenderedNotification rendered = renderer.render(request);
SendResult sendResult = sender.send(rendered);
```

## Rule 5: Delivery tracker should run only after send success

If send fails, do not track delivery.

Correct:

```text
send failed -> retry policy -> return FAILED or RETRY_SCHEDULED
```

Wrong:

```text
send failed -> delivery tracker -> DELIVERED
```

## Rule 6: Invalid recipient failure is not a retryable provider-send failure

Invalid email should fail before sending.

Example:

```text
invalid email -> FAILED -> shouldRetry = false
```

It should not become:

```text
invalid email -> RETRY_SCHEDULED
```

Because retrying the same invalid email will not help.

---

# 20. Common Mistakes

```text
1. Creating concrete products directly in Main.
2. Mixing Email validator with SMS renderer.
3. Service containing provider-specific if-else logic.
4. Sender building template body.
5. Renderer sending notification.
6. Delivery tracker running even when send failed.
7. Returning null result objects.
8. Invalid email passing successfully.
9. SMS delivery returning DELIVERED instead of SENT.
10. Retry policy not being tested.
11. HIGH-priority retry message accidentally saying CRITICAL.
12. Inconsistent WhatsApp/Whatsapp class naming.
13. Constructors throwing NullPointerException because Map.copyOf() is called before null validation.
14. SMS/WhatsApp renderers using subject.
```

---

# 21. Scoring Rubric

| Area | Marks |
|---|---:|
| Enums | 1 |
| Request/result models | 2 |
| Product interfaces | 1.5 |
| Email product family | 1.5 |
| SMS product family | 1.5 |
| WhatsApp product family | 1.5 |
| Abstract factory interface | 1.5 |
| Concrete factories | 1.5 |
| Factory selector | 1 |
| Dispatch service orchestration | 2 |
| Validation and failure handling | 1.5 |
| Main test coverage | 1 |
| Output clarity | 0.5 |
| Naming/readability/backend cleanliness | 1 |

Total marks should be normalized to **10**.

---

# 22. Strong Solution Checklist

A strong Module 2 solution should have:

```text
1. Complete EMAIL product family.
2. Complete SMS product family.
3. Complete WHATSAPP product family.
4. Abstract factory interface creating all five product types.
5. Concrete factory per provider.
6. Factory selector returning the correct provider factory.
7. Dispatch service orchestrating through interfaces only.
8. No provider-specific if-else logic in the dispatch service.
9. Recipient validators performing provider-specific validation.
10. Template renderers creating provider-specific content.
11. Senders sending rendered content only.
12. Retry policies deciding retry only.
13. Trackers tracking only after successful send.
14. Invalid email handled as FAILED.
15. Provider send failure handled as RETRY_SCHEDULED when policy allows.
16. Main testing success and failure paths.
```

---

# 23. Final Learning Goal

By the end of Module 2, you should understand Abstract Factory more deeply than Module 1.

Module 1 taught:

```text
A provider factory creates a toolkit.
```

Module 2 teaches:

```text
A provider factory creates a toolkit that is used together inside a real backend workflow.
```

The final mental model:

```text
NotificationProviderFactory
    -> RecipientValidator
    -> TemplateRenderer
    -> NotificationSender
    -> DeliveryTracker
    -> RetryPolicy
```

And the service flow:

```text
validate recipient
render content
send notification
evaluate retry
track delivery
return final result
```

That is production-style Abstract Factory thinking.

---

# 24. Module 2 Completion Standard

You can consider Module 2 complete when:

```text
1. EMAIL success works.
2. SMS success works.
3. WHATSAPP success works.
4. Invalid email fails before send.
5. Email send failure triggers retry.
6. SMS returns SENT on success.
7. EMAIL/WHATSAPP return DELIVERED on success.
8. Dispatch service has no provider-specific if-else.
9. Main does not create concrete products directly.
10. Provider factories create complete provider families.
11. Retry policy behavior is visible in output.
12. Final output clearly proves success, failure, and retry flows.
```

If all of this is true, you have completed Module 2 at a strong backend LLD level.
