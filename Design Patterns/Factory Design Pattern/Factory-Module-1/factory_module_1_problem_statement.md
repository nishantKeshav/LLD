# Factory Design Pattern Practice — Module 1

## Notification Sender Factory

### Difficulty Level

**Beginner**

### Pattern Focus

**Simple Factory Pattern**

### Backend Theme

**Notification channel selection**

---

# 1. Module Overview

This is **Module 1** of the Factory Design Pattern practice series.

In this module, you will build a simple backend-style notification system that can send messages through different channels:

```text
EMAIL
SMS
WHATSAPP
```

The main goal is to understand how the **Factory Pattern** helps move object creation logic out of the client code and into a dedicated factory class.

Instead of directly creating sender objects like this:

```java
NotificationSender sender = new EmailNotificationSender();
```

the client code should ask the factory for the correct sender:

```java
NotificationSender sender =
        NotificationSenderFactory.getInstance(NotificationType.EMAIL);
```

The factory will decide which concrete class to create.

---

# 2. Background: Why This Module Exists

In real backend applications, notification systems often support multiple channels.

For example:

```text
EMAIL     -> used for account verification, invoices, reports
SMS       -> used for OTPs, alerts, delivery updates
WHATSAPP  -> used for order updates, reminders, support messages
PUSH      -> used for mobile app notifications
```

Each channel has different implementation details.

For example:

```text
Email may need SMTP or an email provider like SendGrid/AWS SES.
SMS may need a telecom provider.
WhatsApp may need a WhatsApp Business API provider.
Push may need Firebase Cloud Messaging.
```

But from the service layer's point of view, the goal is simple:

```text
Send this message to this recipient.
```

The service should not worry about which concrete sender class should be created.

That is where Factory Pattern helps.

---

# 3. Core Factory Pattern Idea

Factory Pattern is used when:

```text
You have multiple classes implementing the same interface,
and you need to create the correct implementation based on input.
```

In this module:

```text
Input: NotificationType
Output: Correct NotificationSender implementation
```

Example:

```text
NotificationType.EMAIL     -> EmailNotificationSender
NotificationType.SMS       -> SmsNotificationSender
NotificationType.WHATSAPP  -> WhatsappNotificationSender
```

So the factory becomes the centralized place for object creation.

---

# 4. Problem Without Factory Pattern

Without Factory Pattern, your `Main` or `NotificationService` might look like this:

```java
NotificationSender sender;

if (notificationType == NotificationType.EMAIL) {
    sender = new EmailNotificationSender();
} else if (notificationType == NotificationType.SMS) {
    sender = new SmsNotificationSender();
} else if (notificationType == NotificationType.WHATSAPP) {
    sender = new WhatsappNotificationSender();
} else {
    throw new IllegalArgumentException("Unsupported notification type");
}

sender.send(recipient, message);
```

This works, but it creates problems:

```text
1. Object creation logic is mixed with business logic.
2. The client code knows about every concrete sender class.
3. If a new sender is added, multiple places may need changes.
4. The code becomes harder to maintain as channels increase.
5. Unit testing becomes messier because object creation is hardcoded.
```

---

# 5. Problem With Factory Pattern

With Factory Pattern, the client code becomes cleaner:

```java
NotificationSender sender =
        NotificationSenderFactory.getInstance(notificationType);

sender.send(recipient, message);
```

Now the selection logic is moved into:

```java
NotificationSenderFactory
```

The client only depends on:

```java
NotificationSender
```

not concrete classes like:

```java
EmailNotificationSender
SmsNotificationSender
WhatsappNotificationSender
```

---

# 6. Main Objective

The objective of this module is to build a notification sender system where:

```text
1. All notification senders follow a common interface.
2. Each notification type has its own sender implementation.
3. A factory class creates the correct sender based on NotificationType.
4. The client code does not directly instantiate concrete sender classes.
5. The system is easy to extend with more notification types later.
```

---

# 7. Required Classes

You need to implement the following classes/interfaces/enums:

```text
NotificationType
NotificationSender
EmailNotificationSender
SmsNotificationSender
WhatsappNotificationSender
NotificationSenderFactory
Main
```

---

# 8. Class Design Overview

## High-Level Structure

```text
NotificationSender
    ├── EmailNotificationSender
    ├── SmsNotificationSender
    └── WhatsappNotificationSender

NotificationSenderFactory
    └── Creates correct NotificationSender based on NotificationType

Main
    └── Uses factory to get sender and send messages
```

---

# 9. Class 1: `NotificationType`

## Purpose

`NotificationType` represents the type/channel of notification to be sent.

Use an enum instead of raw strings.

## Required Code

```java
public enum NotificationType {
    EMAIL,
    SMS,
    WHATSAPP
}
```

## Why enum is better than String

Bad:

```java
NotificationSenderFactory.getInstance("emial");
```

This can compile but fail at runtime because `"emial"` is a typo.

Better:

```java
NotificationSenderFactory.getInstance(NotificationType.EMAIL);
```

With enum, only valid values are allowed.

## Responsibility

```text
Define all supported notification channels.
```

---

# 10. Class 2: `NotificationSender`

## Purpose

This is the common interface for all notification senders.

Every sender must implement this interface.

## Required Code

```java
public interface NotificationSender {
    void send(String recipient, String message);
}
```

## Why interface is needed

The client code should not care about the exact sender class.

It should be able to write:

```java
NotificationSender sender = NotificationSenderFactory.getInstance(type);
sender.send(recipient, message);
```

The actual object may be:

```text
EmailNotificationSender
SmsNotificationSender
WhatsappNotificationSender
```

but the client treats all of them as:

```java
NotificationSender
```

## Responsibility

```text
Define a common contract for sending notifications.
```

---

# 11. Class 3: `EmailNotificationSender`

## Purpose

This class handles email notifications.

## Required Code

```java
import java.time.LocalDateTime;

public class EmailNotificationSender implements NotificationSender {

    @Override
    public void send(String recipient, String message) {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient is null or blank");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message is null or blank");
        }

        System.out.println("Sending email to "
                + recipient
                + ": "
                + message
                + " at "
                + LocalDateTime.now());
    }
}
```

## Responsibility

```text
Validate recipient and message.
Send email-style notification.
Print output showing email was sent.
```

## Example Output

```text
Sending email to amit@example.com: Welcome to the Factory Pattern module at 2026-06-18T12:56:16.307096200
```

---

# 12. Class 4: `SmsNotificationSender`

## Purpose

This class handles SMS notifications.

## Required Code

```java
import java.time.LocalDateTime;

public class SmsNotificationSender implements NotificationSender {

    @Override
    public void send(String recipient, String message) {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient is null or blank");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message is null or blank");
        }

        System.out.println("Sending SMS to "
                + recipient
                + ": "
                + message
                + " at "
                + LocalDateTime.now());
    }
}
```

## Responsibility

```text
Validate recipient and message.
Send SMS-style notification.
Print output showing SMS was sent.
```

## Example Output

```text
Sending SMS to 9876543210: Your OTP is 123456 at 2026-06-18T12:56:16.312632100
```

---

# 13. Class 5: `WhatsappNotificationSender`

## Purpose

This class handles WhatsApp notifications.

## Required Code

```java
import java.time.LocalDateTime;

public class WhatsappNotificationSender implements NotificationSender {

    @Override
    public void send(String recipient, String message) {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient is null or blank");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message is null or blank");
        }

        System.out.println("Sending WhatsApp Message to "
                + recipient
                + ": "
                + message
                + " at "
                + LocalDateTime.now());
    }
}
```

## Responsibility

```text
Validate recipient and message.
Send WhatsApp-style notification.
Print output showing WhatsApp message was sent.
```

## Example Output

```text
Sending WhatsApp Message to 9876543210: Your order has been shipped at 2026-06-18T12:56:16.314639800
```

---

# 14. Class 6: `NotificationSenderFactory`

## Purpose

This is the most important class in Module 1.

The factory creates the correct `NotificationSender` object based on the given `NotificationType`.

## Required Code

```java
public class NotificationSenderFactory {

    private NotificationSenderFactory() {
        /*
         * Utility class.
         * This class should not be instantiated.
         */
    }

    public static NotificationSender getInstance(NotificationType notificationType) {
        if (notificationType == null) {
            throw new IllegalArgumentException("NotificationType cannot be null");
        }

        return switch (notificationType) {
            case SMS -> new SmsNotificationSender();
            case EMAIL -> new EmailNotificationSender();
            case WHATSAPP -> new WhatsappNotificationSender();
        };
    }
}
```

## Responsibility

```text
Accept NotificationType.
Create correct NotificationSender implementation.
Return it as NotificationSender interface.
Centralize object creation logic.
```

## Mapping

| NotificationType | Returned Object |
|---|---|
| `EMAIL` | `EmailNotificationSender` |
| `SMS` | `SmsNotificationSender` |
| `WHATSAPP` | `WhatsappNotificationSender` |

---

# 15. Why the Factory Method Returns Interface Type

The factory returns:

```java
NotificationSender
```

not:

```java
EmailNotificationSender
```

This is important.

## Good

```java
public static NotificationSender getInstance(NotificationType notificationType)
```

## Bad

```java
public static Object getInstance(NotificationType notificationType)
```

or:

```java
public static EmailNotificationSender getInstance(NotificationType notificationType)
```

The caller should only know that it received a sender.

The caller should not care which exact sender it received.

---

# 16. Class 7: `Main`

## Purpose

`Main` should test all three notification types.

## Required Code

```java
public class Main {

    public static void main(String[] args) {
        System.out.println("Factory Module 1 — Notification Sender Factory");

        NotificationSender emailSender =
                NotificationSenderFactory.getInstance(NotificationType.EMAIL);
        emailSender.send("amit@example.com", "Welcome to the Factory Pattern module");

        NotificationSender smsSender =
                NotificationSenderFactory.getInstance(NotificationType.SMS);
        smsSender.send("9876543210", "Your OTP is 123456");

        NotificationSender whatsappSender =
                NotificationSenderFactory.getInstance(NotificationType.WHATSAPP);
        whatsappSender.send("9876543210", "Your order has been shipped");
    }
}
```

## Responsibility

```text
Call the factory.
Receive NotificationSender.
Call send().
Do not directly instantiate concrete sender classes.
```

---

# 17. Expected Output

Your exact timestamp will differ.

Expected output style:

```text
Factory Module 1 — Notification Sender Factory
Sending email to amit@example.com: Welcome to the Factory Pattern module at 2026-06-18T12:56:16.307096200
Sending SMS to 9876543210: Your OTP is 123456 at 2026-06-18T12:56:16.312632100
Sending WhatsApp Message to 9876543210: Your order has been shipped at 2026-06-18T12:56:16.314639800
```

---

# 18. Correct Execution Flow

For this line:

```java
NotificationSender emailSender =
        NotificationSenderFactory.getInstance(NotificationType.EMAIL);
```

Execution flow:

```text
1. Main calls NotificationSenderFactory.getInstance(EMAIL).
2. Factory receives NotificationType.EMAIL.
3. Factory switch matches EMAIL.
4. Factory creates new EmailNotificationSender().
5. Factory returns it as NotificationSender.
6. Main calls emailSender.send(...).
7. EmailNotificationSender.send(...) executes.
```

The same flow applies for SMS and WhatsApp.

---

# 19. What Makes This Factory Pattern?

The Factory Pattern exists in this module because object creation is moved into a separate factory class.

## Without Factory

```java
NotificationSender sender = new EmailNotificationSender();
```

The caller directly creates the concrete class.

## With Factory

```java
NotificationSender sender =
        NotificationSenderFactory.getInstance(NotificationType.EMAIL);
```

The caller asks the factory.

The factory creates the concrete class.

The caller only receives the interface.

This is the main Factory Pattern idea.

---

# 20. What Problem This Module Solves

This module solves:

```text
Concrete class creation spread across client code.
```

Instead of creating sender objects everywhere, creation is centralized.

## Before Factory

```text
Main knows about EmailNotificationSender.
Main knows about SmsNotificationSender.
Main knows about WhatsappNotificationSender.
```

## After Factory

```text
Main knows only NotificationSenderFactory.
Main knows only NotificationSender interface.
Factory knows concrete classes.
```

This is cleaner.

---

# 21. Design Benefits

## 21.1 Cleaner Client Code

Client code becomes:

```java
NotificationSender sender =
        NotificationSenderFactory.getInstance(type);

sender.send(recipient, message);
```

It does not need a long switch or if-else chain.

## 21.2 Centralized Object Creation

All creation logic lives in one place:

```java
NotificationSenderFactory
```

If object creation changes, update the factory.

## 21.3 Easy to Add New Types

Suppose later you add:

```java
PUSH
```

You would add:

```java
public class PushNotificationSender implements NotificationSender {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Sending push notification to " + recipient + ": " + message);
    }
}
```

Then update the enum:

```java
public enum NotificationType {
    EMAIL,
    SMS,
    WHATSAPP,
    PUSH
}
```

And update the factory:

```java
case PUSH -> new PushNotificationSender();
```

The rest of the code still works with:

```java
NotificationSender
```

---

# 22. Important Rule

The factory should create objects.

The sender classes should send messages.

Do not put sending logic inside the factory.

## Wrong

```java
public class NotificationSenderFactory {

    public static void send(NotificationType type, String recipient, String message) {
        if (type == NotificationType.EMAIL) {
            System.out.println("Sending email...");
        }
    }
}
```

## Correct

```java
NotificationSender sender =
        NotificationSenderFactory.getInstance(NotificationType.EMAIL);

sender.send(recipient, message);
```

Factory creates.

Sender sends.

---

# 23. Common Mistakes

## Mistake 1: Creating Concrete Objects Directly in Main

Wrong:

```java
NotificationSender sender = new EmailNotificationSender();
```

Correct:

```java
NotificationSender sender =
        NotificationSenderFactory.getInstance(NotificationType.EMAIL);
```

## Mistake 2: Factory Returning Object

Wrong:

```java
public static Object getInstance(NotificationType type)
```

Correct:

```java
public static NotificationSender getInstance(NotificationType type)
```

## Mistake 3: Using String Instead of Enum

Wrong:

```java
NotificationSenderFactory.getInstance("EMAIL");
```

Correct:

```java
NotificationSenderFactory.getInstance(NotificationType.EMAIL);
```

## Mistake 4: Missing Null Handling

Wrong:

```java
public static NotificationSender getInstance(NotificationType type) {
    return switch (type) {
        case EMAIL -> new EmailNotificationSender();
        case SMS -> new SmsNotificationSender();
        case WHATSAPP -> new WhatsappNotificationSender();
    };
}
```

Better:

```java
if (type == null) {
    throw new IllegalArgumentException("NotificationType cannot be null");
}
```

## Mistake 5: Unnecessary Default in Enum Switch

If all enum values are handled, avoid unnecessary `default`.

Better:

```java
return switch (notificationType) {
    case SMS -> new SmsNotificationSender();
    case EMAIL -> new EmailNotificationSender();
    case WHATSAPP -> new WhatsappNotificationSender();
};
```

Why?

If a new enum value is added later, the compiler can warn that the switch is incomplete.

## Mistake 6: Incorrect Main Method Signature

Use:

```java
public static void main(String[] args)
```

not:

```java
public static void main()
```

Some environments allow instance/main variations, but standard Java entry point is:

```java
public static void main(String[] args)
```

---

# 24. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `NotificationType` enum | 1 |
| Correct `NotificationSender` interface | 1.5 |
| Correct `EmailNotificationSender` implementation | 1 |
| Correct `SmsNotificationSender` implementation | 1 |
| Correct `WhatsappNotificationSender` implementation | 1 |
| Correct `NotificationSenderFactory` | 2 |
| Proper null/invalid type handling | 1 |
| Correct `Main` tests all senders | 1 |
| Code readability and naming | 0.5 |

Total: **10 marks**

---

# 25. Ideal Final Code Structure

Your package/project can look like this:

```text
factory/module1/
    NotificationType.java
    NotificationSender.java
    EmailNotificationSender.java
    SmsNotificationSender.java
    WhatsappNotificationSender.java
    NotificationSenderFactory.java
    Main.java
```

---

# 26. Final Learning Goal

By completing Module 1, you should understand:

```text
1. Why object creation should not always be done directly in Main or service classes.
2. How to create a simple factory.
3. How to select an implementation based on enum input.
4. Why the factory returns an interface type.
5. How Factory Pattern keeps client code cleaner.
```

The main mental model is:

```text
Input comes in.
Factory checks the input.
Factory creates the correct implementation.
Client uses the returned interface.
```

For this module:

```text
EMAIL     -> EmailNotificationSender
SMS       -> SmsNotificationSender
WHATSAPP  -> WhatsappNotificationSender
```

That is Factory Pattern in its simplest form.
