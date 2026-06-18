# Problem Statement: Discount Calculation With Strategy Pattern

## Context

An e-commerce or billing system needs to calculate discounts for different customer categories. The business supports regular, premium, and VIP customers, and each category has its own discount rule:

- Regular customers receive a 10% discount.
- Premium customers receive a 20% discount.
- VIP customers receive a 30% discount.

The system must calculate the discount for a purchase amount without forcing the central calculator to contain every discount rule directly.

## Problem

The discount calculation behavior changes depending on the customer type. If the calculator stores all discount rules in conditional statements, the calculator must be edited whenever a new customer category or discount rule is introduced.

This module solves that problem by separating the discount algorithm from the calculator. `DiscountStrategy` defines the common contract for calculating a discount. `RegularDiscountStrategy`, `PremiumDiscountStrategy`, and `VipDiscountStrategy` each implement one specific rule. `DiscountCalculator` receives a strategy and delegates the calculation to it.

## Why This Design Is Needed

The key design problem is that discount rules vary independently from the act of calculating a final amount. The system needs one stable calculator workflow, but multiple interchangeable discount algorithms.

By using the Strategy Pattern:

- Each discount rule has its own class.
- `DiscountCalculator` does not need to know which customer type is being handled.
- Adding a new discount rule can be done by adding a new strategy class.
- Existing discount classes do not need to be modified when a new rule is added.

This makes the design easier to extend and easier to test. Each discount rule can be checked independently, and the calculator only needs to verify that it delegates correctly.

## Expected Behavior

The `Main` class should create different discount strategy objects, pass each one to `DiscountCalculator`, and print the purchase amount, calculated discount, and final amount. The output should demonstrate that the same calculator can use different discount behaviors at runtime.

## Learning Goal

This module demonstrates how Strategy replaces conditional discount selection with polymorphism. The problem is no longer solved by asking "which customer type is this?" inside the calculator. Instead, the caller provides the correct strategy, and the calculator uses the common interface.
