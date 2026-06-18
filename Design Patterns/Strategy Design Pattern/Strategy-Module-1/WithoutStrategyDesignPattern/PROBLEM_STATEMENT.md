# Problem Statement: Discount Calculation Without Strategy Pattern

## Context

An e-commerce or billing system needs to calculate discounts for different customer categories. In this module, the supported customer types are regular, premium, and VIP customers. Each customer type receives a different percentage discount on the purchase amount:

- Regular customers receive a 10% discount.
- Premium customers receive a 20% discount.
- VIP customers receive a 30% discount.
- Unknown customer types receive no discount.

The `Main` class demonstrates this by passing multiple customer type strings and purchase amounts to `DiscountCalculator`.

## Problem

The discount calculation logic is implemented inside one method using a chain of `if` and `else if` conditions. The method receives the customer type as a plain string and decides which discount rule to apply by comparing that string with known values.

This works for a small demonstration, but it creates a maintenance problem as the number of discount rules grows. Every new customer type requires editing the same `DiscountCalculator` method. For example, adding a "student", "employee", or "festival" discount would require adding more conditional branches to the same class.

## Why This Becomes Difficult

The calculator is responsible for too many decisions:

- It identifies the customer category.
- It knows the discount percentage for every category.
- It decides what to do when the category is unknown.
- It must be changed every time a new category is introduced.

Because the customer type is represented as a string, typing mistakes such as `PREMIUMM` or `vip` are not caught at compile time. The method simply falls through and returns zero discount. This makes incorrect input harder to detect and can hide bugs in larger systems.

The design also violates the Open/Closed Principle. The calculator is not closed for modification because every new discount rule requires changing existing code. Over time, the method can become long, repetitive, and risky to update.

## Expected Behavior

The system should calculate the correct discount amount for each supported customer type and print the purchase amount, discount, and final amount. The current implementation should make it clear why direct conditional logic is simple at first but becomes harder to maintain as business rules expand.

## Learning Goal

This module demonstrates the problem that the Strategy Pattern solves: when several interchangeable algorithms exist for the same operation, placing all of them in conditional statements inside one class leads to tight coupling and poor extensibility.
