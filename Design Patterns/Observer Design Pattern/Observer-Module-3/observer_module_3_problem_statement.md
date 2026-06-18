# Observer Design Pattern Practice — Module 3

## Product Stock “Notify Me” Feature

### Difficulty Level

**Intermediate**

This module is a practical backend-style exercise for the **Observer Design Pattern**.

In this module, you will build a simple e-commerce stock notification system where users can subscribe to a product when it is out of stock. When the product is restocked, all subscribed users should automatically receive a notification.

---

## 1. Problem Statement

You are building an e-commerce application.

A product has limited stock. Users can purchase the product while stock is available.

When the stock becomes `0`, another user may try to purchase it, but the purchase should fail.

At that point, users can subscribe for a **“Notify Me”** alert.

Later, when the product is restocked, all subscribed users should automatically receive a notification.

This is a perfect use case for the **Observer Design Pattern** because:

> One object changes state, and many interested objects need to react automatically.

In this module:

- The product is the object being observed.
- The users are observers.
- Restocking the product is the event.
- Notifying users is the observer update behavior.

---

## 2. Real-Life Analogy

Imagine you want to buy an iPhone from an online store.

The product is currently out of stock.

You click:

```text
Notify Me When Available
```

Many other users may also click the same button.

After a few days, the store restocks the iPhone.

Now the system automatically sends alerts to everyone who subscribed:

- one user gets an email
- another user gets an SMS
- another user gets a push notification

The product does not need to know the internal details of email, SMS, or push notification.

It only needs to say:

```text
Product is back in stock. Notify all subscribers.
```

That is the Observer Design Pattern.

---

## 3. Observer Pattern Mapping

| Observer Pattern Term | Meaning in This Module |
|---|---|
| Subject / Observable | Product being watched |
| Concrete Subject | `IphoneProductObservable` or product stock implementation |
| Observer | User waiting for stock notification |
| Observer Interface | `StockObserver` |
| Concrete Observers | `EmailNotificationObserver`, `SmsNotificationObserver`, `PushNotificationObserver` |
| Event | Product gets restocked |
| Subscribe | User joins the stock alert list |
| Unsubscribe | User leaves the stock alert list |
| Notify | Product informs all subscribed users |
| Update | Each observer receives stock update |

---

## 4. Required Flow

Your program should follow this flow:

```text
Product starts with stock = 2

User purchases 1 item
Stock becomes 1

User purchases 1 item
Stock becomes 0

Another user tries to purchase 1 item
Purchase fails because stock is 0

Users subscribe for stock notification

Product is restocked

All subscribed users are notified

Some users may unsubscribe

Product becomes out of stock again

Product is restocked again

Only currently subscribed users are notified
```

---

## 5. Required Components

You need to create the following components.

---

## 5.1 Observer Interface

Create an interface named:

```java
StockObserver
```

This interface represents a user who wants to be notified when the product is back in stock.

Expected structure:

```java
public interface StockObserver {
    String getUserId();

    void update(String productName, int productQuantity);
}
```

### Purpose

The `update()` method is the main Observer Pattern method.

The product will call this method when stock becomes available again.

Example:

```java
observer.update(productName, productQuantity);
```

This means:

```text
Hey user, this product is back in stock.
```

---

## 5.2 Concrete Observers

Create three observer classes:

```text
EmailNotificationObserver
SmsNotificationObserver
PushNotificationObserver
```

Each class should implement `StockObserver`.

---

### Email Notification Observer

This observer sends an email notification.

Suggested fields:

```java
private final String userId;
private final String email;
```

Expected behavior:

```text
EMAIL sent to amit@gmail.com
Email Notification to User CUST-101: Product 'iPhone 15' is back in stock with quantity 5
```

---

### SMS Notification Observer

This observer sends an SMS notification.

Suggested fields:

```java
private final String userId;
private final String phoneNumber;
```

Expected behavior:

```text
SMS sent to 9876543210
SMS Notification to User CUST-102: Product 'iPhone 15' is back in stock with quantity 5
```

---

### Push Notification Observer

This observer sends a push notification.

Suggested fields:

```java
private final String userId;
private final String deviceToken;
```

Expected behavior:

```text
Push notification sent to User CUST-103 with Device Token device-token-123
Push Notification sent to User CUST-103: Product 'iPhone 15' is back in stock with quantity 5
```

---

## 5.3 Subject / Observable Interface

Create an interface named:

```java
ProductStockObservable
```

This interface represents a product that users can observe.

Expected structure:

```java
public interface ProductStockObservable {
    void addObserver(StockObserver stockObserver);

    void removeObserver(StockObserver stockObserver);

    boolean purchase(int quantity);

    void restock(int quantity);
}
```

### Important

Do **not** expose `notifyObservers()` in this interface.

Why?

Because outside code should not randomly notify users.

Notification should happen only when a real product event happens, such as restocking.

So `notifyObservers()` should be private inside the concrete product class.

---

## 5.4 Concrete Subject

Create a concrete class such as:

```java
IphoneProductObservable
```

or:

```java
ProductStockObservableImpl
```

This class should implement `ProductStockObservable`.

Suggested fields:

```java
private final String productId;
private final String productName;
private int productQuantity;
private final List<StockObserver> observers;
```

### Purpose of each field

| Field | Purpose |
|---|---|
| `productId` | Unique product ID |
| `productName` | Product name shown in messages |
| `productQuantity` | Current stock quantity |
| `observers` | List of users subscribed for stock alerts |

---

## 6. Required Behaviors

---

## 6.1 `addObserver()`

This method subscribes a user for stock alerts.

Example:

```java
product.addObserver(user1);
```

Expected output:

```text
User CUST-101 subscribed for stock alert on iPhone 15
```

### Duplicate subscription handling

If the same observer is added again:

```java
product.addObserver(user1);
product.addObserver(user1);
```

Expected output:

```text
User CUST-101 is already subscribed for stock alert on iPhone 15
```

For this module, using `observers.contains(stockObserver)` is acceptable.

---

## 6.2 `removeObserver()`

This method unsubscribes a user from stock alerts.

Example:

```java
product.removeObserver(user1);
```

Expected output:

```text
User CUST-101 unsubscribed from stock alert on iPhone 15
```

### Invalid unsubscribe handling

If the user is not subscribed:

```java
product.removeObserver(user1);
```

Expected output:

```text
User CUST-101 is not subscribed for stock alert on iPhone 15
```

---

## 6.3 `purchase(int quantity)`

This method tries to purchase the product.

If enough stock is available, reduce the stock and return `true`.

Example:

```java
product.purchase(1);
```

Expected output:

```text
Purchase successful: 1 iPhone 15(s) purchased
Remaining stock: 1
```

If stock is not enough, return `false`.

Expected output:

```text
Purchase failed: iPhone 15 is out of stock
Available quantity: 0
```

### Recommended validation

You may add:

```java
if (quantity <= 0) {
    throw new IllegalArgumentException("Purchase quantity must be greater than zero");
}
```

---

## 6.4 `restock(int quantity)`

This method adds stock back to the product.

Example:

```java
product.restock(5);
```

Expected output:

```text
Restocked iPhone 15 with quantity: 5
Current stock: 5
```

### Important Business Rule

Notify observers only when stock changes from:

```text
0 -> greater than 0
```

That means this should notify:

```text
Old stock = 0
Restock = 5
New stock = 5
```

But this should not notify:

```text
Old stock = 3
Restock = 5
New stock = 8
```

Because the product was already available.

Correct condition:

```java
int previousProductQuantity = productQuantity;

productQuantity += quantity;

if (previousProductQuantity == 0 && productQuantity > 0) {
    notifyObservers();
}
```

### Recommended validation

You may add:

```java
if (quantity <= 0) {
    throw new IllegalArgumentException("Restock quantity must be greater than zero");
}
```

---

## 6.5 Private `notifyObservers()`

Inside the concrete product class, create a private method:

```java
private void notifyObservers() {
    List<StockObserver> observersToNotify = new ArrayList<>(observers);

    for (StockObserver observer : observersToNotify) {
        observer.update(productName, productQuantity);
    }
}
```

### Why copy the list?

Using this:

```java
new ArrayList<>(observers)
```

protects the loop if the observer list changes while notification is happening.

For example, if a user unsubscribes during notification, directly looping over the original list can cause problems.

---

## 7. Expected Main Class

Your `Main` class should look similar to this:

```java
public class Main {
    public static void main(String[] args) {

        System.out.println("Observer Design Pattern - E-Commerce Notify Me");

        ProductStockObservable product = new IphoneProductObservable("P-101", "iPhone 15", 2);

        StockObserver user1 = new EmailNotificationObserver("CUST-101", "amit@gmail.com");
        StockObserver user2 = new SmsNotificationObserver("CUST-102", "9876543210");
        StockObserver user3 = new PushNotificationObserver("CUST-103", "device-token-123");

        product.purchase(1);
        product.purchase(1);

        boolean success = product.purchase(1);

        if (!success) {
            product.addObserver(user1);
            product.addObserver(user2);
            product.addObserver(user3);
        }

        product.addObserver(user1); // duplicate test

        product.restock(5);

        product.removeObserver(user2);

        product.purchase(5);

        product.restock(3);
    }
}
```

---

## 8. Expected Output Style

Your exact text may differ, but the flow should be similar:

```text
Observer Design Pattern - E-Commerce Notify Me

Purchase successful: 1 iPhone 15(s) purchased
Remaining stock: 1

Purchase successful: 1 iPhone 15(s) purchased
Remaining stock: 0

Purchase failed: iPhone 15 is out of stock
Available quantity: 0

User CUST-101 subscribed for stock alert on iPhone 15
User CUST-102 subscribed for stock alert on iPhone 15
User CUST-103 subscribed for stock alert on iPhone 15
User CUST-101 is already subscribed for stock alert on iPhone 15

Restocked iPhone 15 with quantity: 5
Current stock: 5

EMAIL sent to amit@gmail.com
Email Notification to User CUST-101: Product 'iPhone 15' is back in stock with quantity 5

SMS sent to 9876543210
SMS Notification to User CUST-102: Product 'iPhone 15' is back in stock with quantity 5

Push notification sent to User CUST-103 with Device Token device-token-123
Push Notification sent to User CUST-103: Product 'iPhone 15' is back in stock with quantity 5

User CUST-102 unsubscribed from stock alert on iPhone 15

Purchase successful: 5 iPhone 15(s) purchased
Remaining stock: 0

Restocked iPhone 15 with quantity: 3
Current stock: 3

EMAIL sent to amit@gmail.com
Email Notification to User CUST-101: Product 'iPhone 15' is back in stock with quantity 3

Push notification sent to User CUST-103 with Device Token device-token-123
Push Notification sent to User CUST-103: Product 'iPhone 15' is back in stock with quantity 3
```

---

## 9. Rules

Use only plain Java.

Do not use:

```text
Thread
ExecutorService
Spring Boot
Kafka
RabbitMQ
```

Do not expose `notifyObservers()` in the observable interface.

Keep notification triggering inside the concrete product class.

---

## 10. What This Module Tests

This module tests whether you understand:

- how the subject stores observers
- how observers subscribe and unsubscribe
- how state changes trigger notifications
- how to pass event data to observers
- how to avoid notifying users unnecessarily
- how to prevent duplicate subscriptions
- how to handle invalid unsubscribe attempts

---

## 11. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `StockObserver` interface | 1 |
| Three concrete observers | 2 |
| Correct `ProductStockObservable` interface | 1 |
| Correct product subject class | 2 |
| Purchase/restock logic | 1.5 |
| Notify only when stock goes from `0` to `>0` | 1 |
| Duplicate/invalid observer handling | 1 |
| Code readability | 0.5 |

Total: **10 marks**

---

## 12. Key Learning

The most important Observer Pattern idea in this module is:

```java
observer.update(productName, productQuantity);
```

The product does not know whether the user will receive email, SMS, or push notification.

It only knows that every observer has an `update()` method.

Each observer decides how to react.

That is the power of the Observer Design Pattern.