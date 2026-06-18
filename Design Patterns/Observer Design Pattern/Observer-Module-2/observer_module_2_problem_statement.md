# observer Design Pattern Practice — Module 2

## Module Name

**Multiple Notification Types in a YouTube Channel System**

## Difficulty Level

**Beginner+**

This module builds on Module 1. In Module 1, all subscribers behaved the same way. In Module 2, subscribers can receive notifications through different channels such as email, SMS, and push notification.

---

## Goal of This Module

The goal is to practise the **observer Design Pattern** by building a simple YouTube-style notification system.

When a YouTube channel uploads a new video, all subscribed users should automatically receive a notification.

Different users may receive notifications through different methods:

- Email
- SMS
- Push notification

The channel should not care how each subscriber receives the notification. It should only notify all subscribers by calling their `update()` method.

---

## Real-World Analogy

Think of a YouTube channel.

When a creator uploads a new video:

- Some users get an email notification.
- Some users get an SMS notification.
- Some users get a push notification on their phone.

The YouTube channel does not manually handle each notification type. It simply publishes the event: **new video uploaded**.

Each subscriber decides how to react to that event.

That is the observer Design Pattern.

---

## observer Pattern Mapping

| observer Pattern Term | In This Module | Meaning |
|---|---|---|
| Subject / Observable | `YouTubeChannel` | The object being watched |
| Subject Interface | `Channel` | Common contract for channel behavior |
| observer | `Subscriber` | The object waiting for updates |
| Concrete Observers | `EmailSubscriber`, `SmsSubscriber`, `PushSubscriber` | Actual subscribers with different notification methods |
| Subscribe | `subscribe()` | Add a subscriber to the channel |
| Unsubscribe | `unsubscribe()` | Remove a subscriber from the channel |
| Notify | private notification logic inside `YouTubeChannel` | Inform all subscribers when a video is uploaded |
| Update | `update(channelName, videoTitle)` | Subscriber reacts to the uploaded video |

---

## Expected Flow

```text
User subscribes to channel
        ↓
Channel stores the subscriber
        ↓
Channel uploads a new video
        ↓
Channel notifies all current subscribers
        ↓
Each subscriber receives update(channelName, videoTitle)
        ↓
Email subscriber sends email
SMS subscriber sends SMS
Push subscriber sends push notification
```

---

## Required Classes

You need to create the following classes and interfaces:

```text
Subscriber
EmailSubscriber
SmsSubscriber
PushSubscriber
Channel
YouTubeChannel
Main
```

---

# 1. observer Interface: `Subscriber`

Create an interface named `Subscriber`.

This interface represents the observer.

```java
public interface Subscriber {
    void update(String channelName, String videoTitle);

    String getUserName();
}
```

## Purpose

The `Subscriber` interface defines what every subscriber must be able to do.

Every subscriber must:

1. Receive an update when a new video is uploaded.
2. Return the subscriber's username.

The most important method is:

```java
void update(String channelName, String videoTitle);
```

This method is called by the channel when a new video is uploaded.

---

# 2. Concrete observer: `EmailSubscriber`

Create a class named `EmailSubscriber`.

It should implement `Subscriber`.

## Fields

```java
private final String userName;
private final String email;
```

## Behavior

When `update()` is called, it should print an email notification message.

Example:

```text
EMAIL sent to amit@gmail.com
Hi Amit, CodeWithMyD uploaded: observer Pattern Module 2
```

## Example Structure

```java
public class EmailSubscriber implements Subscriber {

    private final String userName;
    private final String email;

    public EmailSubscriber(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    @Override
    public void update(String channelName, String videoTitle) {
        System.out.println("EMAIL sent to " + email);
        System.out.println("Hi " + userName + ", " + channelName + " uploaded: " + videoTitle);
    }

    @Override
    public String getUserName() {
        return userName;
    }
}
```

---

# 3. Concrete observer: `SmsSubscriber`

Create a class named `SmsSubscriber`.

It should implement `Subscriber`.

## Fields

```java
private final String userName;
private final String mobileNumber;
```

## Behavior

When `update()` is called, it should print an SMS notification message.

Example:

```text
SMS sent to 9876543210
Hi Priya, CodeWithMyD uploaded: observer Pattern Module 2
```

---

# 4. Concrete observer: `PushSubscriber`

Create a class named `PushSubscriber`.

It should implement `Subscriber`.

## Fields

```java
private final String userName;
private final String deviceToken;
```

## Behavior

When `update()` is called, it should print a push notification message.

Example:

```text
PUSH sent to device-token-123
Hi Rahul, CodeWithMyD uploaded: observer Pattern Module 2
```

---

# 5. Subject Interface: `Channel`

Create an interface named `Channel`.

This interface represents the subject/observable contract.

```java
public interface Channel {
    void subscribe(Subscriber subscriber);

    void unsubscribe(Subscriber subscriber);

    void uploadVideo(String videoTitle);
}
```

## Purpose

The `Channel` interface says that any channel should allow users to:

1. Subscribe
2. Unsubscribe
3. Trigger a video upload event

For this module, do not expose `notifySubscribers()` in the interface.

In a cleaner backend-style design, notification should happen internally when a real event occurs, such as `uploadVideo()`.

---

# 6. Concrete Subject: `YouTubeChannel`

Create a class named `YouTubeChannel`.

It should implement `Channel`.

## Fields

```java
private final String channelName;
private final List<Subscriber> subscribers;
```

## Constructor

```java
public YouTubeChannel(String channelName) {
    this.channelName = channelName;
    this.subscribers = new ArrayList<>();
}
```

---

## `subscribe()` Behavior

The `subscribe()` method should add a subscriber to the list.

Example:

```text
Amit subscribed to CodeWithMyD
```

### Duplicate Subscription Requirement

If the same subscriber is already subscribed, do not add the subscriber again.

Example:

```java
channel.subscribe(user1);
channel.subscribe(user1);
```

Expected output:

```text
Amit subscribed to CodeWithMyD
Amit is already subscribed to CodeWithMyD
```

Hint:

```java
if (subscribers.contains(subscriber)) {
    // already subscribed
}
```

---

## `unsubscribe()` Behavior

The `unsubscribe()` method should remove a subscriber from the list.

Example:

```text
Priya unsubscribed from CodeWithMyD
```

### Invalid Unsubscribe Requirement

If a subscriber is not currently subscribed, print a clear message.

Example:

```text
Priya is not subscribed to CodeWithMyD
```

Hint:

```java
boolean removed = subscribers.remove(subscriber);

if (!removed) {
    // subscriber was not present
}
```

---

## `uploadVideo()` Behavior

The `uploadVideo()` method should print that a new video was uploaded.

Example:

```text
CodeWithMyD uploaded new video: observer Pattern Module 2
```

Then it should notify all current subscribers.

Example structure:

```java
@Override
public void uploadVideo(String videoTitle) {
    System.out.println(channelName + " uploaded new video: " + videoTitle);
    notifySubscribers(videoTitle);
}
```

---

## Private `notifySubscribers()` Method

Inside `YouTubeChannel`, create a private method:

```java
private void notifySubscribers(String videoTitle) {
    List<Subscriber> subscribersCopy = new ArrayList<>(subscribers);

    for (Subscriber subscriber : subscribersCopy) {
        subscriber.update(channelName, videoTitle);
    }
}
```

## Why create a copy?

Creating a copy protects the code if the subscriber list changes while notification is happening.

For example, if a subscriber unsubscribes during notification, directly looping over the original list could cause issues.

---

# 7. Main Class

Create a `Main` class to test the flow.

```java
public class Main {
    public static void main(String[] args) {
        Channel channel = new YouTubeChannel("CodeWithMyD");

        Subscriber user1 = new EmailSubscriber("Amit", "amit@gmail.com");
        Subscriber user2 = new SmsSubscriber("Priya", "9876543210");
        Subscriber user3 = new PushSubscriber("Rahul", "device-token-123");

        channel.subscribe(user1);
        channel.subscribe(user2);
        channel.subscribe(user3);

        // Duplicate subscription test
        channel.subscribe(user1);

        channel.uploadVideo("observer Pattern Module 2");

        channel.unsubscribe(user2);

        channel.uploadVideo("observer Pattern Module 2 - Part 2");

        // Invalid unsubscribe test
        channel.unsubscribe(user2);
    }
}
```

---

## Expected Output Style

Your exact wording can be different, but the flow should look like this:

```text
Amit subscribed to CodeWithMyD
Priya subscribed to CodeWithMyD
Rahul subscribed to CodeWithMyD
Amit is already subscribed to CodeWithMyD

CodeWithMyD uploaded new video: observer Pattern Module 2

EMAIL sent to amit@gmail.com
Hi Amit, CodeWithMyD uploaded: observer Pattern Module 2

SMS sent to 9876543210
Hi Priya, CodeWithMyD uploaded: observer Pattern Module 2

PUSH sent to device-token-123
Hi Rahul, CodeWithMyD uploaded: observer Pattern Module 2

Priya unsubscribed from CodeWithMyD

CodeWithMyD uploaded new video: observer Pattern Module 2 - Part 2

EMAIL sent to amit@gmail.com
Hi Amit, CodeWithMyD uploaded: observer Pattern Module 2 - Part 2

PUSH sent to device-token-123
Hi Rahul, CodeWithMyD uploaded: observer Pattern Module 2 - Part 2

Priya is not subscribed to CodeWithMyD
```

---

# Rules

For this module, use only plain Java.

Do not use:

```text
Spring Boot
Kafka
RabbitMQ
ExecutorService
Threads
Database
```

Focus only on the observer Design Pattern.

---

# What This Module Teaches

This module teaches you that:

1. One subject can have multiple observers.
2. Each observer can react differently to the same event.
3. The subject does not need to know the details of each observer.
4. Subscribers can be added and removed dynamically.
5. Duplicate subscriptions should be handled.
6. Invalid unsubscribe actions should be handled safely.

---

# Key Concept to Remember

The subject does not do this:

```java
if (subscriber is email subscriber) {
    send email;
} else if (subscriber is SMS subscriber) {
    send SMS;
}
```

Instead, it simply does this:

```java
subscriber.update(channelName, videoTitle);
```

Each subscriber decides what to do inside its own `update()` method.

That is the observer Design Pattern.

---

# Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `Subscriber` interface | 1 |
| Three concrete subscribers | 2 |
| Correct `Channel` interface | 1 |
| Correct `YouTubeChannel` subject | 2 |
| Proper subscribe/unsubscribe/notify flow | 2 |
| Duplicate subscription handling | 1 |
| Invalid unsubscribe handling | 1 |
| **Total** | **10** |

---

# Completion Criteria

You complete this module successfully if:

- A channel can have multiple subscribers.
- Email, SMS, and push subscribers all receive updates differently.
- Uploading a video notifies all current subscribers.
- Unsubscribed users stop receiving notifications.
- Duplicate subscriptions are prevented.
- Invalid unsubscribe attempts are handled clearly.
