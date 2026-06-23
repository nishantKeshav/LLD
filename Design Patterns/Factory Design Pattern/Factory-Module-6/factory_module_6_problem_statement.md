# Factory Design Pattern Practice — Module 6

## Notification Channel Factory with Registry and Fallback Routing

### Difficulty Level

**Advanced+**

### Pattern Focus

**Registry-based Factory Pattern with tenant-aware routing, priority-based selection, fallback channels, request validation, and structured notification result handling**

### Backend Theme

**Multi-channel notification dispatch system for SaaS / fintech / e-commerce backend applications**

---

# 1. Module Overview

This is **Module 6** of the Factory Design Pattern practice series.

The previous modules gradually increased in difficulty:

```text
Module 1: NotificationType -> NotificationSender
Module 2: PaymentMode -> PaymentProcessor
Module 3: FileType -> DocumentParser
Module 4: ReportFormat -> ReportGenerator
Module 5: DeliveryRequest -> LogisticsPartnerFactory -> DeliveryPartner
```

Module 5 introduced a business-rule-based factory. Module 6 goes one step further.

In this module, the factory should **not use a switch statement directly**. Instead, the factory should use a **registry/map-based lookup**.

The system supports these notification channels:

```text
EMAIL
SMS
WHATSAPP
PUSH
```

The final runtime flow should look like this:

```text
NotificationRequest
    -> NotificationDispatchService
        -> NotificationRoutingService
            -> TenantNotificationConfigService
            -> NotificationChannelFactory
                -> NotificationChannelRegistry
                    -> NotificationChannel
                        -> NotificationResult
```

---

# 2. Why This Module Exists

Real backend notification systems are rarely simple.

A system may support multiple channels:

```text
Email
SMS
WhatsApp
Push Notification
In-app Notification
Voice Call
Slack
Webhook
```

But not every channel should always be used.

Example cases:

```text
A tenant may have SMS disabled.
A channel may not support CRITICAL priority.
A user may not have a device token for push notification.
WhatsApp may need a phone number.
Email may need an email address.
```

So the backend must decide:

```text
Which channel should be used for this notification request?
```

That decision may depend on:

```text
tenantId
preferredChannel
fallbackChannels
priority
recipientEmail
recipientPhone
deviceToken
enabled channels for the tenant
supported priorities for each channel
```

This is more complex than a simple enum-to-class mapping.


---

# 3. Core Factory Pattern Idea in This Module

Factory Pattern is used when:

```text
You have multiple implementations of a common interface,
and you need a clean way to obtain the correct implementation.
```

In earlier modules, the factory usually did this:

```java
return switch (type) {
    case EMAIL -> new EmailNotificationChannel();
    case SMS -> new SmsNotificationChannel();
};
```

In Module 6, this is not preferred.

Instead, you build a registry:

```java
Map<NotificationChannelType, NotificationChannel>
```

Then the factory simply fetches the channel:

```java
return registry.get(channelType);
```

So the Factory Pattern still exists, but the selection is registry-based instead of switch-based.

This makes the factory more extensible.

---

# 4. Why Module 6 Is Harder Than Module 5

Module 5 used business-rule-based selection:

```text
DeliveryRequest -> LogisticsPartnerFactory -> DeliveryPartner
```

Module 6 is harder because it introduces multiple layers:

```text
NotificationRequest
    -> DispatchService
    -> RoutingService
    -> TenantConfigService
    -> Factory
    -> Registry
    -> Channel
    -> NotificationResult
```

Module 6 adds:

```text
1. Registry-based factory instead of switch-based factory.
2. Tenant-specific channel configuration.
3. Preferred channel routing.
4. Fallback channel routing.
5. Priority support checks.
6. Recipient-detail checks.
7. Failure result modeling.
8. Attempted channel tracking.
9. Separation between factory and routing logic.
```

The factory is not responsible for all decision-making.

Instead:

```text
NotificationChannelFactory
    -> fetches channel by type

NotificationRoutingService
    -> decides which channel should be used

NotificationDispatchService
    -> validates request and performs dispatch
```

---

# 5. Problem Without Registry-Based Factory

Without registry-based factory, the factory may look like this:

```java
public class NotificationChannelFactory {

    public NotificationChannel getChannel(NotificationChannelType channelType) {
        return switch (channelType) {
            case EMAIL -> new EmailNotificationChannel();
            case SMS -> new SmsNotificationChannel();
            case WHATSAPP -> new WhatsappNotificationChannel();
            case PUSH -> new PushNotificationChannel();
        };
    }
}
```

This works, but it is less flexible.

## Problems

```text
1. Factory must change every time a new channel is added.
2. Channel registration is hardcoded.
3. Object creation and channel lookup are tightly coupled.
4. It is harder to enable dynamic channel registration.
5. It is harder to test by injecting mock channels.
6. The factory becomes bigger as channel count grows.
```

---

# 6. Problem With Registry-Based Factory

With registry-based factory, channels are registered in one place:

```java
NotificationChannelRegistry registry = new NotificationChannelRegistry();
registry.register(new EmailNotificationChannel());
registry.register(new SmsNotificationChannel());
registry.register(new WhatsappNotificationChannel());
registry.register(new PushNotificationChannel());
```

The factory only does:

```java
public NotificationChannel getChannel(NotificationChannelType channelType) {
    return registry.get(channelType);
}
```

This is cleaner because the factory no longer needs a direct switch.


---

# 7. Main Objective

Build a notification dispatch system using a **Registry-based Factory Pattern**.

The system should:

```text
1. Accept a NotificationRequest.
2. Validate basic request fields.
3. Fetch tenant notification configuration.
4. Try the preferred channel first.
5. Try fallback channels if preferred channel is unavailable.
6. Skip channels disabled for the tenant.
7. Skip channels that do not support request priority.
8. Skip channels that do not have required recipient details.
9. Fetch selected channel using NotificationChannelFactory.
10. Send notification using selected NotificationChannel.
11. Return a structured NotificationResult.
12. Return a failed NotificationResult if no channel can be used.
```

---

# 8. Required Classes

Implement these classes:

```text
NotificationChannelType
NotificationPriority
NotificationRequest
NotificationResult
NotificationChannel
EmailNotificationChannel
SmsNotificationChannel
WhatsappNotificationChannel
PushNotificationChannel
NotificationChannelRegistry
NotificationChannelFactory
TenantNotificationConfig
TenantNotificationConfigService
NotificationRequestValidator
NotificationRoutingService
NotificationDispatchService
Main
```

---

# 9. Class Design Overview

```text
NotificationChannel
    ├── EmailNotificationChannel
    ├── SmsNotificationChannel
    ├── WhatsappNotificationChannel
    └── PushNotificationChannel

NotificationChannelRegistry
    └── Stores available channels in EnumMap

NotificationChannelFactory
    └── Fetches channels from registry

TenantNotificationConfig
    └── Stores enabled channels for a tenant

TenantNotificationConfigService
    └── Returns tenant-specific notification config

NotificationRoutingService
    └── Applies preferred/fallback/tenant/priority/recipient rules

NotificationDispatchService
    └── Validates request and sends notification

NotificationRequest
    └── Represents notification input

NotificationResult
    └── Represents notification dispatch result
```

Runtime flow:

```text
Main
  -> NotificationDispatchService
      -> NotificationRequestValidator
      -> NotificationRoutingService
          -> TenantNotificationConfigService
          -> NotificationChannelFactory
              -> NotificationChannelRegistry
                  -> NotificationChannel
                      -> NotificationResult
```


---

# 10. Enums

## `NotificationChannelType`

```java
public enum NotificationChannelType {
    EMAIL,
    SMS,
    WHATSAPP,
    PUSH
}
```

## `NotificationPriority`

```java
public enum NotificationPriority {
    LOW,
    NORMAL,
    HIGH,
    CRITICAL
}
```

---

# 11. Channel Priority Rules

Each channel supports different priorities.

```text
EMAIL    -> LOW, NORMAL, HIGH, CRITICAL
SMS      -> HIGH, CRITICAL
WHATSAPP -> NORMAL, HIGH
PUSH     -> LOW, NORMAL, HIGH
```

Important examples:

```text
CRITICAL notification can use EMAIL or SMS.
CRITICAL notification cannot use PUSH.
LOW notification can use EMAIL or PUSH.
LOW notification cannot use SMS.
NORMAL notification can use EMAIL, WHATSAPP, or PUSH.
```

These rules should be implemented inside each channel class using:

```java
Set<NotificationPriority> supportedPriorities();
```

and:

```java
boolean supportsPriority(NotificationPriority priority);
```

---

# 12. `NotificationRequest`

## Purpose

Represents one notification dispatch request.

## Required Fields

```text
notificationId
tenantId
userId
recipientEmail
recipientPhone
deviceToken
title
message
priority
preferredChannel
fallbackChannels
createdAt
```

## Suggested Code

```java
import java.time.LocalDateTime;
import java.util.List;

public class NotificationRequest {

    private final String notificationId;
    private final String tenantId;
    private final String userId;
    private final String recipientEmail;
    private final String recipientPhone;
    private final String deviceToken;
    private final String title;
    private final String message;
    private final NotificationPriority priority;
    private final NotificationChannelType preferredChannel;
    private final List<NotificationChannelType> fallbackChannels;
    private final LocalDateTime createdAt;

    public NotificationRequest(
            String notificationId,
            String tenantId,
            String userId,
            String recipientEmail,
            String recipientPhone,
            String deviceToken,
            String title,
            String message,
            NotificationPriority priority,
            NotificationChannelType preferredChannel,
            List<NotificationChannelType> fallbackChannels
    ) {
        if (fallbackChannels == null) {
            throw new IllegalArgumentException("Fallback channels cannot be null.");
        }

        if (fallbackChannels.contains(null)) {
            throw new IllegalArgumentException("Fallback channels cannot contain null values.");
        }

        this.notificationId = notificationId;
        this.tenantId = tenantId;
        this.userId = userId;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.deviceToken = deviceToken;
        this.title = title;
        this.message = message;
        this.priority = priority;
        this.preferredChannel = preferredChannel;
        this.fallbackChannels = List.copyOf(fallbackChannels);
        this.createdAt = LocalDateTime.now();
    }

    public String getNotificationId() { return notificationId; }
    public String getTenantId() { return tenantId; }
    public String getUserId() { return userId; }
    public String getRecipientEmail() { return recipientEmail; }
    public String getRecipientPhone() { return recipientPhone; }
    public String getDeviceToken() { return deviceToken; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public NotificationPriority getPriority() { return priority; }
    public NotificationChannelType getPreferredChannel() { return preferredChannel; }
    public List<NotificationChannelType> getFallbackChannels() { return fallbackChannels; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

Using `List.copyOf(fallbackChannels)` is good because it makes the list immutable.


---

# 13. `NotificationResult`

## Purpose

Represents the result of notification dispatch.

This class must be modeled carefully.

For success:

```text
sent = true
channelUsed = selected channel
message = success message
providerMessageId = generated provider ID
failureReason = null
```

For failure:

```text
sent = false
channelUsed = null
message = Notification dispatch failed
providerMessageId = null
failureReason = reason why dispatch failed
```

## Required Fields

```text
notificationId
tenantId
userId
channelUsed
sent
message
providerMessageId
attemptedChannels
failureReason
```

## Recommended Code

```java
import java.util.List;

public class NotificationResult {

    private final String notificationId;
    private final String tenantId;
    private final String userId;
    private final NotificationChannelType channelUsed;
    private final boolean sent;
    private final String message;
    private final String providerMessageId;
    private final List<NotificationChannelType> attemptedChannels;
    private final String failureReason;

    public NotificationResult(
            String notificationId,
            String tenantId,
            String userId,
            NotificationChannelType channelUsed,
            boolean sent,
            String message,
            String providerMessageId,
            List<NotificationChannelType> attemptedChannels,
            String failureReason
    ) {
        this.notificationId = notificationId;
        this.tenantId = tenantId;
        this.userId = userId;
        this.channelUsed = channelUsed;
        this.sent = sent;
        this.message = message;
        this.providerMessageId = providerMessageId;
        this.attemptedChannels = List.copyOf(attemptedChannels);
        this.failureReason = failureReason;
    }

    public String getNotificationId() { return notificationId; }
    public String getTenantId() { return tenantId; }
    public String getUserId() { return userId; }
    public NotificationChannelType getChannelUsed() { return channelUsed; }
    public boolean isSent() { return sent; }
    public String getMessage() { return message; }
    public String getProviderMessageId() { return providerMessageId; }
    public List<NotificationChannelType> getAttemptedChannels() { return attemptedChannels; }
    public String getFailureReason() { return failureReason; }
}
```

## Common Modeling Mistake

Wrong:

```text
Sent: true
Message: Your payment was successful.
Failure Reason: SMS notification sent successfully
```

Correct:

```text
Sent: true
Message: SMS notification sent successfully
Failure Reason: null
```

For failure, do not generate a provider message ID.

Correct failure output:

```text
Sent: false
Channel Used: null
Provider Message ID: null
Failure Reason: No suitable channel found
```


---

# 14. `NotificationChannel` Interface

## Purpose

Common interface for all notification channels.

## Required Code

```java
import java.util.List;
import java.util.Set;

public interface NotificationChannel {

    NotificationResult send(
            NotificationRequest request,
            List<NotificationChannelType> attemptedChannels
    );

    NotificationChannelType getChannelType();

    String getChannelName();

    Set<NotificationPriority> supportedPriorities();

    boolean supportsPriority(NotificationPriority priority);
}
```

Every channel should:

```text
send notification
return its channel type
return its name
define supported priorities
check whether a priority is supported
```

---

# 15. Concrete Channel Classes

## `EmailNotificationChannel`

Supports:

```text
LOW, NORMAL, HIGH, CRITICAL
```

```java
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EmailNotificationChannel implements NotificationChannel {

    @Override
    public NotificationResult send(
            NotificationRequest request,
            List<NotificationChannelType> attemptedChannels
    ) {
        return new NotificationResult(
                request.getNotificationId(),
                request.getTenantId(),
                request.getUserId(),
                getChannelType(),
                true,
                "Email notification sent successfully",
                "EMAIL-" + UUID.randomUUID(),
                attemptedChannels,
                null
        );
    }

    @Override
    public NotificationChannelType getChannelType() {
        return NotificationChannelType.EMAIL;
    }

    @Override
    public String getChannelName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Set<NotificationPriority> supportedPriorities() {
        return Set.of(
                NotificationPriority.LOW,
                NotificationPriority.NORMAL,
                NotificationPriority.HIGH,
                NotificationPriority.CRITICAL
        );
    }

    @Override
    public boolean supportsPriority(NotificationPriority priority) {
        return supportedPriorities().contains(priority);
    }
}
```

## `SmsNotificationChannel`

Supports:

```text
HIGH, CRITICAL
```

```java
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SmsNotificationChannel implements NotificationChannel {

    @Override
    public NotificationResult send(
            NotificationRequest request,
            List<NotificationChannelType> attemptedChannels
    ) {
        return new NotificationResult(
                request.getNotificationId(),
                request.getTenantId(),
                request.getUserId(),
                getChannelType(),
                true,
                "SMS notification sent successfully",
                "SMS-" + UUID.randomUUID(),
                attemptedChannels,
                null
        );
    }

    @Override
    public NotificationChannelType getChannelType() {
        return NotificationChannelType.SMS;
    }

    @Override
    public String getChannelName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Set<NotificationPriority> supportedPriorities() {
        return Set.of(
                NotificationPriority.HIGH,
                NotificationPriority.CRITICAL
        );
    }

    @Override
    public boolean supportsPriority(NotificationPriority priority) {
        return supportedPriorities().contains(priority);
    }
}
```

## `WhatsappNotificationChannel`

Supports:

```text
NORMAL, HIGH
```

## `PushNotificationChannel`

Supports:

```text
LOW, NORMAL, HIGH
```

All concrete classes should return a `NotificationResult` with:

```text
sent = true
failureReason = null
providerMessageId = CHANNEL-<uuid>
```


---

# 16. `NotificationChannelRegistry`

## Purpose

Stores all registered notification channels.

This is the class that makes the factory registry-based.

## Required Code

```java
import java.util.EnumMap;
import java.util.Map;

public class NotificationChannelRegistry {

    private final Map<NotificationChannelType, NotificationChannel> channels =
            new EnumMap<>(NotificationChannelType.class);

    public NotificationChannelRegistry() {
        register(new EmailNotificationChannel());
        register(new SmsNotificationChannel());
        register(new WhatsappNotificationChannel());
        register(new PushNotificationChannel());
    }

    public void register(NotificationChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Notification channel cannot be null.");
        }

        channels.put(channel.getChannelType(), channel);
    }

    public NotificationChannel get(NotificationChannelType channelType) {
        if (channelType == null) {
            throw new IllegalArgumentException("Channel type cannot be null.");
        }

        NotificationChannel channel = channels.get(channelType);

        if (channel == null) {
            throw new IllegalArgumentException("No notification channel registered for " + channelType);
        }

        return channel;
    }

    public boolean isRegistered(NotificationChannelType channelType) {
        return channels.containsKey(channelType);
    }
}
```

`EnumMap` is efficient and clean when the key is an enum.

---

# 17. `NotificationChannelFactory`

## Purpose

Fetches channels using the registry.

This factory should not use switch.

## Required Code

```java
public class NotificationChannelFactory {

    private final NotificationChannelRegistry registry;

    public NotificationChannelFactory(NotificationChannelRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("Notification channel registry cannot be null.");
        }

        this.registry = registry;
    }

    public NotificationChannel getChannel(NotificationChannelType channelType) {
        return registry.get(channelType);
    }
}
```

Bad for Module 6:

```java
return switch (channelType) {
    case EMAIL -> new EmailNotificationChannel();
    case SMS -> new SmsNotificationChannel();
};
```

Good for Module 6:

```java
return registry.get(channelType);
```


---

# 18. Tenant Configuration

## `TenantNotificationConfig`

Stores which channels are enabled for a tenant.

```java
import java.util.Set;

public class TenantNotificationConfig {

    private final String tenantId;
    private final Set<NotificationChannelType> enabledChannels;

    public TenantNotificationConfig(
            String tenantId,
            Set<NotificationChannelType> enabledChannels
    ) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank.");
        }

        if (enabledChannels == null || enabledChannels.isEmpty()) {
            throw new IllegalArgumentException("Enabled channels cannot be null or empty.");
        }

        this.tenantId = tenantId;
        this.enabledChannels = Set.copyOf(enabledChannels);
    }

    public String getTenantId() {
        return tenantId;
    }

    public Set<NotificationChannelType> getEnabledChannels() {
        return enabledChannels;
    }

    public boolean isChannelEnabled(NotificationChannelType channelType) {
        return enabledChannels.contains(channelType);
    }
}
```

## `TenantNotificationConfigService`

Simulates fetching tenant notification configuration from DB or config service.

Required config:

```text
TENANT-A -> EMAIL, SMS, PUSH
TENANT-B -> EMAIL, WHATSAPP
TENANT-C -> PUSH
```

```java
import java.util.Map;
import java.util.Set;

public class TenantNotificationConfigService {

    private final Map<String, TenantNotificationConfig> configs =
            Map.of(
                    "TENANT-A",
                    new TenantNotificationConfig(
                            "TENANT-A",
                            Set.of(
                                    NotificationChannelType.EMAIL,
                                    NotificationChannelType.SMS,
                                    NotificationChannelType.PUSH
                            )
                    ),
                    "TENANT-B",
                    new TenantNotificationConfig(
                            "TENANT-B",
                            Set.of(
                                    NotificationChannelType.EMAIL,
                                    NotificationChannelType.WHATSAPP
                            )
                    ),
                    "TENANT-C",
                    new TenantNotificationConfig(
                            "TENANT-C",
                            Set.of(NotificationChannelType.PUSH)
                    )
            );

    public TenantNotificationConfig getConfig(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank.");
        }

        TenantNotificationConfig config = configs.get(tenantId);

        if (config == null) {
            throw new IllegalArgumentException("No notification config found for tenant: " + tenantId);
        }

        return config;
    }
}
```


---

# 19. `NotificationRequestValidator`

## Purpose

Validates the basic notification request fields.

## Required Validation

```text
notificationId cannot be null/blank
tenantId cannot be null/blank
userId cannot be null/blank
title cannot be null/blank
message cannot be null/blank
priority cannot be null
preferredChannel cannot be null
fallbackChannels cannot be null
fallbackChannels cannot contain null
```

## Required Code

```java
import java.util.Objects;

public class NotificationRequestValidator {

    private NotificationRequestValidator() {
        // Utility class
    }

    public static void validate(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Notification request cannot be null.");
        }

        if (request.getNotificationId() == null || request.getNotificationId().isBlank()) {
            throw new IllegalArgumentException("Notification ID cannot be null or blank.");
        }

        if (request.getTenantId() == null || request.getTenantId().isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank.");
        }

        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank.");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank.");
        }

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank.");
        }

        if (request.getPriority() == null) {
            throw new IllegalArgumentException("Priority cannot be null.");
        }

        if (request.getPreferredChannel() == null) {
            throw new IllegalArgumentException("Preferred channel cannot be null.");
        }

        if (request.getFallbackChannels() == null) {
            throw new IllegalArgumentException("Fallback channels cannot be null.");
        }

        if (request.getFallbackChannels().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Fallback channels cannot contain null values.");
        }
    }
}
```

---

# 20. Recipient Detail Rules

Recipient-detail validation should happen during routing because the selected channel determines which recipient field is required.

```text
EMAIL requires recipientEmail
SMS requires recipientPhone
WHATSAPP requires recipientPhone
PUSH requires deviceToken
```

Example:

```text
If preferred channel is PUSH but deviceToken is missing,
PUSH should be skipped and fallback channels should be tried.
```


---

# 21. `NotificationRoutingService`

## Purpose

This is the main decision-making service of Module 6.

It decides which notification channel should actually be used.

## Responsibilities

```text
1. Fetch tenant config.
2. Build candidate list: preferred channel first, fallback channels next.
3. Track attempted channels.
4. Skip disabled channels.
5. Skip unsupported priority channels.
6. Skip channels without required recipient details.
7. Return first valid channel.
8. Throw error if no suitable channel is found.
```

## Required Code

```java
import java.util.ArrayList;
import java.util.List;

public class NotificationRoutingService {

    private final NotificationChannelFactory channelFactory;
    private final TenantNotificationConfigService configService;

    public NotificationRoutingService(
            NotificationChannelFactory channelFactory,
            TenantNotificationConfigService configService
    ) {
        if (channelFactory == null) {
            throw new IllegalArgumentException("NotificationChannelFactory cannot be null.");
        }

        if (configService == null) {
            throw new IllegalArgumentException("TenantNotificationConfigService cannot be null.");
        }

        this.channelFactory = channelFactory;
        this.configService = configService;
    }

    public NotificationChannel resolveChannel(
            NotificationRequest request,
            List<NotificationChannelType> attemptedChannels
    ) {
        TenantNotificationConfig config =
                configService.getConfig(request.getTenantId());

        List<NotificationChannelType> candidates = new ArrayList<>();
        candidates.add(request.getPreferredChannel());
        candidates.addAll(request.getFallbackChannels());

        for (NotificationChannelType channelType : candidates) {
            attemptedChannels.add(channelType);

            if (!config.isChannelEnabled(channelType)) {
                continue;
            }

            NotificationChannel channel =
                    channelFactory.getChannel(channelType);

            if (!channel.supportsPriority(request.getPriority())) {
                continue;
            }

            if (!hasRequiredRecipientDetails(request, channelType)) {
                continue;
            }

            return channel;
        }

        throw new IllegalArgumentException(
                "No suitable notification channel found for request: "
                        + request.getNotificationId()
        );
    }

    private boolean hasRequiredRecipientDetails(
            NotificationRequest request,
            NotificationChannelType channelType
    ) {
        return switch (channelType) {
            case EMAIL -> request.getRecipientEmail() != null
                    && !request.getRecipientEmail().isBlank();

            case SMS, WHATSAPP -> request.getRecipientPhone() != null
                    && !request.getRecipientPhone().isBlank();

            case PUSH -> request.getDeviceToken() != null
                    && !request.getDeviceToken().isBlank();
        };
    }
}
```

---

# 22. `NotificationDispatchService`

## Purpose

Coordinates the full dispatch process.

## Responsibilities

```text
1. Validate request.
2. Create attempted channel list.
3. Ask routing service to resolve channel.
4. Send notification using selected channel.
5. Return success result.
6. Return failure result if routing fails.
```

## Required Code

```java
import java.util.ArrayList;
import java.util.List;

public class NotificationDispatchService {

    private final NotificationRoutingService routingService;

    public NotificationDispatchService(NotificationRoutingService routingService) {
        if (routingService == null) {
            throw new IllegalArgumentException("NotificationRoutingService cannot be null.");
        }

        this.routingService = routingService;
    }

    public NotificationResult dispatch(NotificationRequest request) {
        NotificationRequestValidator.validate(request);

        List<NotificationChannelType> attemptedChannels = new ArrayList<>();

        try {
            NotificationChannel channel =
                    routingService.resolveChannel(request, attemptedChannels);

            return channel.send(request, attemptedChannels);

        } catch (IllegalArgumentException exception) {
            return new NotificationResult(
                    request.getNotificationId(),
                    request.getTenantId(),
                    request.getUserId(),
                    null,
                    false,
                    "Notification dispatch failed",
                    null,
                    attemptedChannels,
                    exception.getMessage()
            );
        }
    }
}
```

For failed dispatch:

```text
channelUsed = null
providerMessageId = null
failureReason = actual reason
sent = false
```


---

# 23. Expected Main

Your `Main` should test at least these scenarios:

```text
1. Direct success:
   TENANT-A, preferred SMS, priority CRITICAL -> SMS selected.

2. Fallback success:
   TENANT-B, preferred SMS, fallback EMAIL, priority CRITICAL
   -> SMS disabled, EMAIL selected.

3. Fallback success:
   TENANT-C, preferred EMAIL, fallback PUSH, priority LOW
   -> EMAIL disabled, PUSH selected.

4. Failure:
   TENANT-C, preferred PUSH, priority CRITICAL
   -> PUSH enabled but does not support CRITICAL, no fallback, dispatch fails.

5. Optional:
   Missing recipient detail scenario.
```

## Required Code

```java
import java.util.List;

public class Main {

    public static void main(String[] args) {
        NotificationChannelRegistry registry =
                new NotificationChannelRegistry();

        NotificationChannelFactory factory =
                new NotificationChannelFactory(registry);

        TenantNotificationConfigService configService =
                new TenantNotificationConfigService();

        NotificationRoutingService routingService =
                new NotificationRoutingService(factory, configService);

        NotificationDispatchService dispatchService =
                new NotificationDispatchService(routingService);

        NotificationRequest criticalSmsRequest = new NotificationRequest(
                "NOTIF-101",
                "TENANT-A",
                "USER-101",
                "user101@example.com",
                "9876543210",
                "device-token-101",
                "Payment Alert",
                "Your payment was successful.",
                NotificationPriority.CRITICAL,
                NotificationChannelType.SMS,
                List.of(NotificationChannelType.EMAIL, NotificationChannelType.PUSH)
        );

        printResult(dispatchService.dispatch(criticalSmsRequest));

        NotificationRequest fallbackToEmailRequest = new NotificationRequest(
                "NOTIF-102",
                "TENANT-B",
                "USER-102",
                "user102@example.com",
                "9876543211",
                "device-token-102",
                "Security Alert",
                "New login detected.",
                NotificationPriority.CRITICAL,
                NotificationChannelType.SMS,
                List.of(NotificationChannelType.EMAIL)
        );

        printResult(dispatchService.dispatch(fallbackToEmailRequest));

        NotificationRequest fallbackToPushRequest = new NotificationRequest(
                "NOTIF-103",
                "TENANT-C",
                "USER-103",
                "user103@example.com",
                "9876543212",
                "device-token-103",
                "Offer Alert",
                "New offer available.",
                NotificationPriority.LOW,
                NotificationChannelType.EMAIL,
                List.of(NotificationChannelType.PUSH)
        );

        printResult(dispatchService.dispatch(fallbackToPushRequest));

        NotificationRequest failureRequest = new NotificationRequest(
                "NOTIF-104",
                "TENANT-C",
                "USER-104",
                "user104@example.com",
                "9876543213",
                "device-token-104",
                "Critical Alert",
                "Suspicious activity detected.",
                NotificationPriority.CRITICAL,
                NotificationChannelType.PUSH,
                List.of()
        );

        printResult(dispatchService.dispatch(failureRequest));
    }

    private static void printResult(NotificationResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Notification ID: " + result.getNotificationId());
        System.out.println("Tenant ID: " + result.getTenantId());
        System.out.println("User ID: " + result.getUserId());
        System.out.println("Sent: " + result.isSent());
        System.out.println("Channel Used: " + result.getChannelUsed());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Provider Message ID: " + result.getProviderMessageId());
        System.out.println("Attempted Channels: " + result.getAttemptedChannels());
        System.out.println("Failure Reason: " + result.getFailureReason());
        System.out.println("==================================================");
    }
}
```


---

# 24. Expected Output Style

## Direct Success

```text
==================================================
Notification ID: NOTIF-101
Tenant ID: TENANT-A
User ID: USER-101
Sent: true
Channel Used: SMS
Message: SMS notification sent successfully
Provider Message ID: SMS-<uuid>
Attempted Channels: [SMS]
Failure Reason: null
==================================================
```

## Fallback to Email

```text
==================================================
Notification ID: NOTIF-102
Tenant ID: TENANT-B
User ID: USER-102
Sent: true
Channel Used: EMAIL
Message: Email notification sent successfully
Provider Message ID: EMAIL-<uuid>
Attempted Channels: [SMS, EMAIL]
Failure Reason: null
==================================================
```

## Fallback to Push

```text
==================================================
Notification ID: NOTIF-103
Tenant ID: TENANT-C
User ID: USER-103
Sent: true
Channel Used: PUSH
Message: Push notification sent successfully
Provider Message ID: PUSH-<uuid>
Attempted Channels: [EMAIL, PUSH]
Failure Reason: null
==================================================
```

## Failure

```text
==================================================
Notification ID: NOTIF-104
Tenant ID: TENANT-C
User ID: USER-104
Sent: false
Channel Used: null
Message: Notification dispatch failed
Provider Message ID: null
Attempted Channels: [PUSH]
Failure Reason: No suitable notification channel found for request: NOTIF-104
==================================================
```

---

# 25. Correct Execution Flow

For this call:

```java
dispatchService.dispatch(fallbackToEmailRequest);
```

Execution flow:

```text
1. Main creates NotificationRequest.
2. Main calls NotificationDispatchService.dispatch(request).
3. DispatchService validates request.
4. DispatchService creates attemptedChannels list.
5. DispatchService calls RoutingService.resolveChannel().
6. RoutingService gets tenant config for TENANT-B.
7. Candidate list becomes [SMS, EMAIL].
8. RoutingService attempts SMS.
9. SMS is not enabled for TENANT-B, so skip.
10. RoutingService attempts EMAIL.
11. EMAIL is enabled.
12. EMAIL supports CRITICAL.
13. EMAIL has recipientEmail.
14. RoutingService returns EmailNotificationChannel.
15. DispatchService calls channel.send().
16. EmailNotificationChannel returns success NotificationResult.
17. Main prints result.
```

---

# 26. What Makes This Factory Pattern?

The Factory Pattern exists because the system uses:

```java
NotificationChannelFactory
```

to obtain the correct `NotificationChannel`.

But in this module, the factory uses a registry:

```java
NotificationChannelRegistry
```

instead of switch.

Without Factory:

```java
NotificationChannel channel = new SmsNotificationChannel();
```

With Registry-Based Factory:

```java
NotificationChannel channel =
        notificationChannelFactory.getChannel(NotificationChannelType.SMS);
```

The caller does not directly create the concrete implementation.

The caller receives the interface:

```java
NotificationChannel
```

This keeps the caller decoupled from concrete classes.

---

# 27. Responsibility Separation

## NotificationChannelRegistry

```text
Stores available channel implementations.
```

## NotificationChannelFactory

```text
Fetches a channel by channel type.
```

## TenantNotificationConfigService

```text
Provides tenant-specific enabled channels.
```

## NotificationRoutingService

```text
Applies routing rules:
preferred channel
fallback channels
tenant enabled channel check
priority support check
recipient detail check
```

## NotificationDispatchService

```text
Validates request.
Calls routing service.
Calls selected channel.
Returns result.
```

## NotificationChannel implementations

```text
Send notification.
Return NotificationResult.
Define supported priorities.
```


---

# 28. Important Rules

## Rule 1: No switch inside factory

Wrong:

```java
return switch (channelType) {
    case EMAIL -> new EmailNotificationChannel();
    case SMS -> new SmsNotificationChannel();
};
```

Correct:

```java
return registry.get(channelType);
```

## Rule 2: Factory should not handle fallback

Fallback belongs to `NotificationRoutingService`.

## Rule 3: Dispatch service should not create channels

Wrong:

```java
NotificationChannel channel = new EmailNotificationChannel();
```

Correct:

```java
NotificationChannel channel =
        routingService.resolveChannel(request, attemptedChannels);
```

## Rule 4: Routing service should not create channels directly

Wrong:

```java
NotificationChannel channel = new SmsNotificationChannel();
```

Correct:

```java
NotificationChannel channel =
        channelFactory.getChannel(channelType);
```

## Rule 5: Channel must support priority

If request priority is `CRITICAL`, then:

```text
PUSH must be skipped
SMS can be used
EMAIL can be used
```

## Rule 6: Channel must be enabled for tenant

If tenant config says:

```text
TENANT-B -> EMAIL, WHATSAPP
```

then:

```text
SMS must be skipped
```

## Rule 7: Channel must have required recipient details

```text
EMAIL    -> recipientEmail
SMS      -> recipientPhone
WHATSAPP -> recipientPhone
PUSH     -> deviceToken
```

## Rule 8: Failed result should not look successful

For failure:

```text
sent = false
channelUsed = null
providerMessageId = null
message = Notification dispatch failed
failureReason = actual reason
```

---

# 29. Common Mistakes

```text
1. Using switch inside NotificationChannelFactory.
2. Creating channels directly inside NotificationRoutingService.
3. Not using NotificationChannelRegistry.
4. Not using EnumMap for enum-based registry.
5. Not testing fallback scenarios.
6. Not testing failed routing.
7. Ignoring tenant enabled channels.
8. Ignoring priority support.
9. Ignoring required recipient details.
10. Returning Object instead of NotificationChannel.
11. Letting factory dispatch/send notification.
12. Putting too much logic inside Main.
13. Not defensively copying fallbackChannels.
14. Putting success message inside failureReason.
15. Generating providerMessageId for failed dispatch.
16. Setting channelUsed on failed dispatch.
17. Catching broad Exception instead of expected IllegalArgumentException.
18. Missing HIGH support in SMS channel.
```

---

# 30. Scoring Rubric

| Area | Marks |
|---|---:|
| `NotificationChannelType` enum | 0.7 |
| `NotificationPriority` enum | 0.7 |
| `NotificationRequest` | 1 |
| `NotificationResult` | 1 |
| `NotificationChannel` interface | 1 |
| All 4 concrete channels | 1.5 |
| Channel supported-priority logic | 1 |
| `NotificationChannelRegistry` | 1.5 |
| Registry-based `NotificationChannelFactory` | 1.5 |
| `TenantNotificationConfig` | 1 |
| `TenantNotificationConfigService` | 1 |
| `NotificationRequestValidator` | 1 |
| `NotificationRoutingService` fallback logic | 2 |
| `NotificationDispatchService` | 1 |
| Main direct success scenario | 1 |
| Main fallback scenarios | 1 |
| Main failure scenario | 1 |
| Correct result semantics | 1 |
| Code readability and naming | 1 |

Total: **21.9 marks normalized to 10**

---

# 31. Ideal Final Code Structure

```text
factory/module6/
    NotificationChannelType.java
    NotificationPriority.java
    NotificationRequest.java
    NotificationResult.java
    NotificationChannel.java
    EmailNotificationChannel.java
    SmsNotificationChannel.java
    WhatsappNotificationChannel.java
    PushNotificationChannel.java
    NotificationChannelRegistry.java
    NotificationChannelFactory.java
    TenantNotificationConfig.java
    TenantNotificationConfigService.java
    NotificationRequestValidator.java
    NotificationRoutingService.java
    NotificationDispatchService.java
    Main.java
```

Optional future improvements:

```text
ProviderMessageIdGenerator.java
NotificationSendException.java
NotificationAuditLog.java
NotificationAttempt.java
```

---

# 32. Difference Between Module 5 and Module 6

Module 5:

```text
DeliveryRequest -> LogisticsPartnerFactory -> DeliveryPartner
```

The factory selected implementation using business rules.

Module 6:

```text
NotificationRequest -> RoutingService -> Factory -> Registry -> Channel
```

The factory is registry-based.

Module 6 is more advanced because it adds:

```text
registry/map-based factory
tenant-specific config
preferred channel
fallback channels
priority support
recipient-detail checks
attempted channel tracking
failure result modeling
separation between routing and factory
```

---

# 33. Final Learning Goal

By completing Module 6, you should understand:

```text
1. Factory Pattern does not always need switch.
2. A factory can use registry/map lookup.
3. Registry-based factories are more extensible.
4. Factory and routing service should have separate responsibilities.
5. Fallback logic should not be placed inside the factory.
6. Tenant config can influence implementation selection.
7. Priority support can influence channel selection.
8. Recipient details can influence routing.
9. Result objects must model success and failure correctly.
10. Real backend dispatch systems use layered decision-making.
```

The key mental model is:

```text
NotificationRequest comes in.
DispatchService validates it.
RoutingService decides the best channel.
Factory fetches channel from Registry.
Channel sends notification.
NotificationResult is returned.
```

For this module:

```text
EMAIL    -> EmailNotificationChannel
SMS      -> SmsNotificationChannel
WHATSAPP -> WhatsappNotificationChannel
PUSH     -> PushNotificationChannel
```

But the selected channel also depends on:

```text
tenant enabled channels
priority support
recipient details
preferred channel
fallback channels
```

That is Factory Pattern with a realistic, extensible notification dispatch backend.
