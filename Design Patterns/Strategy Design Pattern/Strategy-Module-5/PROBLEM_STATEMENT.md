# Problem Statement: Refund Processing With Strategy Pattern and Validation

## Context

A payment or order management system needs to process customer refunds through multiple refund modes. In this module, the supported modes are original source, wallet, bank transfer, and manual adjustment.

Each refund mode represents a different business process:

- Original source refunds return money to the original transaction source.
- Wallet refunds add the amount to the customer's wallet.
- Bank transfer refunds send money to a bank account.
- Manual adjustments create an internal adjustment entry.

## Problem

The application needs to initiate refunds through different refund modes while keeping common validation and mode-specific behavior separate. A refund request has shared data such as amount, customer ID, refund request ID, and remarks. Some modes also require additional data, such as an original transaction ID or bank account number.

If all refund behavior were placed inside one service method, the service would need conditionals for every mode, plus all common and mode-specific validation. That would make refund processing hard to extend and risky to modify.

## Why This Design Is Needed

Refund logic usually has both common rules and mode-specific rules. Common rules apply to every refund, such as ensuring the amount is valid and the customer ID is present. Mode-specific rules apply only to certain strategies, such as requiring a bank account number for bank transfers or an original transaction ID for original-source refunds.

This module separates those responsibilities:

- `RefundRequest` groups the refund data.
- `RefundRequestValidator` validates fields that every refund mode needs.
- `RefundStrategy` defines the common refund behavior contract.
- Concrete strategies implement mode-specific refund behavior.
- `RefundService` validates common fields, selects the strategy from an `EnumMap`, and delegates the refund operation.

This design allows the refund service to remain stable while new refund modes are added as separate strategy classes.

## Expected Behavior

The `Main` class should create all refund strategies, create a refund request, and initiate a refund for each value in `RefundMode`. Each strategy should print the steps relevant to its refund mode. Common validation should run before strategy-specific behavior, and mode-specific validation should happen inside the strategy that needs it.

## Learning Goal

This module demonstrates a more complete Strategy Pattern use case. It shows not only interchangeable algorithms, but also how to combine shared validation in the service with specialized validation and processing inside each concrete strategy.
