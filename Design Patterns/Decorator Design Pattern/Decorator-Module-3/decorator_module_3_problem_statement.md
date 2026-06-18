# Decorator Design Pattern Practice — Module 3

## Notification Sender Decorator

### Difficulty Level

**Intermediate**

This is the third practice module for the **Decorator Design Pattern**.

In Module 1, you practiced Decorator Pattern with a simple coffee add-on system.

In Module 2, you practiced Decorator Pattern with pizza toppings and multiple base components.

Now in Module 3, you will move closer to a backend-style use case.

You will build a notification sending system where the base sender only sends a message, and decorators add extra behavior such as:

```text
Logging
Encryption
Timestamp
Metrics
```

This module is more practical than the coffee and pizza examples because this kind of wrapping is common in backend systems.

---

## 1. Problem Statement

You are building a notification sending system.

The system has a basic notification sender that can send a message.

Example:

```text
Payment successful for order ORD-101
```

The basic sender should simply print/send the message.

But in real backend systems, notification sending often needs extra features:

```text
1. Logging before and after sending
2. Encrypting the message before sending
3. Adding timestamp to the message
4. Tracking how many notifications were sent
```

You should add these extra features using the **Decorator Design Pattern**.

You should not modify the base sender every time you add a new feature.

Instead, you should wrap the sender with decorators.

Example:

```java
NotificationSender sender = new BasicNotificationSender();

sender = new LoggingNotificationDecorator(sender);
sender = new EncryptionNotificationDecorator(sender);
sender = new TimestampNotificationDecorator(sender);
sender = new MetricsNotificationDecorator(sender, metrics);

sender.send("Payment successful for order ORD-101");
```

This creates a chain of wrappers.

---

## 2. Real Backend Analogy

Think of a backend service that sends notifications to users.

The core job is simple:

```text
Send notification
```

But production systems usually need more things around that core operation:

```text
Log request before sending
Encrypt sensitive message content
Add metadata such as timestamp
Track metrics
Retry failures
Validate input
Audit the action
```

If you keep adding these responsibilities directly inside `BasicNotificationSender`, that class becomes large and messy.

Bad design:

```java
public class BasicNotificationSender {
    public void send(String message) {
        // validate
        // log before
        // add timestamp
        // encrypt
        // send
        // increment metrics
        // log after
    }
}
```

This violates the Single Responsibility Principle.

Decorator Pattern solves this by keeping each responsibility in its own wrapper class.

---

## 3. Decorator Pattern Mapping

| Decorator Pattern Term | Meaning in This Module |
|---|---|
| Component | `NotificationSender` interface |
| Concrete Component | `BasicNotificationSender` |
| Base Decorator | `NotificationSenderDecorator` |
| Concrete Decorators | `LoggingNotificationDecorator`, `EncryptionNotificationDecorator`, `TimestampNotificationDecorator`, `MetricsNotificationDecorator` |
| Wrapped Object | The `NotificationSender` object inside each decorator |
| Operation | `send(String message)` |

---

## 4. Key Difference from Module 2

### Module 2

In Module 2, decorators changed:

```text
Description
Cost
```

Example:

```text
Pizza + Cheese + Olives
```

### Module 3

In Module 3, decorators change or add behavior around a method call.

The method is:

```java
send(String message)
```

Some decorators modify the message:

```text
Encryption
Timestamp
```

Some decorators add behavior around the method call:

```text
Logging
Metrics
```

This is closer to real backend code.

---

## 5. Required Classes

You need to create:

```text
NotificationSender
BasicNotificationSender
NotificationSenderDecorator
LoggingNotificationDecorator
EncryptionNotificationDecorator
TimestampNotificationDecorator
NotificationMetrics
MetricsNotificationDecorator
Main
```

---

## 6. Class 1 — `NotificationSender` Interface

Create:

```java
public interface NotificationSender {
    void send(String message);
}
```

### Purpose

This is the **Component interface**.

Both the base sender and all decorators must implement this interface.

Because all decorators are also `NotificationSender`, you can keep writing:

```java
NotificationSender sender = new BasicNotificationSender();

sender = new LoggingNotificationDecorator(sender);
sender = new EncryptionNotificationDecorator(sender);
```

The variable type stays the same:

```java
NotificationSender
```

But the behavior grows as more decorators wrap it.

---

## 7. Class 2 — `BasicNotificationSender`

Create:

```java
public class BasicNotificationSender implements NotificationSender {

    @Override
    public void send(String message) {
        System.out.println("Sending notification: " + message);
    }
}
```

### Purpose

This is the **Concrete Component**.

It is the original object.

It should only do the basic sending.

It should not know about:

```text
Logging
Encryption
Timestamp
Metrics
```

That is important.

The base class should remain simple.

---

## 8. Class 3 — `NotificationSenderDecorator`

Create:

```java
public abstract class NotificationSenderDecorator implements NotificationSender {

    protected final NotificationSender wrappedSender;

    protected NotificationSenderDecorator(NotificationSender wrappedSender) {
        if (wrappedSender == null) {
            throw new IllegalArgumentException("Wrapped sender cannot be null");
        }
        this.wrappedSender = wrappedSender;
    }

    @Override
    public void send(String message) {
        wrappedSender.send(message);
    }
}
```

### Purpose

This is the **Base Decorator**.

It wraps another `NotificationSender`.

This line is the core of the decorator:

```java
protected final NotificationSender wrappedSender;
```

It means:

```text
Every decorator contains another NotificationSender inside it.
```

Example:

```java
new LoggingNotificationDecorator(new BasicNotificationSender())
```

Means:

```text
LoggingNotificationDecorator wraps BasicNotificationSender
```

### Why is this class abstract?

Because `NotificationSenderDecorator` is not a real feature by itself.

You should not normally create:

```java
new NotificationSenderDecorator(...)
```

Real decorators are:

```text
LoggingNotificationDecorator
EncryptionNotificationDecorator
TimestampNotificationDecorator
MetricsNotificationDecorator
```

The base decorator only exists to hold common wrapping logic.

### Why is `wrappedSender` protected?

Because child decorators need to access the wrapped sender.

Example:

```java
wrappedSender.send(message);
```

But outside classes should not access it directly.

So this is good:

```java
protected final NotificationSender wrappedSender;
```

This means:

```text
Child decorators can use it.
Normal outside classes cannot directly touch it.
```

### Why is `wrappedSender` final?

Because once a decorator wraps a sender, the wrapped sender should not change later.

This prevents accidental reassignment.

---

## 9. Class 4 — `LoggingNotificationDecorator`

Create:

```java
public class LoggingNotificationDecorator extends NotificationSenderDecorator {

    public LoggingNotificationDecorator(NotificationSender wrappedSender) {
        super(wrappedSender);
    }

    @Override
    public void send(String message) {
        System.out.println("LOG: Before sending notification");
        wrappedSender.send(message);
        System.out.println("LOG: After sending notification");
    }
}
```

### Purpose

This decorator adds logging before and after notification sending.

It does not modify the message.

It only adds behavior around the original send call.

### Expected behavior

If message is:

```text
Payment successful for order ORD-101
```

Output should include:

```text
LOG: Before sending notification
Sending notification: Payment successful for order ORD-101
LOG: After sending notification
```

---

## 10. Class 5 — `EncryptionNotificationDecorator`

Create:

```java
public class EncryptionNotificationDecorator extends NotificationSenderDecorator {

    public EncryptionNotificationDecorator(NotificationSender wrappedSender) {
        super(wrappedSender);
    }

    @Override
    public void send(String message) {
        String encryptedMessage = encrypt(message);
        wrappedSender.send(encryptedMessage);
    }

    private String encrypt(String message) {
        return "[ENCRYPTED: " + message + "]";
    }
}
```

### Purpose

This decorator modifies the message before passing it to the next sender.

It simulates encryption.

You do not need real encryption.

Fake encryption is enough for this module.

Example:

```text
Payment successful for order ORD-101
```

becomes:

```text
[ENCRYPTED: Payment successful for order ORD-101]
```

---

## 11. Class 6 — `TimestampNotificationDecorator`

Create:

```java
import java.time.LocalDateTime;

public class TimestampNotificationDecorator extends NotificationSenderDecorator {

    public TimestampNotificationDecorator(NotificationSender wrappedSender) {
        super(wrappedSender);
    }

    @Override
    public void send(String message) {
        String timestampedMessage = addTimestamp(message);
        wrappedSender.send(timestampedMessage);
    }

    private String addTimestamp(String message) {
        return "[" + LocalDateTime.now() + "] " + message;
    }
}
```

### Purpose

This decorator adds a timestamp to the message.

Example:

```text
Payment successful for order ORD-101
```

becomes:

```text
[2026-06-17T15:30:10] Payment successful for order ORD-101
```

The exact timestamp will differ every time.

That is okay.

---

## 12. Class 7 — `NotificationMetrics`

Create:

```java
public class NotificationMetrics {

    private int sentCount;

    public void incrementSentCount() {
        sentCount++;
    }

    public int getSentCount() {
        return sentCount;
    }
}
```

### Purpose

This class stores notification metrics.

For this module, you only need one metric:

```text
sentCount
```

It should count how many notifications were successfully sent.

---

## 13. Class 8 — `MetricsNotificationDecorator`

Create:

```java
public class MetricsNotificationDecorator extends NotificationSenderDecorator {

    private final NotificationMetrics metrics;

    public MetricsNotificationDecorator(
            NotificationSender wrappedSender,
            NotificationMetrics metrics
    ) {
        super(wrappedSender);

        if (metrics == null) {
            throw new IllegalArgumentException("Metrics cannot be null");
        }

        this.metrics = metrics;
    }

    @Override
    public void send(String message) {
        wrappedSender.send(message);
        metrics.incrementSentCount();
    }
}
```

### Purpose

This decorator increments metrics after successful sending.

Important:

```java
wrappedSender.send(message);
metrics.incrementSentCount();
```

This means metrics increments only after the wrapped sender completes.

If sending throws an exception, metrics should not increment.

That is usually correct for successful-send metrics.

---

## 14. Class 9 — `Main`

Create:

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Module 3 — Notification Sender Decorator");

        NotificationMetrics metrics = new NotificationMetrics();

        NotificationSender sender = new BasicNotificationSender();

        sender = new LoggingNotificationDecorator(sender);
        sender = new EncryptionNotificationDecorator(sender);
        sender = new TimestampNotificationDecorator(sender);
        sender = new MetricsNotificationDecorator(sender, metrics);

        sender.send("Payment successful for order ORD-101");

        System.out.println("Total notifications sent: " + metrics.getSentCount());
    }
}
```

### Purpose

This class tests your decorator chain.

The key part is:

```java
NotificationSender sender = new BasicNotificationSender();

sender = new LoggingNotificationDecorator(sender);
sender = new EncryptionNotificationDecorator(sender);
sender = new TimestampNotificationDecorator(sender);
sender = new MetricsNotificationDecorator(sender, metrics);
```

This builds a chain of wrappers.

---

## 15. What Actually Happens Internally

This code:

```java
NotificationSender sender = new BasicNotificationSender();

sender = new LoggingNotificationDecorator(sender);
sender = new EncryptionNotificationDecorator(sender);
sender = new TimestampNotificationDecorator(sender);
sender = new MetricsNotificationDecorator(sender, metrics);
```

creates this structure:

```text
MetricsNotificationDecorator
    wraps TimestampNotificationDecorator
        wraps EncryptionNotificationDecorator
            wraps LoggingNotificationDecorator
                wraps BasicNotificationSender
```

When you call:

```java
sender.send("Payment successful for order ORD-101");
```

the call enters the outermost decorator first:

```text
MetricsNotificationDecorator
```

Then it flows inward:

```text
MetricsNotificationDecorator.send()
    -> TimestampNotificationDecorator.send()
        -> EncryptionNotificationDecorator.send()
            -> LoggingNotificationDecorator.send()
                -> BasicNotificationSender.send()
```

---

## 16. Expected Flow

Given this chain:

```java
sender = new LoggingNotificationDecorator(sender);
sender = new EncryptionNotificationDecorator(sender);
sender = new TimestampNotificationDecorator(sender);
sender = new MetricsNotificationDecorator(sender, metrics);
```

The call flow is:

```text
1. Metrics decorator receives original message
2. Timestamp decorator adds timestamp
3. Encryption decorator encrypts timestamped message
4. Logging decorator prints before log
5. Basic sender sends final message
6. Logging decorator prints after log
7. Metrics decorator increments sent count
```

Expected output style:

```text
Module 3 — Notification Sender Decorator
LOG: Before sending notification
Sending notification: [ENCRYPTED: [2026-06-17T15:30:10] Payment successful for order ORD-101]
LOG: After sending notification
Total notifications sent: 1
```

The timestamp value will differ.

---

## 17. Important Learning: Decorator Order Matters

This order:

```java
sender = new EncryptionNotificationDecorator(sender);
sender = new TimestampNotificationDecorator(sender);
```

is different from this order:

```java
sender = new TimestampNotificationDecorator(sender);
sender = new EncryptionNotificationDecorator(sender);
```

### Case 1

```java
sender = new EncryptionNotificationDecorator(sender);
sender = new TimestampNotificationDecorator(sender);
```

The final outermost decorator is `TimestampNotificationDecorator`.

So timestamp happens first.

Possible final message:

```text
[2026-06-17T15:30:10] [ENCRYPTED: Payment successful]
```

### Case 2

```java
sender = new TimestampNotificationDecorator(sender);
sender = new EncryptionNotificationDecorator(sender);
```

The final outermost decorator is `EncryptionNotificationDecorator`.

So encryption happens first.

Possible final message:

```text
[ENCRYPTED: [2026-06-17T15:30:10] Payment successful]
```

Both can be correct depending on desired behavior.

The important point:

```text
The last decorator assigned becomes the outermost wrapper.
```

---

## 18. Expected Output

Your exact timestamp will vary.

Example:

```text
Module 3 — Notification Sender Decorator
LOG: Before sending notification
Sending notification: [ENCRYPTED: [2026-06-17T15:30:10.123] Payment successful for order ORD-101]
LOG: After sending notification
Total notifications sent: 1
```

Another valid output depending on order:

```text
Module 3 — Notification Sender Decorator
LOG: Before sending notification
Sending notification: [2026-06-17T15:30:10.123] [ENCRYPTED: Payment successful for order ORD-101]
LOG: After sending notification
Total notifications sent: 1
```

Both are acceptable if the chain order explains it.

---

## 19. Important Rules

### Rule 1: Do Not Modify `BasicNotificationSender`

Bad:

```java
public class BasicNotificationSender implements NotificationSender {
    public void send(String message) {
        System.out.println("LOG: Before");
        String encrypted = encrypt(message);
        System.out.println("Sending notification: " + encrypted);
        metrics.incrementSentCount();
        System.out.println("LOG: After");
    }
}
```

Correct:

```text
Keep BasicNotificationSender simple.
Use decorators for extra behavior.
```

---

### Rule 2: Each Decorator Should Have One Responsibility

Good:

```text
LoggingNotificationDecorator only logs
EncryptionNotificationDecorator only encrypts
TimestampNotificationDecorator only adds timestamp
MetricsNotificationDecorator only updates metrics
```

Avoid putting all behavior in one decorator.

---

### Rule 3: Message-transforming Decorators Should Pass Modified Message Forward

Correct:

```java
String encryptedMessage = encrypt(message);
wrappedSender.send(encryptedMessage);
```

Wrong:

```java
String encryptedMessage = encrypt(message);
wrappedSender.send(message);
```

If you do this, encryption has no effect.

---

### Rule 4: Logging Decorator Should Call Wrapped Sender

Correct:

```java
System.out.println("LOG: Before sending notification");
wrappedSender.send(message);
System.out.println("LOG: After sending notification");
```

If you forget `wrappedSender.send(message)`, the notification will never actually be sent.

---

### Rule 5: Metrics Should Increment After Successful Send

Preferred:

```java
wrappedSender.send(message);
metrics.incrementSentCount();
```

This means only successful sends are counted.

---

### Rule 6: Base Decorator Should Be Abstract

Preferred:

```java
public abstract class NotificationSenderDecorator implements NotificationSender
```

Because it is not a real feature by itself.

---

### Rule 7: Wrapped Sender Should Be Final

Preferred:

```java
protected final NotificationSender wrappedSender;
```

This prevents accidental reassignment.

---

## 20. Common Mistakes to Avoid

### Mistake 1: Calling `this.send(message)` Inside Decorator

Bad:

```java
@Override
public void send(String message) {
    this.send(message);
}
```

This causes infinite recursion.

Correct:

```java
wrappedSender.send(message);
```

---

### Mistake 2: Forgetting to Delegate

Bad:

```java
@Override
public void send(String message) {
    System.out.println("LOG: Before sending notification");
    System.out.println("LOG: After sending notification");
}
```

The notification was never sent.

Correct:

```java
System.out.println("LOG: Before sending notification");
wrappedSender.send(message);
System.out.println("LOG: After sending notification");
```

---

### Mistake 3: Mixing Responsibilities

Bad:

```java
public class LoggingNotificationDecorator extends NotificationSenderDecorator {
    public void send(String message) {
        String encrypted = encrypt(message);
        System.out.println("LOG");
        metrics.incrementSentCount();
        wrappedSender.send(encrypted);
    }
}
```

This decorator is doing too many things.

Better:

```text
Separate decorators for logging, encryption, and metrics.
```

---

### Mistake 4: Not Understanding Order

If output is different from expected, check decorator order.

This:

```java
sender = new LoggingNotificationDecorator(sender);
sender = new EncryptionNotificationDecorator(sender);
sender = new TimestampNotificationDecorator(sender);
```

is different from:

```java
sender = new TimestampNotificationDecorator(sender);
sender = new EncryptionNotificationDecorator(sender);
sender = new LoggingNotificationDecorator(sender);
```

The final assigned decorator becomes the outermost wrapper.

---

### Mistake 5: Incrementing Metrics Before Sending

Less ideal:

```java
metrics.incrementSentCount();
wrappedSender.send(message);
```

If sending fails, metrics still increments.

Preferred:

```java
wrappedSender.send(message);
metrics.incrementSentCount();
```

---

## 21. What This Module Tests

This module checks whether you understand:

```text
Decorator Pattern in backend-style code
Component interface
Concrete component
Base decorator
Concrete decorators
Message transformation
Before/after behavior
Metrics tracking
Decorator order
Single Responsibility Principle
Open/Closed Principle
```

---

## 22. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `NotificationSender` interface | 1 |
| Correct `BasicNotificationSender` | 1 |
| Correct `NotificationSenderDecorator` | 1.5 |
| Correct `LoggingNotificationDecorator` | 1.5 |
| Correct `EncryptionNotificationDecorator` | 1.5 |
| Correct `TimestampNotificationDecorator` | 1 |
| Correct `NotificationMetrics` | 0.75 |
| Correct `MetricsNotificationDecorator` | 0.75 |
| Correct wrapping in `Main` | 0.7 |
| Code readability | 0.3 |

Total: **10 marks**

---

## 23. Key Learning

The key learning of Module 3 is:

```text
Decorator Pattern is not only for food examples.
It is very useful for backend-style pipelines.
```

You can keep the base behavior simple:

```java
new BasicNotificationSender()
```

Then add features layer by layer:

```java
new LoggingNotificationDecorator(sender)
new EncryptionNotificationDecorator(sender)
new TimestampNotificationDecorator(sender)
new MetricsNotificationDecorator(sender, metrics)
```

Each decorator:

```text
1. Implements the same interface
2. Wraps another object of that interface
3. Adds one extra behavior
4. Delegates to the wrapped object
```

This keeps the system flexible and easy to extend.

---

## 24. Final Mental Model

Remember:

```java
NotificationSender sender = new BasicNotificationSender();

sender = new LoggingNotificationDecorator(sender);
sender = new EncryptionNotificationDecorator(sender);
sender = new TimestampNotificationDecorator(sender);
sender = new MetricsNotificationDecorator(sender, metrics);
```

This means:

```text
MetricsNotificationDecorator(
    TimestampNotificationDecorator(
        EncryptionNotificationDecorator(
            LoggingNotificationDecorator(
                BasicNotificationSender
            )
        )
    )
)
```

Decorator Pattern is:

```text
Same interface
+ wrapped object
+ extra behavior
```

In simple words:

> Decorator Pattern lets you add backend features like logging, encryption, timestamp, and metrics without modifying the original sender class.
