# Observer Design Pattern Practice — Module 7

## Queue-Based Event Bus with Idempotency, Replay, Metrics, and Dead Letter Queue

### Difficulty Level

**Final Boss / Backend-style Advanced**

This is the final practice module for the **Observer Design Pattern**.

In earlier modules, observers were notified directly when something happened. In this final module, you will build a more backend-style design where events are first stored in a queue and processed later.

The key idea is:

```text
publish(event)
        ↓
add event to queue
        ↓
processNext() / processAll()
        ↓
notify observers
```

This is similar to the way real backend systems use queues, event buses, and message brokers. However, for this module, use **plain Java only**.

Do not use:

```text
Spring Boot
Kafka
RabbitMQ
Thread
ExecutorService
CompletableFuture
```

This module is fully synchronous and in-memory.

---

## 1. Problem Statement

You are building an in-memory queue-based event bus for an order management system.

Different order events can happen:

```text
ORDER_PLACED
PAYMENT_SUCCESS
ORDER_SHIPPED
ORDER_CANCELLED
```

Observers can subscribe to specific event types.

For this module, the main event you will test is:

```text
PAYMENT_SUCCESS
```

When a payment succeeds, multiple observers may need to react:

```text
AuditLogObserver
InvoiceObserver
LoyaltyPointsObserver
HighValueOrderObserver
FailingObserver
```

But unlike previous modules, the event should not be delivered immediately when published.

Instead, your event bus should:

```text
1. Accept the event
2. Store it in an internal queue
3. Process queued events later
4. Notify observers in priority order
5. Apply observer filters
6. Retry failed observers
7. Store permanently failed observer executions in a dead-letter queue
8. Allow dead-letter events to be replayed
9. Prevent duplicate successful observer processing using idempotency
10. Track metrics
11. Return dispatch result summaries
```

---

## 2. Real Backend Analogy

Imagine a payment succeeds in an e-commerce system.

The order service creates this event:

```text
PAYMENT_SUCCESS
Order: ORD-1
Customer: CUST-101
Amount: 2500.0
```

In a real backend system, the order service may not directly call invoice, loyalty, audit, and notification services.

Instead, it publishes the event to a queue.

Then an event processor picks up the event and notifies interested listeners.

Example:

```text
Order service publishes PAYMENT_SUCCESS
        ↓
Event enters queue
        ↓
Event processor picks event
        ↓
Audit service records it
        ↓
Invoice service generates invoice
        ↓
Loyalty service adds reward points
        ↓
High-value observer checks if special monitoring is needed
        ↓
Failing observer fails and goes to DLQ
```

This module simulates that flow using plain Java.

---

## 3. Observer Pattern Mapping

| Observer Pattern Term | Meaning in Module 7 |
|---|---|
| Subject / Observable | `QueuedEventBus` |
| Concrete Subject | `InMemoryQueuedEventBus` |
| Observer | `OrderEventObserver` |
| Concrete Observers | `AuditLogObserver`, `InvoiceObserver`, `LoyaltyPointsObserver`, `HighValueOrderObserver`, `FailingObserver` |
| Event Object | `OrderEvent` |
| Event Type | `OrderEventType` |
| Observer Metadata | `ObserverRegistration` |
| Filter | `EventFilter` |
| Queue | Stores events waiting to be processed |
| Dead Letter Queue | Stores failed observer executions |
| Dispatch Result | Processing report for one event |
| Metrics | Counters for published, processed, failed, skipped events |

---

## 4. Key Difference from Module 6

### Module 6

In Module 6, publishing an event immediately processed it:

```java
DispatchResult result = dispatcher.publish(event);
```

That means:

```text
publish(event)
        ↓
notify observers immediately
```

### Module 7

In Module 7, publishing only adds the event to a queue:

```java
eventBus.publish(event);
```

Processing happens later:

```java
eventBus.processNext();
```

or:

```java
eventBus.processAll();
```

That means:

```text
publish(event)
        ↓
add event to queue
        ↓
process later
        ↓
notify observers
```

This separation between publishing and processing is the main learning of Module 7.

---

## 5. Required Event Type Enum

Create:

```java
public enum OrderEventType {
    ORDER_PLACED,
    PAYMENT_SUCCESS,
    ORDER_SHIPPED,
    ORDER_CANCELLED
}
```

### Purpose

This enum defines the possible event types in the order system.

For this module, the main tested event type is:

```java
OrderEventType.PAYMENT_SUCCESS
```

---

## 6. Required Order Status Enum

Create:

```java
public enum OrderStatus {
    CREATED,
    PLACED,
    PAID,
    SHIPPED,
    CANCELLED
}
```

### Purpose

This enum represents the lifecycle status of an order.

For a payment success event, the status transition is:

```text
PLACED -> PAID
```

---

## 7. Required Event Object: `OrderEvent`

Create:

```java
public class OrderEvent
```

Fields:

```java
private final String eventId;
private final OrderEventType eventType;
private final String orderId;
private final String customerId;
private final double amount;
private final OrderStatus oldStatus;
private final OrderStatus newStatus;
private final String message;
private final LocalDateTime eventTime;
```

### Constructor

```java
public OrderEvent(
        OrderEventType eventType,
        String orderId,
        String customerId,
        double amount,
        OrderStatus oldStatus,
        OrderStatus newStatus,
        String message
) {
    this.eventId = UUID.randomUUID().toString();
    this.eventType = eventType;
    this.orderId = orderId;
    this.customerId = customerId;
    this.amount = amount;
    this.oldStatus = oldStatus;
    this.newStatus = newStatus;
    this.message = message;
    this.eventTime = LocalDateTime.now();
}
```

Create getters for all fields.

---

## 8. Why `eventId` Is Important

`eventId` is required for idempotency.

Idempotency means:

```text
If the same event is processed again, the same observer should not successfully process it twice.
```

Example:

```text
eventId = EVT-1
observer = InvoiceObserver
```

Once `InvoiceObserver` successfully processes this event, store a key:

```text
EVT-1::InvoiceObserver
```

If the same event is replayed later, this observer should be skipped as:

```text
DUPLICATE_SKIPPED
```

This prevents duplicate work like:

```text
Invoice generated twice
Loyalty points added twice
Customer notified twice
Audit logged twice
```

---

## 9. Required Observer Interface: `OrderEventObserver`

Create:

```java
public interface OrderEventObserver {
    String getObserverName();

    void onEvent(OrderEvent event);
}
```

### Purpose

This is the Observer interface.

Every observer must provide:

```java
String getObserverName();
```

and:

```java
void onEvent(OrderEvent event);
```

The event bus calls:

```java
observer.onEvent(event);
```

when the observer should process an event.

---

## 10. Required Event Filter Interface

Create:

```java
public interface EventFilter {
    boolean shouldProcess(OrderEvent event);
}
```

### Purpose

This interface decides whether an observer should process a particular event.

Example:

```java
event -> true
```

means always process.

Example:

```java
event -> event.getAmount() > 10000
```

means process only high-value orders.

---

## 11. Why `EventFilter` Is Needed

Sometimes an observer subscribes to an event type, but does not want every event of that type.

Example:

```text
Event type: PAYMENT_SUCCESS
```

Events:

```text
ORD-1 amount 2500.0
ORD-2 amount 15000.0
```

`HighValueOrderObserver` should process only:

```text
ORD-2 amount 15000.0
```

So during registration, use:

```java
event -> event.getAmount() > 10000
```

For `ORD-1`, the observer is skipped.

For `ORD-2`, the observer runs.

---

## 12. Required Observer Registration Class

Create:

```java
public class ObserverRegistration
```

Fields:

```java
private final OrderEventObserver observer;
private final int priority;
private final int maxRetries;
private final EventFilter eventFilter;
```

Constructor:

```java
public ObserverRegistration(
        OrderEventObserver observer,
        int priority,
        int maxRetries,
        EventFilter eventFilter
)
```

Create getters for all fields.

---

## 13. Meaning of `ObserverRegistration`

`ObserverRegistration` stores an observer along with metadata.

| Field | Meaning |
|---|---|
| `observer` | Actual observer object |
| `priority` | Execution order. Lower number runs first |
| `maxRetries` | Number of retries after first failure |
| `eventFilter` | Condition that decides whether observer should process the event |

Example:

```java
new ObserverRegistration(auditObserver, 1, 0, event -> true)
```

Meaning:

```text
Observer: AuditLogObserver
Priority: 1
Retries: 0
Filter: always process
```

Example:

```java
new ObserverRegistration(highValueObserver, 4, 0, event -> event.getAmount() > 10000)
```

Meaning:

```text
Observer: HighValueOrderObserver
Priority: 4
Retries: 0
Filter: only high-value orders
```

---

## 14. Required Dispatch Status Enum

Create:

```java
public enum DispatchStatus {
    SUCCESS,
    FAILED,
    SKIPPED,
    DUPLICATE_SKIPPED
}
```

### Meaning

| Status | Meaning |
|---|---|
| `SUCCESS` | Observer processed the event successfully |
| `FAILED` | Observer failed after all attempts |
| `SKIPPED` | Observer filter did not match |
| `DUPLICATE_SKIPPED` | Observer had already successfully processed this event earlier |

---

## 15. Required Observer Dispatch Result

Create:

```java
public class ObserverDispatchResult
```

Fields:

```java
private final String observerName;
private final String eventId;
private final OrderEventType eventType;
private final DispatchStatus status;
private final int attempts;
private final String errorMessage;
```

Constructor:

```java
public ObserverDispatchResult(
        String observerName,
        String eventId,
        OrderEventType eventType,
        DispatchStatus status,
        int attempts,
        String errorMessage
)
```

Create getters.

### Purpose

This class stores the result of delivering one event to one observer.

Examples:

```text
InvoiceObserver SUCCESS attempts=1
HighValueOrderObserver SKIPPED attempts=0
FailingObserver FAILED attempts=3
AuditLogObserver DUPLICATE_SKIPPED attempts=0
```

---

## 16. Required Dispatch Result

Create:

```java
public class DispatchResult
```

Fields:

```java
private final OrderEvent event;
private final List<ObserverDispatchResult> observerResults;
```

Constructor:

```java
public DispatchResult(OrderEvent event, List<ObserverDispatchResult> observerResults)
```

Recommended:

```java
this.observerResults = new ArrayList<>(observerResults);
```

Getter should also return a copy:

```java
public List<ObserverDispatchResult> getObserverResults() {
    return new ArrayList<>(observerResults);
}
```

Add helper methods:

```java
public long getSuccessCount()
public long getFailedCount()
public long getSkippedCount()
public long getDuplicateSkippedCount()
```

### Purpose

This class is the final report for one processed event.

Example:

```text
PAYMENT_SUCCESS ORD-1

Success: 3
Failed: 1
Skipped: 1
Duplicate skipped: 0
```

---

## 17. Required Dead Letter Event

Create:

```java
public class DeadLetterEvent
```

Fields:

```java
private final String deadLetterId;
private final OrderEvent event;
private final String observerName;
private final String reason;
private final LocalDateTime failedAt;
private final int attempts;
private boolean replayed;
```

Constructor:

```java
public DeadLetterEvent(
        OrderEvent event,
        String observerName,
        String reason,
        int attempts
)
```

Set:

```java
this.deadLetterId = UUID.randomUUID().toString();
this.failedAt = LocalDateTime.now();
this.replayed = false;
```

Create getters.

Add:

```java
public void markReplayed() {
    this.replayed = true;
}
```

### Purpose

This stores failed observer executions.

Example:

```text
DeadLetterEvent:
deadLetterId = DLQ-123
event = PAYMENT_SUCCESS ORD-1
observerName = FailingObserver
reason = Intentional observer failure
attempts = 3
replayed = false
```

---

## 18. Required Metrics Class

Create:

```java
public class EventBusMetrics
```

Fields:

```java
private int publishedEvents;
private int processedEvents;
private int successfulObserverCalls;
private int failedObserverCalls;
private int skippedObserverCalls;
private int duplicateSkippedObserverCalls;
private int deadLetterCount;
```

Methods:

```java
public void incrementPublishedEvents()
public void incrementProcessedEvents()
public void incrementSuccessfulObserverCalls()
public void incrementFailedObserverCalls()
public void incrementSkippedObserverCalls()
public void incrementDuplicateSkippedObserverCalls()
public void incrementDeadLetterCount()
```

Create getters for all fields.

### Purpose

This class tracks what happened inside the event bus.

Example final metrics:

```text
Published events: 2
Processed events: 3
Successful observer calls: 7
Failed observer calls: 3
Skipped observer calls: 2
Duplicate skipped observer calls: 3
Dead-letter count: 3
```

---

## 19. Required Event Bus Interface

Create:

```java
public interface QueuedEventBus {
    void subscribe(OrderEventType eventType, ObserverRegistration registration);

    void unsubscribe(OrderEventType eventType, OrderEventObserver observer);

    void publish(OrderEvent event);

    DispatchResult processNext();

    List<DispatchResult> processAll();

    void replayDeadLetter(String deadLetterId);

    List<OrderEvent> getPendingEvents();

    List<OrderEvent> getEventHistory();

    List<DeadLetterEvent> getDeadLetterEvents();

    EventBusMetrics getMetrics();
}
```

---

## 20. Concrete Class: `InMemoryQueuedEventBus`

Create:

```java
public class InMemoryQueuedEventBus implements QueuedEventBus
```

Fields:

```java
private final EventBusMetrics metrics;
private final Queue<OrderEvent> eventQueue;
private final List<OrderEvent> eventHistory;
private final Set<String> processedObserverEventKeys;
private final List<DeadLetterEvent> deadLetterEvents;
private final Map<OrderEventType, List<ObserverRegistration>> observersByEventType;
```

### Suggested Constructor

```java
public InMemoryQueuedEventBus() {
    this.metrics = new EventBusMetrics();
    this.eventQueue = new ArrayDeque<>();
    this.eventHistory = new ArrayList<>();
    this.processedObserverEventKeys = new HashSet<>();
    this.deadLetterEvents = new ArrayList<>();
    this.observersByEventType = new EnumMap<>(OrderEventType.class);
}
```

---

## 21. Field Explanation

### `eventQueue`

Stores events waiting to be processed.

```text
publish(event) adds event here
processNext() removes event from here
```

### `eventHistory`

Stores all events that were published.

### `processedObserverEventKeys`

Stores successful observer executions.

Example:

```text
eventId::observerName
```

This is used for idempotency.

### `deadLetterEvents`

Stores failed observer executions.

### `observersByEventType`

Stores observers grouped by event type.

Example:

```text
PAYMENT_SUCCESS -> Audit, Invoice, Loyalty, HighValue, Failing
```

### `metrics`

Tracks counters for published events, processed events, skipped observers, failed observers, etc.

---

## 22. Required Behavior: `subscribe()`

Register an observer for a specific event type.

Example:

```java
eventBus.subscribe(
        OrderEventType.PAYMENT_SUCCESS,
        new ObserverRegistration(invoiceObserver, 2, 1, event -> true)
);
```

Expected output:

```text
InvoiceObserver subscribed to PAYMENT_SUCCESS with priority 2
```

### Duplicate Handling

If an observer with the same name is already subscribed to the same event type:

```text
InvoiceObserver is already subscribed to PAYMENT_SUCCESS
```

Recommended logic:

```java
List<ObserverRegistration> registrations =
        observersByEventType.computeIfAbsent(eventType, key -> new ArrayList<>());

boolean alreadySubscribed = registrations.stream()
        .anyMatch(existing ->
                existing.getObserver().getObserverName()
                        .equals(registration.getObserver().getObserverName())
        );

if (alreadySubscribed) {
    return;
}

registrations.add(registration);
```

---

## 23. Required Behavior: `unsubscribe()`

Remove an observer from a specific event type.

Important:

The map stores:

```java
List<ObserverRegistration>
```

not:

```java
List<OrderEventObserver>
```

So removal should be:

```java
registrations.removeIf(registration ->
        registration.getObserver().getObserverName().equals(observer.getObserverName())
);
```

If observer was not subscribed, print:

```text
ObserverName is not subscribed to EVENT_TYPE
```

---

## 24. Required Behavior: `publish()`

Publishing should **not notify observers immediately**.

It should:

```text
1. Validate event is not null
2. Add event to queue
3. Add event to history
4. Increment published event metric
5. Print log
```

Example:

```java
eventQueue.offer(event);
eventHistory.add(event);
metrics.incrementPublishedEvents();
```

Expected output:

```text
Event published to queue: PAYMENT_SUCCESS order ORD-1
```

---

## 25. Required Behavior: `processNext()`

This is the core method.

It should:

```text
1. Poll one event from queue
2. If queue is empty, return null
3. Increment processed event metric
4. Get observers for the event type
5. Sort observers by priority
6. For each observer:
   - check idempotency
   - check filter
   - retry on failure
   - record dispatch result
   - update metrics
   - create dead-letter event if failed
7. Return DispatchResult
```

---

## 26. Idempotency Logic

Before processing an observer, create a key:

```java
String key = event.getEventId() + "::" + observer.getObserverName();
```

If already processed:

```java
if (processedObserverEventKeys.contains(key)) {
    // add DUPLICATE_SKIPPED result
    // increment duplicate skipped metric
    // continue
}
```

### Why this matters

Suppose an event is replayed from DLQ.

During the first run:

```text
AuditLogObserver       SUCCESS
InvoiceObserver        SUCCESS
LoyaltyPointsObserver  SUCCESS
FailingObserver        FAILED
```

During replay, you do not want Audit, Invoice, and Loyalty to run again.

So they should become:

```text
DUPLICATE_SKIPPED
```

---

## 27. Filter Logic

After idempotency check, check filter:

```java
if (eventFilter != null && !eventFilter.shouldProcess(event)) {
    // add SKIPPED result
    // increment skipped metric
    // continue
}
```

Example:

```java
event -> event.getAmount() > 10000
```

For amount `2500`, skip.

For amount `15000`, process.

---

## 28. Retry Logic

If the observer should process the event, call:

```java
observer.onEvent(event);
```

Retry if it fails.

If:

```java
maxRetries = 2
```

then total attempts are:

```text
1 original attempt + 2 retries = 3 attempts
```

Pseudo-flow:

```text
Attempt 1
If fails, attempt 2
If fails, attempt 3
If still fails, mark FAILED and send to DLQ
```

---

## 29. Dead Letter Logic

If observer fails after all attempts:

```java
deadLetterEvents.add(new DeadLetterEvent(
        event,
        observer.getObserverName(),
        errorMessage,
        attempts
));
```

Also:

```java
metrics.incrementFailedObserverCalls();
metrics.incrementDeadLetterCount();
```

---

## 30. Required Behavior: `processAll()`

Process all queued events.

Example:

```java
List<DispatchResult> results = new ArrayList<>();

while (!eventQueue.isEmpty()) {
    DispatchResult result = processNext();

    if (result != null) {
        results.add(result);
    }
}

return results;
```

---

## 31. Required Behavior: `replayDeadLetter()`

Method:

```java
void replayDeadLetter(String deadLetterId)
```

Should:

```text
1. Validate deadLetterId
2. Find matching DeadLetterEvent
3. If not found, print message and return
4. If already replayed, print message and return
5. Get original OrderEvent
6. Add original event back to queue
7. Mark DLQ item as replayed
8. Print replay message
```

Important:

`replayDeadLetter()` should not directly call observers.

It should only do:

```java
eventQueue.offer(originalEvent);
```

Then `processNext()` should process it.

---

## 32. Why Replay Needs Idempotency

Suppose `FailingObserver` failed and event was sent to DLQ.

When you replay that event, the event bus will process the same event again.

Without idempotency, successful observers would run again:

```text
Invoice generated twice
Loyalty points added twice
Audit log duplicated
```

With idempotency:

```text
AuditLogObserver       DUPLICATE_SKIPPED
InvoiceObserver        DUPLICATE_SKIPPED
LoyaltyPointsObserver  DUPLICATE_SKIPPED
FailingObserver        tries again
```

This is correct.

---

## 33. Required Observers

Create the following observers.

### 33.1 AuditLogObserver

```java
public class AuditLogObserver implements OrderEventObserver {
    @Override
    public String getObserverName() {
        return "AuditLogObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        System.out.println("AUDIT: Event "
                + event.getEventType()
                + " recorded for order "
                + event.getOrderId());
    }
}
```

### 33.2 InvoiceObserver

```java
public class InvoiceObserver implements OrderEventObserver {
    @Override
    public String getObserverName() {
        return "InvoiceObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        System.out.println("INVOICE: Invoice generated for order "
                + event.getOrderId()
                + " amount "
                + event.getAmount());
    }
}
```

### 33.3 LoyaltyPointsObserver

```java
public class LoyaltyPointsObserver implements OrderEventObserver {
    @Override
    public String getObserverName() {
        return "LoyaltyPointsObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        System.out.println("LOYALTY: Reward points added for customer "
                + event.getCustomerId());
    }
}
```

### 33.4 HighValueOrderObserver

```java
public class HighValueOrderObserver implements OrderEventObserver {
    @Override
    public String getObserverName() {
        return "HighValueOrderObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        System.out.println("HIGH VALUE: Special monitoring for high value order "
                + event.getOrderId()
                + " amount "
                + event.getAmount());
    }
}
```

Use filter during registration:

```java
event -> event.getAmount() > 10000
```

### 33.5 FailingObserver

```java
public class FailingObserver implements OrderEventObserver {
    @Override
    public String getObserverName() {
        return "FailingObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        throw new RuntimeException("Intentional observer failure");
    }
}
```

---

## 34. Expected Main Class

```java
public class Main {
    public static void main(String[] args) {
        QueuedEventBus eventBus = new InMemoryQueuedEventBus();

        OrderEventObserver auditObserver = new AuditLogObserver();
        OrderEventObserver invoiceObserver = new InvoiceObserver();
        OrderEventObserver loyaltyObserver = new LoyaltyPointsObserver();
        OrderEventObserver highValueObserver = new HighValueOrderObserver();
        OrderEventObserver failingObserver = new FailingObserver();

        eventBus.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(auditObserver, 1, 0, event -> true)
        );

        eventBus.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(invoiceObserver, 2, 1, event -> true)
        );

        eventBus.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(loyaltyObserver, 3, 1, event -> true)
        );

        eventBus.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(highValueObserver, 4, 0, event -> event.getAmount() > 10000)
        );

        eventBus.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(failingObserver, 5, 2, event -> true)
        );

        OrderEvent normalPaymentEvent = new OrderEvent(
                OrderEventType.PAYMENT_SUCCESS,
                "ORD-1",
                "CUST-101",
                2500.0,
                OrderStatus.PLACED,
                OrderStatus.PAID,
                "Payment successful"
        );

        OrderEvent highValuePaymentEvent = new OrderEvent(
                OrderEventType.PAYMENT_SUCCESS,
                "ORD-2",
                "CUST-202",
                15000.0,
                OrderStatus.PLACED,
                OrderStatus.PAID,
                "High value payment successful"
        );

        eventBus.publish(normalPaymentEvent);
        eventBus.publish(highValuePaymentEvent);

        System.out.println("Pending events before processing: "
                + eventBus.getPendingEvents().size());

        List<DispatchResult> results = eventBus.processAll();

        for (DispatchResult result : results) {
            printDispatchResult(result);
        }

        System.out.println("Pending events after processing: "
                + eventBus.getPendingEvents().size());

        System.out.println("Event history count: "
                + eventBus.getEventHistory().size());

        System.out.println("Dead-letter count: "
                + eventBus.getDeadLetterEvents().size());

        DeadLetterEvent firstDeadLetter = eventBus.getDeadLetterEvents().get(0);

        eventBus.replayDeadLetter(firstDeadLetter.getDeadLetterId());

        System.out.println("Pending events after replay: "
                + eventBus.getPendingEvents().size());

        DispatchResult replayResult = eventBus.processNext();

        printDispatchResult(replayResult);

        EventBusMetrics metrics = eventBus.getMetrics();

        System.out.println("Published events: "
                + metrics.getPublishedEvents());

        System.out.println("Processed events: "
                + metrics.getProcessedEvents());

        System.out.println("Successful observer calls: "
                + metrics.getSuccessfulObserverCalls());

        System.out.println("Failed observer calls: "
                + metrics.getFailedObserverCalls());

        System.out.println("Skipped observer calls: "
                + metrics.getSkippedObserverCalls());

        System.out.println("Duplicate skipped observer calls: "
                + metrics.getDuplicateSkippedObserverCalls());

        System.out.println("Dead-letter count: "
                + metrics.getDeadLetterCount());
    }

    private static void printDispatchResult(DispatchResult result) {
        if (result == null) {
            System.out.println("No event processed.");
            return;
        }

        System.out.println("Dispatch summary for event: "
                + result.getEvent().getEventType()
                + " order "
                + result.getEvent().getOrderId());

        System.out.println("Success: " + result.getSuccessCount());
        System.out.println("Failed: " + result.getFailedCount());
        System.out.println("Skipped: " + result.getSkippedCount());
        System.out.println("Duplicate skipped: " + result.getDuplicateSkippedCount());
    }
}
```

---

## 35. Expected Output Flow

Expected starting flow:

```text
AuditLogObserver subscribed to PAYMENT_SUCCESS with priority 1
InvoiceObserver subscribed to PAYMENT_SUCCESS with priority 2
LoyaltyPointsObserver subscribed to PAYMENT_SUCCESS with priority 3
HighValueOrderObserver subscribed to PAYMENT_SUCCESS with priority 4
FailingObserver subscribed to PAYMENT_SUCCESS with priority 5

Event published to queue: PAYMENT_SUCCESS order ORD-1
Event published to queue: PAYMENT_SUCCESS order ORD-2

Pending events before processing: 2
```

---

## 36. Expected First Event Processing

Event:

```text
ORD-1
amount = 2500.0
```

Expected observer results:

| Observer | Result |
|---|---|
| `AuditLogObserver` | SUCCESS |
| `InvoiceObserver` | SUCCESS |
| `LoyaltyPointsObserver` | SUCCESS |
| `HighValueOrderObserver` | SKIPPED |
| `FailingObserver` | FAILED |

Summary:

```text
Success: 3
Failed: 1
Skipped: 1
Duplicate skipped: 0
```

---

## 37. Expected Second Event Processing

Event:

```text
ORD-2
amount = 15000.0
```

Expected observer results:

| Observer | Result |
|---|---|
| `AuditLogObserver` | SUCCESS |
| `InvoiceObserver` | SUCCESS |
| `LoyaltyPointsObserver` | SUCCESS |
| `HighValueOrderObserver` | SUCCESS |
| `FailingObserver` | FAILED |

Summary:

```text
Success: 4
Failed: 1
Skipped: 0
Duplicate skipped: 0
```

---

## 38. Expected After Processing Both Events

```text
Pending events after processing: 0
Event history count: 2
Dead-letter count: 2
```

There are two dead-letter events because `FailingObserver` failed for both events.

---

## 39. Expected Replay Behavior

Replay the first dead-letter event:

```java
DeadLetterEvent firstDeadLetter = eventBus.getDeadLetterEvents().get(0);
eventBus.replayDeadLetter(firstDeadLetter.getDeadLetterId());
```

Expected:

```text
Pending events after replay: 1
```

Then process replayed event:

```java
DispatchResult replayResult = eventBus.processNext();
```

For replay of `ORD-1`, expected observer results:

| Observer | Result |
|---|---|
| `AuditLogObserver` | DUPLICATE_SKIPPED |
| `InvoiceObserver` | DUPLICATE_SKIPPED |
| `LoyaltyPointsObserver` | DUPLICATE_SKIPPED |
| `HighValueOrderObserver` | SKIPPED |
| `FailingObserver` | FAILED |

Summary:

```text
Success: 0
Failed: 1
Skipped: 1
Duplicate skipped: 3
```

---

## 40. Expected Metrics

After publishing two events, processing both, replaying one DLQ event, and processing replay:

```text
Published events: 2
Processed events: 3
Successful observer calls: 7
Failed observer calls: 3
Skipped observer calls: 2
Duplicate skipped observer calls: 3
Dead-letter count: 3
```

### Explanation

Published events:

```text
ORD-1
ORD-2
```

So:

```text
Published events = 2
```

Processed events:

```text
ORD-1
ORD-2
Replay of ORD-1
```

So:

```text
Processed events = 3
```

Successful observer calls:

```text
ORD-1: Audit + Invoice + Loyalty = 3
ORD-2: Audit + Invoice + Loyalty + HighValue = 4
Replay ORD-1: 0
```

So:

```text
Successful observer calls = 7
```

Failed observer calls:

```text
FailingObserver failed for ORD-1
FailingObserver failed for ORD-2
FailingObserver failed for replayed ORD-1
```

So:

```text
Failed observer calls = 3
```

Skipped observer calls:

```text
HighValueOrderObserver skipped ORD-1
HighValueOrderObserver skipped replayed ORD-1
```

So:

```text
Skipped observer calls = 2
```

Duplicate skipped observer calls:

```text
AuditLogObserver duplicate skipped
InvoiceObserver duplicate skipped
LoyaltyPointsObserver duplicate skipped
```

So:

```text
Duplicate skipped observer calls = 3
```

Dead-letter count:

```text
ORD-1 FailingObserver failure
ORD-2 FailingObserver failure
Replay ORD-1 FailingObserver failure
```

So:

```text
Dead-letter count = 3
```

---

## 41. Important Rules

Use plain Java only.

Do not use:

```text
Spring Boot
Kafka
RabbitMQ
Thread
ExecutorService
CompletableFuture
```

Do not notify observers directly from `publish()`.

`publish()` should only add events to the queue.

Observer notification should happen in:

```java
processNext()
```

or:

```java
processAll()
```

---

## 42. Common Mistakes to Avoid

### Mistake 1: Processing observers inside `publish()`

Bad:

```java
public void publish(OrderEvent event) {
    observer.onEvent(event);
}
```

Correct:

```java
public void publish(OrderEvent event) {
    eventQueue.offer(event);
}
```

### Mistake 2: Not implementing `getPendingEvents()`

Bad:

```java
return List.of();
```

Correct:

```java
return new ArrayList<>(eventQueue);
```

### Mistake 3: Treating duplicate skip as failure

Bad:

```java
if (processedObserverEventKeys.contains(key)) {
    break;
}
```

This can accidentally fall into failure logic.

Correct:

```java
if (processedObserverEventKeys.contains(key)) {
    add DUPLICATE_SKIPPED result;
    continue;
}
```

### Mistake 4: Forgetting metrics

Do not only increment published events.

You must also increment:

```text
processed events
successful observer calls
failed observer calls
skipped observer calls
duplicate skipped observer calls
dead-letter count
```

### Mistake 5: Returning internal lists directly

Bad:

```java
return eventHistory;
```

Correct:

```java
return new ArrayList<>(eventHistory);
```

---

## 43. What This Module Tests

This module tests whether you understand:

```text
Observer Pattern
Queue-based event processing
Event bus design
Event-specific subscriptions
Observer metadata
Priority dispatch
Filter-based skipping
Retry logic
Dead-letter queue
DLQ replay
Idempotency
Metrics
Dispatch result reporting
Defensive copying
```

---

## 44. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct event model with `eventId` | 1 |
| Correct queue-based publish/process design | 1.5 |
| Correct observer registration and priority | 1 |
| Correct filter handling | 1 |
| Correct retry and DLQ handling | 1.5 |
| Correct idempotency using `eventId + observerName` | 1.5 |
| Correct dispatch result tracking | 1 |
| Correct metrics tracking | 1 |
| Correct replay dead-letter behavior | 1 |
| Code readability | 0.5 |

Total: **10 marks**

---

## 45. Key Learning

The key learning of Module 7 is:

```text
Observers do not always need to be notified immediately.
Events can be queued, processed later, retried, replayed, measured, and protected from duplicate processing.
```

The most important methods are:

```java
publish(event)
processNext()
processAll()
replayDeadLetter(deadLetterId)
```

The most important safety mechanism is:

```java
eventId + "::" + observerName
```

This prevents duplicate successful observer processing during replay.

This is still the Observer Design Pattern, but now it looks much closer to real backend event-driven architecture.

After this module, you are ready to start the Decorator Design Pattern.
