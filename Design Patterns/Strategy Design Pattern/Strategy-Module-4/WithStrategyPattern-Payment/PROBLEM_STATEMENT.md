# Problem Statement: Payment Processing With Strategy Pattern

## Context

A payment system needs to support multiple payment modes while keeping each mode's processing rules separate. In this module, the supported modes are UPI, card, and net banking.

Each payment mode works toward the same business goal: process a payment request. However, each mode can have different validation, gateway interaction, and output requirements.

## Problem

The application needs to process payments without placing all mode-specific behavior inside one service method. The service should receive a payment mode and a payment request, find the correct payment behavior, and execute it.

This module introduces `PaymentStrategy` as the common abstraction. `UpiPaymentStrategy`, `CardPaymentStrategy`, and `NetBankingPaymentStrategy` each implement payment behavior for one mode. `PaymentService` stores the strategies in an `EnumMap` keyed by `PaymentMode`.

## Why This Design Is Needed

Payment behavior is expected to vary and expand over time. New modes such as wallet, EMI, pay later, or international card payments may be added. If the central service contains all payment rules directly, every new mode requires modifying core payment code.

By applying the Strategy Pattern:

- Each payment mode gets its own class.
- `PaymentService` depends on the `PaymentStrategy` interface.
- Strategy lookup is performed through `PaymentMode`.
- Payment request data is grouped in `PaymentRequest` instead of passing many loose parameters.
- Missing strategy configuration is reported with a clear exception.

This structure keeps payment-mode behavior isolated. The service coordinates strategy selection, while the concrete strategy performs the actual payment-specific work.

## Expected Behavior

The `Main` class should create all payment strategies, create one payment request, and process that request through every value in `PaymentMode`. For each mode, the service should find the matching strategy and print the validation and payment details.

## Learning Goal

This module demonstrates a production-style Strategy Pattern shape: a strategy interface, concrete mode implementations, a request object, an enum for supported modes, and a service that resolves strategies through a map instead of conditional logic.
