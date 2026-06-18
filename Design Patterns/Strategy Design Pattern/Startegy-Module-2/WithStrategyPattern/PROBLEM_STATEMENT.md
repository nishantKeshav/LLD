# Problem Statement: Notification Sending With Strategy Pattern

## Context

An application needs to send the same kind of notification message through different communication channels. The supported channels in this module are email, SMS, and WhatsApp.

Each channel has the same high-level purpose, which is sending a message, but each channel can have different implementation details. Email may require email-specific formatting, SMS may have length limits, and WhatsApp may involve a different provider integration.

## Problem

The application needs to support multiple notification behaviors without placing all channel-specific logic in one service method. The system should allow the notification service to send a message through whichever channel strategy it receives.

This module introduces `NotificationStrategy` as the common contract. Each concrete strategy represents one channel: `EmailNotificationStrategy`, `SmsNotificationStrategy`, and `WhatsAppNotificationStrategy`. `NotificationService` receives one strategy and delegates the message sending operation to that strategy.

## Why This Design Is Needed

The behavior varies by notification channel, but the service should not be responsible for knowing the internal details of every channel. If every channel is implemented inside one service class, the service becomes harder to change and test.

By applying the Strategy Pattern:

- Channel-specific behavior is moved into separate strategy classes.
- `NotificationService` depends on the `NotificationStrategy` abstraction.
- The same service class can work with email, SMS, or WhatsApp behavior.
- A new channel can be added by creating a new strategy class that implements the same interface.

The module also introduces `NotificationType`, allowing each strategy to identify its channel in a structured way instead of relying only on raw strings.

## Expected Behavior

The `Main` class should create a list of notification strategies and send the same message through each one. The output should show that each strategy handles the sending operation for its own channel while the service remains unchanged.

## Learning Goal

This module demonstrates the basic Strategy Pattern structure for notification sending: a common interface, multiple concrete strategies, and a service that delegates behavior instead of implementing all variations itself.
