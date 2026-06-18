# Problem Statement: Notification Strategy Lookup by Type

## Context

An application supports multiple notification channels such as email, SMS, and WhatsApp. In earlier Strategy examples, the caller directly supplied the strategy object to the service. This module moves one step closer to a real service design by allowing callers to request notification delivery by `NotificationType`.

## Problem

The application needs to send a notification by channel type without forcing the caller to manually choose and pass the correct strategy object each time. The caller should be able to say "send this message using EMAIL" or "send this message using SMS", and the service should route the request to the correct strategy.

At the same time, the service should avoid a large conditional block that checks the requested type and manually creates or calls each strategy.

## Why This Design Is Needed

In many applications, the caller knows the desired type of behavior but should not know the concrete class that implements it. For example, a controller or business workflow may know that the user prefers WhatsApp, but it should not need to instantiate `WhatsAppNotificationStrategy` directly.

This module solves that problem by creating a lookup map inside `NotificationService`. The service receives a list of strategies, asks each strategy for its `NotificationType`, and stores them in an `EnumMap`. When a notification request arrives, the service retrieves the matching strategy from the map and delegates the send operation.

This design keeps the service extensible:

- The lookup is data-driven through the strategy list.
- The service does not need an `if` or `switch` block for every channel.
- Each strategy still owns its own sending behavior.
- Missing strategy configuration is detected with an exception.

## Expected Behavior

The `Main` class should build the available strategies, create a `NotificationService`, and send the same message for each value in `NotificationType`. For every supported type, the matching strategy should be found and used.

## Learning Goal

This module demonstrates a common refinement of the Strategy Pattern: selecting a strategy from a registry or map by type. It shows how a service can choose the correct behavior at runtime without hardcoding all behavior selection in conditionals.
