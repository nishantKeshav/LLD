# Observer Design Pattern Practice — Module 5

## Backend Order Event Dispatcher

### Difficulty Level

**Advanced**

This module is a backend-style Observer Design Pattern exercise.

In earlier modules, the subject directly stored observers and notified them. For example:

```text
Product -> stores observers -> notifies observers
```

In this module, we make the design more realistic for backend systems.

Instead of one business class directly managing observers, we introduce a central event dispatcher.

The flow becomes:

```text
OrderService publishes an event
        ↓
EventDispatcher receives the event
        ↓
EventDispatcher finds observers interested in that event type
        ↓
EventDispatcher notifies those observers
```

This is closer to how real backend systems work with event buses, message brokers, and domain events.

For this module, use plain Java and synchronous execution only.

Do not use Kafka, RabbitMQ, Spring Boot, threads, or async processing.

---

## 1. Problem Statement

You are building an order management system.

Different events can happen during the lifecycle of an order:

```text
ORDER_PLACED
PAYMENT_SUCCESS
ORDER_SHIPPED
ORDER_CANCELLED
```

Different backend components are interested in different order events.

| Event | Interested Components |
|---|---|
| `ORDER_PLACED` | Email service, Inventory service, Audit log |
| `PAYMENT_SUCCESS` | Invoice service, Loyalty points service, Audit log |
| `ORDER_SHIPPED` | SMS service, Audit log |
| `ORDER_CANCELLED` | Refund service, Inventory service, Audit log |

Instead of hardcoding all these actions inside `OrderService`, you will create an `EventDispatcher`.

The `OrderService` will only publish events.

The `EventDispatcher` will notify the correct observers.

This keeps the order service clean and allows new observers to be added without changing order-service logic.

---

## 2. Real-Life Analogy

Imagine a company office.

When an order is placed, the order department announces:

```text
Order ORD-1 has been placed.
```

Now different departments react:

```text
Email team sends an order confirmation.
Inventory team reserves stock.
Audit team records the event.
```

When payment succeeds:

```text
Invoice team generates invoice.
Loyalty team adds reward points.
Audit team records the event.
```

The order department does not manually call every department one by one.

It announces the event, and interested departments react.

That is the idea of this module.

---

## 3. Observer Pattern Mapping

| Observer Pattern Term | Meaning in This Module |
|---|---|
| Subject / Observable | `EventDispatcher` |
| Concrete Subject | `SimpleEventDispatcher` |
| Observer | `OrderEventObserver` |
| Concrete Observers | Email, Inventory, Invoice, Audit, Refund, SMS, Loyalty, etc. |
| Event Object | `OrderEvent` |
| Event Type | `OrderEventType` |
| Event Publisher | `OrderService` |
| Notify Method | `publish(event)` |
| Observer Callback | `observer.onEvent(event)` |

---

## 4. Key Difference from Previous Modules

### Module 4

In Module 4, the `Product` itself stored event-specific observers:

```java
Map<ProductEventType, List<ProductObserver>> observersByEventType;
```

The product was both:

```text
Business object + observable subject
```

### Module 5

In Module 5, the business service does not store observers directly.

Instead:

```text
OrderService publishes events
EventDispatcher stores observers
Observers react to events
```

This is more backend-realistic.

The main structure becomes:

```java
Map<OrderEventType, List<OrderEventObserver>> observersByEventType;
```

inside the dispatcher.

---

## 5. Required Event Types

Create an enum:

```java
public enum OrderEventType {
    ORDER_PLACED,
    PAYMENT_SUCCESS,
    ORDER_SHIPPED,
    ORDER_CANCELLED
}
```

### Purpose

This enum defines the kinds of events that can happen in the order system.

Using an enum is better than strings because it prevents spelling mistakes.

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

Expected normal transition:

```text
CREATED -> PLACED -> PAID -> SHIPPED
```

For cancellation:

```text
PLACED -> CANCELLED
PAID -> CANCELLED
```

In this module, simple validation is enough, but the improved version should track actual status transitions.

---

## 7. Required Event Object

Create a class:

```java
public class OrderEvent
```

Suggested fields:

```java
private final OrderEventType eventType;
private final String orderId;
private final String customerId;
private final double amount;
private final OrderStatus oldStatus;
private final OrderStatus newStatus;
private final String message;
private final LocalDateTime eventTime;
```

### Purpose

`OrderEvent` is the data object passed to observers.

Instead of passing many separate values like this:

```java
observer.onEvent(orderId, customerId, amount, oldStatus, newStatus);
```

you pass one object:

```java
observer.onEvent(orderEvent);
```

This is cleaner and closer to real backend event design.

### Example Event

```java
OrderEvent event = new OrderEvent(
        OrderEventType.ORDER_PLACED,
        "ORD-1",
        2500.0,
        "ORDER PLACED SUCCESSFULLY",
        "CUST-101",
        OrderStatus.CREATED,
        OrderStatus.PLACED
);
```

---

## 8. Required Observer Interface

Create:

```java
public interface OrderEventObserver {
    String getObserverName();

    void onEvent(OrderEvent event);
}
```

### Purpose

This is the Observer interface.

Every backend listener should implement this interface.

The important method is:

```java
void onEvent(OrderEvent event);
```

The dispatcher calls this method when an event is published.

Example:

```java
observer.onEvent(event);
```

---

## 9. Required Event Dispatcher Interface

Create:

```java
public interface EventDispatcher {
    void subscribe(OrderEventType eventType, OrderEventObserver observer);

    void unsubscribe(OrderEventType eventType, OrderEventObserver observer);

    void publish(OrderEvent event);

    List<OrderEvent> getEventHistory();
}
```

### Purpose

The dispatcher is the central subject/observable.

It allows observers to subscribe to event types.

It allows services to publish events.

It keeps event history.

---

## 10. Concrete Event Dispatcher

Create:

```java
public class SimpleEventDispatcher implements EventDispatcher
```

Suggested fields:

```java
private final Map<OrderEventType, List<OrderEventObserver>> observersByEventType;
private final List<OrderEvent> eventHistory;
```

Use:

```java
EnumMap<OrderEventType, List<OrderEventObserver>>
```

Example:

```java
private final Map<OrderEventType, List<OrderEventObserver>> observersByEventType =
        new EnumMap<>(OrderEventType.class);
```

### Why `EnumMap`?

Because the map key is an enum.

For enum keys, `EnumMap` is cleaner and efficient.

---

## 11. Dispatcher Behavior

### 11.1 `subscribe()`

Registers an observer for a specific event type.

Example:

```java
dispatcher.subscribe(OrderEventType.ORDER_PLACED, emailObserver);
```

Expected output:

```text
Observer EmailNotificationObserver subscribed to event type ORDER_PLACED
```

### Duplicate Subscription Handling

If the same observer subscribes again to the same event type:

```java
dispatcher.subscribe(OrderEventType.ORDER_PLACED, emailObserver);
dispatcher.subscribe(OrderEventType.ORDER_PLACED, emailObserver);
```

Expected output:

```text
Observer EmailNotificationObserver is already subscribed to event type ORDER_PLACED
```

### Important

Duplicate checking is event-specific.

The same observer can be subscribed to multiple event types.

Example:

```java
dispatcher.subscribe(OrderEventType.ORDER_PLACED, auditObserver);
dispatcher.subscribe(OrderEventType.PAYMENT_SUCCESS, auditObserver);
dispatcher.subscribe(OrderEventType.ORDER_SHIPPED, auditObserver);
dispatcher.subscribe(OrderEventType.ORDER_CANCELLED, auditObserver);
```

This is allowed.

---

### 11.2 `unsubscribe()`

Removes an observer from a specific event type.

Example:

```java
dispatcher.unsubscribe(OrderEventType.ORDER_PLACED, emailObserver);
```

Expected output:

```text
Observer EmailNotificationObserver unsubscribed from event type ORDER_PLACED
```

If observer is not subscribed:

```text
Observer EmailNotificationObserver is not subscribed to event type ORDER_PLACED
```

---

### 11.3 `publish()`

This is the main Observer Pattern method in this module.

When this is called:

```java
dispatcher.publish(orderEvent);
```

The dispatcher should:

1. Add the event to event history.
2. Check the event type.
3. Get observers subscribed to that event type.
4. Copy the observer list.
5. Call `observer.onEvent(event)` for each observer.
6. Catch observer failures so one failing observer does not stop others.

Expected logic:

```java
@Override
public void publish(OrderEvent event) {
    eventHistory.add(event);

    OrderEventType eventType = event.getEventType();
    List<OrderEventObserver> observers = observersByEventType.get(eventType);

    if (observers == null || observers.isEmpty()) {
        System.out.println("No observers found for event type " + eventType);
        return;
    }

    List<OrderEventObserver> observersToNotify = new ArrayList<>(observers);

    for (OrderEventObserver observer : observersToNotify) {
        try {
            observer.onEvent(event);
        } catch (Exception e) {
            System.out.println("Error while updating observer "
                    + observer.getObserverName()
                    + ": "
                    + e.getMessage());
        }
    }
}
```

### Why catch exceptions?

In backend systems, one listener failing should not stop other listeners.

Example:

```text
InvoiceObserver succeeds
LoyaltyPointsObserver succeeds
FailingObserver fails
AuditLogObserver should still run
```

This is called failure isolation.

---

### 11.4 `getEventHistory()`

Should return all events that were published.

Recommended:

```java
@Override
public List<OrderEvent> getEventHistory() {
    return new ArrayList<>(eventHistory);
}
```

### Why return a copy?

Do not return the original list directly.

If you return the original list, outside code can modify your internal event history.

Returning a copy protects internal state.

---

## 12. Required Concrete Observers

### 12.1 Email Notification Observer

Class:

```java
EmailNotificationObserver
```

Subscribes to:

```java
ORDER_PLACED
```

Expected behavior:

```text
EMAIL: Order placed notification sent to customer CUST-101 for order ORD-1
```

---

### 12.2 Inventory Observer

Class:

```java
InventoryObserver
```

Subscribes to:

```java
ORDER_PLACED
ORDER_CANCELLED
```

Expected behavior for `ORDER_PLACED`:

```text
INVENTORY: Stock reserved for order ORD-1
```

Expected behavior for `ORDER_CANCELLED`:

```text
INVENTORY: Stock released for cancelled order ORD-1
```

Since this observer handles two event types, it can check:

```java
switch (event.getEventType()) {
    case ORDER_PLACED -> ...
    case ORDER_CANCELLED -> ...
}
```

---

### 12.3 Invoice Observer

Class:

```java
InvoiceObserver
```

Subscribes to:

```java
PAYMENT_SUCCESS
```

Expected behavior:

```text
INVOICE: Invoice generated for order ORD-1 amount 2500.0
```

---

### 12.4 Loyalty Points Observer

Class:

```java
LoyaltyPointsObserver
```

Subscribes to:

```java
PAYMENT_SUCCESS
```

Expected behavior:

```text
LOYALTY: Reward points added for customer CUST-101
```

---

### 12.5 SMS Notification Observer

Class:

```java
SmsNotificationObserver
```

Subscribes to:

```java
ORDER_SHIPPED
```

Expected behavior:

```text
SMS: Shipping notification sent to customer CUST-101 for order ORD-1
```

---

### 12.6 Refund Observer

Class:

```java
RefundObserver
```

Subscribes to:

```java
ORDER_CANCELLED
```

Expected behavior:

```text
REFUND: Refund processed for order ORD-2 amount 5000.0
```

or:

```text
REFUND: Refund initiated for cancelled order ORD-2
```

Either wording is acceptable.

---

### 12.7 Audit Log Observer

Class:

```java
AuditLogObserver
```

Subscribes to all events:

```java
ORDER_PLACED
PAYMENT_SUCCESS
ORDER_SHIPPED
ORDER_CANCELLED
```

Expected behavior:

```text
AUDIT: Event ORDER_PLACED recorded for order ORD-1
```

---

### 12.8 Failing Observer

Class:

```java
FailingObserver
```

Subscribes to:

```java
PAYMENT_SUCCESS
```

Expected behavior:

```java
throw new RuntimeException("Intentional observer failure");
```

This observer intentionally fails to test that the dispatcher catches the error and continues notifying other observers.

Expected output:

```text
Error while updating observer FailingObserver: Intentional observer failure
```

---

## 13. Required Order Service

Create:

```java
public class OrderService
```

Suggested fields:

```java
private final EventDispatcher eventDispatcher;
private final Map<String, OrderStatus> orderStatusMap;
```

Use:

```java
HashMap<String, OrderStatus>
```

### Purpose

`OrderService` acts as the event publisher.

It does not notify observers directly.

It only publishes events to the dispatcher.

Example:

```java
eventDispatcher.publish(event);
```

---

## 14. Required Order Service Methods

### 14.1 `placeOrder()`

Signature:

```java
public void placeOrder(String orderId, String customerId, double amount)
```

Expected behavior:

1. Validate that order does not already exist.
2. Store order status as `PLACED`.
3. Create `ORDER_PLACED` event.
4. Publish event.

Expected status transition:

```text
CREATED -> PLACED
```

---

### 14.2 `markPaymentSuccess()`

Signature:

```java
public void markPaymentSuccess(String orderId, String customerId, double amount)
```

Expected behavior:

1. Check that order exists.
2. Ideally verify current status is `PLACED`.
3. Change status to `PAID`.
4. Create `PAYMENT_SUCCESS` event.
5. Publish event.

Expected status transition:

```text
PLACED -> PAID
```

---

### 14.3 `shipOrder()`

Signature:

```java
public void shipOrder(String orderId, String customerId, double amount)
```

Expected behavior:

1. Check that order exists.
2. Ideally verify current status is `PAID`.
3. Change status to `SHIPPED`.
4. Create `ORDER_SHIPPED` event.
5. Publish event.

Expected status transition:

```text
PAID -> SHIPPED
```

---

### 14.4 `cancelOrder()`

Signature:

```java
public void cancelOrder(String orderId, String customerId, double amount)
```

Expected behavior:

1. Check that order exists.
2. Do not allow cancellation if already cancelled.
3. Optionally do not allow cancellation if already shipped.
4. Change status to `CANCELLED`.
5. Create `ORDER_CANCELLED` event.
6. Publish event.

Expected status transition:

```text
PLACED -> CANCELLED
```

or:

```text
PAID -> CANCELLED
```

---

## 15. Expected Main Class

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to LLD Observer Design Pattern");

        EventDispatcher dispatcher = new SimpleEventDispatcher();

        OrderEventObserver emailObserver = new EmailNotificationObserver();
        OrderEventObserver inventoryObserver = new InventoryObserver();
        OrderEventObserver invoiceObserver = new InvoiceObserver();
        OrderEventObserver loyaltyObserver = new LoyaltyPointsObserver();
        OrderEventObserver smsObserver = new SmsNotificationObserver();
        OrderEventObserver refundObserver = new RefundObserver();
        OrderEventObserver auditObserver = new AuditLogObserver();
        OrderEventObserver failingObserver = new FailingObserver();

        dispatcher.subscribe(OrderEventType.ORDER_PLACED, emailObserver);
        dispatcher.subscribe(OrderEventType.ORDER_PLACED, inventoryObserver);
        dispatcher.subscribe(OrderEventType.ORDER_PLACED, auditObserver);

        dispatcher.subscribe(OrderEventType.PAYMENT_SUCCESS, invoiceObserver);
        dispatcher.subscribe(OrderEventType.PAYMENT_SUCCESS, loyaltyObserver);
        dispatcher.subscribe(OrderEventType.PAYMENT_SUCCESS, auditObserver);
        dispatcher.subscribe(OrderEventType.PAYMENT_SUCCESS, failingObserver);

        dispatcher.subscribe(OrderEventType.ORDER_SHIPPED, smsObserver);
        dispatcher.subscribe(OrderEventType.ORDER_SHIPPED, auditObserver);

        dispatcher.subscribe(OrderEventType.ORDER_CANCELLED, refundObserver);
        dispatcher.subscribe(OrderEventType.ORDER_CANCELLED, inventoryObserver);
        dispatcher.subscribe(OrderEventType.ORDER_CANCELLED, auditObserver);

        dispatcher.subscribe(OrderEventType.ORDER_PLACED, emailObserver); // duplicate test

        OrderService orderService = new OrderService(dispatcher);

        orderService.placeOrder("ORD-1", "CUST-101", 2500.0);
        orderService.markPaymentSuccess("ORD-1", "CUST-101", 2500.0);
        orderService.shipOrder("ORD-1", "CUST-101", 2500.0);

        orderService.placeOrder("ORD-2", "CUST-202", 5000.0);
        orderService.cancelOrder("ORD-2", "CUST-202", 5000.0);

        System.out.println("Total events published: " + dispatcher.getEventHistory().size());
    }
}
```

---

## 16. Expected Output Style

Exact text can differ, but the flow should be similar:

```text
Welcome to LLD Observer Design Pattern

Observer EmailNotificationObserver subscribed to event type ORDER_PLACED
Observer InventoryObserver subscribed to event type ORDER_PLACED
Observer AuditLogObserver subscribed to event type ORDER_PLACED

Observer InvoiceObserver subscribed to event type PAYMENT_SUCCESS
Observer LoyaltyPointsObserver subscribed to event type PAYMENT_SUCCESS
Observer AuditLogObserver subscribed to event type PAYMENT_SUCCESS
Observer FailingObserver subscribed to event type PAYMENT_SUCCESS

Observer SmsNotificationObserver subscribed to event type ORDER_SHIPPED
Observer AuditLogObserver subscribed to event type ORDER_SHIPPED

Observer RefundObserver subscribed to event type ORDER_CANCELLED
Observer InventoryObserver subscribed to event type ORDER_CANCELLED
Observer AuditLogObserver subscribed to event type ORDER_CANCELLED

Observer EmailNotificationObserver is already subscribed to event type ORDER_PLACED

EMAIL: Order placed notification sent to customer CUST-101 for order ORD-1
INVENTORY: Stock reserved for order ORD-1
AUDIT: Event ORDER_PLACED recorded for order ORD-1

INVOICE: Invoice generated for order ORD-1 amount 2500.0
LOYALTY: Reward points added for customer CUST-101
AUDIT: Event PAYMENT_SUCCESS recorded for order ORD-1
Error while updating observer FailingObserver: Intentional observer failure

SMS: Shipping notification sent to customer CUST-101 for order ORD-1
AUDIT: Event ORDER_SHIPPED recorded for order ORD-1

EMAIL: Order placed notification sent to customer CUST-202 for order ORD-2
INVENTORY: Stock reserved for order ORD-2
AUDIT: Event ORDER_PLACED recorded for order ORD-2

REFUND: Refund processed for order ORD-2 amount 5000.0
INVENTORY: Stock released for cancelled order ORD-2
AUDIT: Event ORDER_CANCELLED recorded for order ORD-2

Total events published: 5
```

---

## 17. Important Rules

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

This module is synchronous.

Do not directly notify observers from `OrderService`.

The `OrderService` should only publish events:

```java
eventDispatcher.publish(event);
```

The dispatcher should handle observer notification.

---

## 18. What This Module Tests

This module checks whether you understand:

- Observer Pattern in backend event-dispatcher style
- event-specific observer subscriptions
- event object design
- central dispatcher as the subject
- services publishing domain events
- observers/listeners reacting to events
- event history tracking
- failure isolation between observers
- status transition thinking in a service class

---

## 19. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct enums and `OrderEvent` | 1.5 |
| Correct `OrderEventObserver` interface | 1 |
| Correct `EventDispatcher` interface | 1 |
| Correct `SimpleEventDispatcher` with `EnumMap` | 2 |
| Event-specific subscribe/unsubscribe | 1 |
| Publish logic with event history | 1 |
| Failure isolation during observer notification | 1 |
| Correct concrete observers | 1 |
| Correct `OrderService` event publishing | 1 |
| Code readability | 0.5 |

Total: **10 marks**

---

## 20. Key Learning

The key learning of this module is:

```text
Business services should publish events.
A dispatcher should notify interested observers.
```

The most important call from `OrderService` is:

```java
eventDispatcher.publish(event);
```

The most important call inside the dispatcher is:

```java
observer.onEvent(event);
```

This is the Observer Design Pattern applied in a backend-style event dispatcher.
