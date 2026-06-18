# Problem Statement: Payment Processing Without Strategy Pattern

## Context

A payment system needs to process payments through multiple payment modes. In this module, the supported modes are UPI, card, and net banking. Each mode has its own validation or processing steps before the payment is completed.

The `Main` class demonstrates this by creating a `PaymentService` and calling it with different payment mode strings, amounts, and customer IDs.

## Problem

The payment processing logic is implemented inside one `makePayment` method using conditional statements. The method receives the payment mode as a string and decides which set of payment steps to execute.

This makes the service simple for a small example, but it does not scale well. Real payment systems often need mode-specific validation, request fields, gateway calls, error handling, auditing, retry policies, and compliance checks. Placing all of those details in one conditional method would make the service difficult to maintain.

## Why This Becomes Difficult

The `PaymentService` is responsible for too much:

- It identifies the payment mode.
- It contains UPI-specific behavior.
- It contains card-specific behavior.
- It contains net-banking-specific behavior.
- It handles invalid modes.
- It must be edited every time a new payment mode is added.

The use of raw strings also makes the system error-prone. A typo such as `NETBANKING` instead of `NET_BANKING` would be treated as invalid at runtime. The compiler cannot help catch these mistakes.

The design violates the Open/Closed Principle because extending payment behavior requires modifying the existing service. In a payment domain, modifying central payment code can be risky because it may affect all payment modes, not just the one being added.

## Expected Behavior

The system should print validation and payment messages for UPI, card, and net banking payments. The implementation should make the limitation clear: each new payment mode increases the size and responsibility of the same service method.

## Learning Goal

This module demonstrates why payment modes are a strong candidate for the Strategy Pattern. Payment processing has one shared goal, but the algorithm varies by payment mode. Conditional logic keeps those variations tightly coupled inside one service.
