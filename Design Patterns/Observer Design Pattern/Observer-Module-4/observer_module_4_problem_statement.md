# Observer Design Pattern Practice — Module 4

## Multi-Event Product Alert System

### Difficulty Level

**Intermediate++ / Backend-style**

This module is a more advanced Observer Design Pattern exercise.

In the previous modules, observers were subscribed to one general event, such as:

```text
Product is back in stock
```

But in real backend systems, one subject can produce many different types of events.

For example, an e-commerce product can trigger:

```text
RESTOCK
PRICE_DROP
LOW_STOCK
```

Different users may care about different events.

One user may want only restock alerts.  
Another user may want only price-drop alerts.  
Another user may want both restock and price-drop alerts.

This module focuses on **event-specific subscriptions**.

---

## 1. Problem Statement

You are building a product alert system for an e-commerce application.

A product can trigger different events:

```text
RESTOCK
PRICE_DROP
LOW_STOCK
```

Users should be able to subscribe to specific event types.

For example:

```java
product.subscribe(ProductEventType.RESTOCK, user1);
product.subscribe(ProductEventType.PRICE_DROP, user2);
product.subscribe(ProductEventType.LOW_STOCK, user3);
```

When a `RESTOCK` event happens, only RESTOCK observers should be notified.

When a `PRICE_DROP` event happens, only PRICE_DROP observers should be notified.

When a `LOW_STOCK` event happens, only LOW_STOCK observers should be notified.

This is different from basic Observer Pattern examples where every observer receives every update.

---

## 2. Real-Life Analogy

Imagine Amazon or Flipkart product alerts.

A product has multiple types of alerts:

```text
Notify me when back in stock
Notify me when price drops
Notify me when only a few items are left
```

Different customers may choose different alert types.

Example:

| User | Subscribed Event |
|---|---|
| Amit | RESTOCK |
| Priya | PRICE_DROP |
| Rahul | RESTOCK and PRICE_DROP |
| Sneha | LOW_STOCK |

Now suppose the price drops.

Only Priya and Rahul should be notified.

Amit should not receive price-drop notification because he subscribed only for restock.

This is the main idea of Module 4.

---

## 3. Observer Pattern Mapping

| Observer Pattern Term | Meaning in This Module |
|---|---|
| Subject / Observable | Product |
| Concrete Subject | `Product` |
| Observer | User notification receiver |
| Observer Interface | `ProductObserver` |
| Concrete Observers | `EmailProductObserver`, `SmsProductObserver`, `PushProductObserver` |
| Event Type | `ProductEventType` |
| Event Object | `ProductEvent` |
| Subscribe | Subscribe user to a specific event type |
| Unsubscribe | Remove user from a specific event type |
| Notify | Notify only users subscribed to that event type |

---

## 4. Key Difference from Previous Modules

### Previous Modules

Earlier, you had something like:

```java
List<StockObserver> observers;
```

That means every observer receives every notification.

### Module 4

Now you should use:

```java
Map<ProductEventType, List<ProductObserver>> observersByEventType;
```

This means observers are grouped by event type.

Example internal structure:

```text
RESTOCK
  - CUST-101
  - CUST-103

PRICE_DROP
  - CUST-102
  - CUST-103

LOW_STOCK
  - CUST-101
```

So when `PRICE_DROP` happens, only this list is notified:

```text
PRICE_DROP
  - CUST-102
  - CUST-103
```

---

## 5. Required Flow

Your program should follow this flow:

```text
Product starts with:
stock = 2
price = 80000
lowStockThreshold = 1

User 1 subscribes to RESTOCK
User 2 subscribes to PRICE_DROP
User 3 subscribes to RESTOCK
User 3 also subscribes to PRICE_DROP
User 1 subscribes to LOW_STOCK

User 1 tries duplicate RESTOCK subscription
System should prevent duplicate subscription

Product purchase reduces stock from 2 to 1
LOW_STOCK event should trigger

Product purchase reduces stock from 1 to 0
LOW_STOCK should not trigger again if threshold was already crossed

Another purchase fails because stock is 0

Product is restocked from 0 to 5
RESTOCK event should trigger

Product price increases from 80000 to 85000
PRICE_DROP should not trigger

Product price decreases from 85000 to 75000
PRICE_DROP should trigger

User 2 unsubscribes from PRICE_DROP

Product price decreases from 75000 to 70000
Only remaining PRICE_DROP observers should be notified
```

---

## 6. Required Components

---

## 6.1 Event Type Enum

Create:

```java
public enum ProductEventType {
    RESTOCK,
    PRICE_DROP,
    LOW_STOCK
}
```

### Purpose

This enum defines the types of product events that users can subscribe to.

Instead of using strings like:

```java
"RESTOCK"
"PRICE_DROP"
"LOW_STOCK"
```

we use enum values because they are safer and cleaner.

---

## 6.2 Product Event Class

Create:

```java
public class ProductEvent
```

Suggested fields:

```java
private final ProductEventType eventType;
private final String productId;
private final String productName;
private final int availableQuantity;
private final double oldPrice;
private final double newPrice;
private final String message;
private final LocalDateTime eventTime;
```

### Purpose

The `ProductEvent` object carries event data to the observers.

Instead of passing multiple values separately:

```java
observer.update(productName, quantity, oldPrice, newPrice);
```

you pass one object:

```java
observer.update(productEvent);
```

This is cleaner and more backend-realistic.

---

## 6.3 Product Observer Interface

Create:

```java
public interface ProductObserver {
    String getUserId();

    void update(ProductEvent event);
}
```

### Purpose

This is the Observer interface.

Every notification receiver must implement this interface.

The most important method is:

```java
void update(ProductEvent event);
```

This method gets called when the subscribed event occurs.

---

## 6.4 Concrete Observers

Create at least three observers:

```text
EmailProductObserver
SmsProductObserver
PushProductObserver
```

Each one should implement `ProductObserver`.

---

### Email Observer

Suggested fields:

```java
private final String userId;
private final String email;
```

Expected output style:

```text
EMAIL sent to amit@gmail.com
User: CUST-101
Event: RESTOCK
Product: IPhone 15
Message: IPhone 15 is back in stock
Time: 2026-06-11T13:41:15
```

---

### SMS Observer

Suggested fields:

```java
private final String userId;
private final String phoneNumber;
```

Expected output style:

```text
SMS sent to 9876543210
User: CUST-102
Event: PRICE_DROP
Product: IPhone 15
Message: Price dropped from 85000.0 to 75000.0
Time: 2026-06-11T13:41:15
```

---

### Push Observer

Suggested fields:

```java
private final String userId;
private final String deviceToken;
```

Expected output style:

```text
Push Notification sent to device token: device-token-123
User: CUST-103
Event: RESTOCK
Product: IPhone 15
Message: IPhone 15 is back in stock
Time: 2026-06-11T13:41:15
```

---

## 6.5 Product Observable Interface

Create:

```java
public interface ProductObservable {
    void subscribe(ProductEventType eventType, ProductObserver observer);

    void unsubscribe(ProductEventType eventType, ProductObserver observer);

    boolean purchase(int quantity);

    void restock(int quantity);

    void updatePrice(double newPrice);
}
```

### Important

Notice that subscription now needs an event type:

```java
subscribe(ProductEventType eventType, ProductObserver observer)
```

This is because one user can subscribe to one event type, another user can subscribe to another event type, and one user can also subscribe to multiple event types.

---

## 6.6 Concrete Subject: Product

Create:

```java
public class Product implements ProductObservable
```

Suggested fields:

```java
private final String productId;
private final String productName;
private int stockQuantity;
private double price;
private final int lowStockThreshold;
private final Map<ProductEventType, List<ProductObserver>> observersByEventType;
```

Use:

```java
EnumMap<ProductEventType, List<ProductObserver>>
```

Example:

```java
private final Map<ProductEventType, List<ProductObserver>> observersByEventType =
        new EnumMap<>(ProductEventType.class);
```

### Why `EnumMap`?

Because your map key is an enum.

For enum keys, `EnumMap` is cleaner and efficient.

---

## 7. Required Behaviors

---

## 7.1 `subscribe()`

This method subscribes a user to a specific event type.

Example:

```java
product.subscribe(ProductEventType.RESTOCK, user1);
```

Expected output:

```text
CUST-101 subscribed for RESTOCK alerts on IPhone 15
```

### Duplicate subscription handling

If the same observer subscribes again to the same event type:

```java
product.subscribe(ProductEventType.RESTOCK, user1);
product.subscribe(ProductEventType.RESTOCK, user1);
```

Expected output:

```text
CUST-101 is already subscribed for RESTOCK alerts on IPhone 15
```

### Important

Duplicate checking is event-specific.

This should be allowed:

```java
product.subscribe(ProductEventType.RESTOCK, user1);
product.subscribe(ProductEventType.PRICE_DROP, user1);
```

Because the same user may subscribe to different event types.

---

## 7.2 `unsubscribe()`

This method removes a user from a specific event type.

Example:

```java
product.unsubscribe(ProductEventType.PRICE_DROP, user2);
```

Expected output:

```text
CUST-102 unsubscribed from PRICE_DROP alerts on IPhone 15
```

If the user is not subscribed:

```text
CUST-102 is not subscribed for PRICE_DROP alerts on IPhone 15
```

### Important

Unsubscribing from one event should not remove the user from all events.

Example:

```java
product.unsubscribe(ProductEventType.PRICE_DROP, user3);
```

If `user3` is also subscribed to `RESTOCK`, the RESTOCK subscription should remain.

---

## 7.3 `purchase(int quantity)`

This method reduces product stock when a purchase succeeds.

Recommended behavior:

```java
boolean purchase(int quantity);
```

Return:

```java
true
```

if purchase succeeds.

Return:

```java
false
```

if purchase fails.

### Purchase success

Example:

```text
Purchase successful: 1 units of IPhone 15
Remaining stock: 1
```

### Purchase failure

Example:

```text
Buying 1 for product IPhone 15 exceeds product quantity 0
```

### LOW_STOCK notification logic

LOW_STOCK should trigger only when stock crosses from safe stock level to low stock level.

Correct condition:

```java
if (previousStockQuantity > lowStockThreshold
        && stockQuantity <= lowStockThreshold) {
    notifyObservers(ProductEventType.LOW_STOCK, event);
}
```

### Example with threshold = 1

| Previous Stock | New Stock | LOW_STOCK Trigger? | Reason |
|---:|---:|---|---|
| 5 | 4 | No | Still safe |
| 3 | 2 | No | Still safe |
| 2 | 1 | Yes | Crossed into low stock |
| 1 | 0 | No | Already low before |
| 5 | 0 | Yes | Crossed directly into low stock |

---

## 7.4 `restock(int quantity)`

This method adds stock.

Example:

```text
Restock successful: 5 units of IPhone 15
Current stock: 5
```

### RESTOCK notification logic

RESTOCK should trigger only when stock changes from:

```text
0 -> greater than 0
```

Correct condition:

```java
if (previousStockQuantity == 0 && stockQuantity > 0) {
    notifyObservers(ProductEventType.RESTOCK, event);
}
```

### Example

| Previous Stock | New Stock | RESTOCK Trigger? | Reason |
|---:|---:|---|---|
| 0 | 5 | Yes | Product came back in stock |
| 3 | 8 | No | Product was already available |
| 0 | 0 | No | Product is still unavailable |

---

## 7.5 `updatePrice(double newPrice)`

This method updates the product price.

### If price increases

Example:

```java
product.updatePrice(85000.0);
```

Expected behavior:

```text
Price update successful: IPhone 15 new price is 85000.0
```

No `PRICE_DROP` notification should be sent.

### If price decreases

Example:

```java
product.updatePrice(75000.0);
```

Expected behavior:

```text
Price update successful: IPhone 15 new price is 75000.0
PRICE DROP ALERT triggered
```

Correct condition:

```java
if (newPrice < oldPrice) {
    notifyObservers(ProductEventType.PRICE_DROP, event);
}
```

---

## 7.6 Private `notifyObservers()`

Inside `Product`, create:

```java
private void notifyObservers(ProductEventType eventType, ProductEvent event)
```

Expected logic:

```java
private void notifyObservers(ProductEventType eventType, ProductEvent event) {
    List<ProductObserver> observers = observersByEventType.get(eventType);

    if (observers == null || observers.isEmpty()) {
        return;
    }

    List<ProductObserver> observersToNotify = new ArrayList<>(observers);

    for (ProductObserver observer : observersToNotify) {
        observer.update(event);
    }
}
```

### Why copy the list?

This line:

```java
new ArrayList<>(observers)
```

prevents problems if the observer list changes while notification is happening.

---

## 8. Expected Main Class

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Observer Module 4: Multi-Event Product Alert System");

        ProductObservable product = new Product(
                80000.0,
                2,
                "P-101",
                "IPhone 15",
                1
        );

        ProductObserver user1 = new EmailProductObserver("CUST-101", "amit@gmail.com");
        ProductObserver user2 = new SmsProductObserver("CUST-102", "9876543210");
        ProductObserver user3 = new PushProductObserver("CUST-103", "device-token-123");

        product.subscribe(ProductEventType.RESTOCK, user1);
        product.subscribe(ProductEventType.PRICE_DROP, user2);
        product.subscribe(ProductEventType.RESTOCK, user3);
        product.subscribe(ProductEventType.PRICE_DROP, user3);
        product.subscribe(ProductEventType.LOW_STOCK, user1);

        product.subscribe(ProductEventType.RESTOCK, user1); // duplicate test

        product.purchase(1);   // stock 2 -> 1, should trigger LOW_STOCK
        product.purchase(1);   // stock 1 -> 0, should not trigger LOW_STOCK again
        product.purchase(1);   // fail

        product.restock(5);    // should trigger RESTOCK

        product.updatePrice(85000.0); // no notification, price increased
        product.updatePrice(75000.0); // should trigger PRICE_DROP

        product.unsubscribe(ProductEventType.PRICE_DROP, user2);

        product.updatePrice(70000.0); // only user3 gets PRICE_DROP
    }
}
```

---

## 9. Rules

Use plain Java only.

Do not use:

```text
Spring Boot
Kafka
RabbitMQ
Thread
ExecutorService
```

Do not expose `notifyObservers()` in the public interface.

Keep notification logic inside the concrete subject.

---

## 10. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `ProductEventType` enum | 1 |
| Correct `ProductEvent` class | 1.5 |
| Correct `ProductObserver` interface | 1 |
| Concrete observers | 1.5 |
| `EnumMap<ProductEventType, List<ProductObserver>>` usage | 1.5 |
| Correct event-specific subscribe/unsubscribe | 1 |
| Correct RESTOCK notification logic | 1 |
| Correct PRICE_DROP notification logic | 1 |
| Correct LOW_STOCK notification logic | 1 |
| Code readability | 0.5 |

Total: **10 marks**

---

## 11. Key Learning

The key learning of this module is:

```text
Do not notify every observer for every event.
Notify only the observers who subscribed to that specific event type.
```

The important structure is:

```java
Map<ProductEventType, List<ProductObserver>> observersByEventType;
```

This gives you event-specific observer lists.

The important notification call is:

```java
notifyObservers(ProductEventType.PRICE_DROP, event);
```

This means:

```text
Only notify observers subscribed to PRICE_DROP.
```

That is a more realistic backend-style Observer Pattern implementation.
