# Problem Statement: Notification Sending Without Strategy Pattern

## Context

An application needs to send notifications through different communication channels. In this module, the supported channels are email, SMS, and WhatsApp. The `Main` class sends the same test message through multiple channel names and also includes an invalid channel.

## Problem

The notification behavior is implemented in `NotificationService` using conditional logic. The service receives a channel string and then decides how to send the message using `if`, `else if`, and `else` branches.

This approach works while the channel list is small, but it becomes difficult to maintain when more notification channels are added. Adding push notifications, in-app notifications, voice calls, or third-party integrations would require editing the same service method repeatedly.

## Why This Becomes Difficult

The service mixes multiple responsibilities:

- It decides which channel was requested.
- It contains the sending logic for every supported channel.
- It handles invalid channel input.
- It must be modified whenever a new channel is introduced.

The channel is represented as a plain string, so invalid values are easy to pass. A typo or casing mismatch can route the request to the invalid channel branch. In a real application, this could silently prevent important messages from being sent.

As the channel-specific logic grows, each branch may need different validation, formatting, provider credentials, retry rules, or error handling. Keeping all of that in one method would make the service large and fragile.

## Expected Behavior

The system should print a different notification message for each supported channel and print an invalid channel message when the channel is not recognized. The implementation should expose the drawback of using direct conditionals for behavior that is expected to vary.

## Learning Goal

This module demonstrates the design pressure that leads to the Strategy Pattern. Sending a notification is one operation, but the algorithm for sending it changes depending on the channel. Keeping those algorithms inside one conditional method makes the design harder to extend.
