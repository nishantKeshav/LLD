# Observer Design Pattern Practice — Module 1

## Difficulty Level

**Beginner**

This module is designed to help you practise the **Observer Design Pattern** using plain Java.

The goal is not to write production-level code yet. The goal is to understand how a **Subject** stores multiple **Observers** and notifies them when something important happens.

---

# Scenario

You are building a simple **YouTube channel notification system**.

When a YouTube channel uploads a new video, all subscribers of that channel should automatically receive a notification.

This is a perfect beginner example of the Observer Design Pattern because:

- one channel can have many subscribers
- subscribers can subscribe to the channel
- subscribers can unsubscribe from the channel
- when a new video is uploaded, all current subscribers are notified

---

# Real-World Analogy

Think of a YouTube channel.

When you subscribe to a channel, you are saying:

> “Tell me whenever this channel uploads a new video.”

The channel does not manually call each subscriber one by one in hardcoded logic. Instead, it keeps a list of subscribers.

When a new video is uploaded, the channel loops through that list and notifies everyone.

---

# Observer Pattern Mapping

| Observer Pattern Term | In This Exercise | Meaning |
|---|---|---|
| Subject / Observable | `YouTubeChannel` | The object being watched |
| Observer | `Subscriber` | The object waiting for updates |
| Concrete Observer | `UserSubscriber` | A real user subscribed to the channel |
| Subscribe/Register | `subscribe()` | Add observer to the subject |
| Unsubscribe/Deregister | `unsubscribe()` | Remove observer from the subject |
| Notify | `notifySubscribers()` | Tell all observers about the new video |
| Update | `update()` | Observer receives the notification |

---

# Expected Flow

```text
Subscriber subscribes
        ↓
Channel stores subscriber in a list
        ↓
Channel uploads a new video
        ↓
Channel notifies all subscribers
        ↓
Each subscriber receives the video notification
```

---

# Required Classes

You need to create the following Java classes/interfaces:

```text
Subscriber
UserSubscriber
Channel
YouTubeChannel
Main
```

---

# 1. Observer Interface — `Subscriber`

Create an interface named `Subscriber`.

This represents the **Observer**.

```java
public interface Subscriber {
    void update(String videoTitle);
}
```

## Purpose

The `update()` method is called when the channel uploads a new video.

Every subscriber should receive the video title through this method.

---

# 2. Concrete Observer — `UserSubscriber`

Create a class named `UserSubscriber`.

This class represents a real subscriber/user.

```java
public class UserSubscriber implements Subscriber {
    private final String userName;

    public UserSubscriber(String userName) {
        this.userName = userName;
    }

    @Override
    public void update(String videoTitle) {
        System.out.println(userName + " got notification for new video: " + videoTitle);
    }
}
```

## Purpose

Each `UserSubscriber` object represents one user.

Example users:

```text
Amit
Priya
Rahul
```

When the channel uploads a video, each user's `update()` method should be called.

---

# 3. Subject Interface — `Channel`

Create an interface named `Channel`.

This represents the **Subject / Observable** contract.

```java
public interface Channel {
    void subscribe(Subscriber subscriber);

    void unsubscribe(Subscriber subscriber);

    void notifySubscribers(String videoTitle);
}
```

## Purpose

The channel should be able to:

1. add subscribers
2. remove subscribers
3. notify all subscribers

---

# 4. Concrete Subject — `YouTubeChannel`

Create a class named `YouTubeChannel`.

This is the actual channel that users subscribe to.

It should implement `Channel`.

Required fields:

```java
private final String channelName;
private final List<Subscriber> subscribers;
```

## Expected methods

```java
subscribe(Subscriber subscriber)
unsubscribe(Subscriber subscriber)
notifySubscribers(String videoTitle)
uploadVideo(String videoTitle)
```

## Important Rule

The `uploadVideo()` method should call `notifySubscribers(videoTitle)`.

Example:

```java
public void uploadVideo(String videoTitle) {
    System.out.println(channelName + " uploaded new video: " + videoTitle);
    notifySubscribers(videoTitle);
}
```

---

# 5. Main Class

Create a `Main` class to test your implementation.

Expected usage:

```java
public class Main {
    public static void main(String[] args) {
        YouTubeChannel channel = new YouTubeChannel("CodeWithShashwat");

        Subscriber user1 = new UserSubscriber("Amit");
        Subscriber user2 = new UserSubscriber("Priya");
        Subscriber user3 = new UserSubscriber("Rahul");

        channel.subscribe(user1);
        channel.subscribe(user2);
        channel.subscribe(user3);

        channel.uploadVideo("Observer Design Pattern in Java");
    }
}
```

---

# Expected Output

```text
Amit subscribed to CodeWithShashwat
Priya subscribed to CodeWithShashwat
Rahul subscribed to CodeWithShashwat

CodeWithShashwat uploaded new video: Observer Design Pattern in Java

Amit got notification for new video: Observer Design Pattern in Java
Priya got notification for new video: Observer Design Pattern in Java
Rahul got notification for new video: Observer Design Pattern in Java
```

Your exact formatting can differ slightly, but the behavior should be the same.

---

# Rules

For this module, keep it simple.

Do **not** use:

```text
Thread
ExecutorService
Spring Boot
Kafka
RabbitMQ
Database
REST API
```

Use only plain Java.

---

# What You Should Learn From This Module

After completing this module, you should understand:

- what a subject is
- what an observer is
- how observers subscribe to a subject
- how observers unsubscribe from a subject
- how the subject notifies all observers
- why the subject should not hardcode specific subscriber behavior

---

# What I Will Check When Reviewing Your Code

I will score your code out of 10 based on this rubric:

| Area | Marks |
|---|---:|
| Correct `Subscriber` observer interface | 1 |
| Correct `Channel` subject interface | 1 |
| Correct `UserSubscriber` concrete observer | 2 |
| Correct `YouTubeChannel` concrete subject | 2 |
| Proper subscribe/unsubscribe/notify flow | 2 |
| Code readability | 1 |
| Correct understanding of Observer Pattern | 1 |

Total: **10 marks**

---

# Your Task

Write the full Java code for:

```text
Subscriber.java
UserSubscriber.java
Channel.java
YouTubeChannel.java
Main.java
```

Then paste your code for review.

I will evaluate it like a senior backend engineer and score it out of 10.
