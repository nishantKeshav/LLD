# Factory Design Pattern Practice — Module 2

## Payment Processor Factory

### Difficulty Level

**Beginner+**

### Pattern Focus

**Simple Factory Pattern with Request Object**

### Backend Theme

**Payment mode routing**

---

# 1. Module Overview

This is **Module 2** of the Factory Design Pattern practice series.

In Module 1, you created a simple notification sender factory:

```text
NotificationType -> NotificationSender
```

In this module, you will build a backend-style payment processing system where different payment modes require different payment processors.

The system should support:

```text
UPI
CARD
NET_BANKING
WALLET
```

The main goal is to understand how the Factory Pattern helps select the correct payment processor based on the payment mode present inside a request object.

Instead of directly creating payment processors like this:

```java
PaymentProcessor processor = new UpiPaymentProcessor();
```

the client code should ask the factory for the correct processor:

```java
PaymentProcessor processor =
        PaymentProcessorFactory.getProcessor(request.getPaymentMode());
```

The factory will decide which concrete processor class to create.

---

# 2. Background: Why This Module Exists

In real backend payment systems, users can pay using different methods.

For example:

```text
UPI          -> user pays using VPA / UPI ID
CARD         -> user pays using debit card / credit card
NET_BANKING  -> user pays through bank redirect / bank authorization
WALLET       -> user pays using wallet balance
```

Each payment method has different processing logic.

For example:

```text
UPI may need VPA validation.
Card may need card tokenization and 3DS authentication.
Net banking may need bank code and redirect flow.
Wallet may need wallet balance check.
```

But from the payment service's point of view, the high-level goal is simple:

```text
Process this payment request.
```

The payment service should not worry about which concrete processor class should be created.

That is where Factory Pattern helps.

---

# 3. Core Factory Pattern Idea

Factory Pattern is used when:

```text
You have multiple classes implementing the same interface,
and you need to create the correct implementation based on input.
```

In this module:

```text
Input: PaymentMode
Output: Correct PaymentProcessor implementation
```

Example:

```text
PaymentMode.UPI          -> UpiPaymentProcessor
PaymentMode.CARD         -> CardPaymentProcessor
PaymentMode.NET_BANKING  -> NetBankingPaymentProcessor
PaymentMode.WALLET       -> WalletPaymentProcessor
```

So the factory becomes the centralized place for payment processor creation.

---

# 4. Problem Without Factory Pattern

Without Factory Pattern, your `Main` or `PaymentService` might look like this:

```java
PaymentProcessor processor;

if (request.getPaymentMode() == PaymentMode.UPI) {
    processor = new UpiPaymentProcessor();
} else if (request.getPaymentMode() == PaymentMode.CARD) {
    processor = new CardPaymentProcessor();
} else if (request.getPaymentMode() == PaymentMode.NET_BANKING) {
    processor = new NetBankingPaymentProcessor();
} else if (request.getPaymentMode() == PaymentMode.WALLET) {
    processor = new WalletPaymentProcessor();
} else {
    throw new IllegalArgumentException("Unsupported payment mode");
}

processor.processPayment(request);
```

This works, but it creates problems:

```text
1. Object creation logic is mixed with payment flow logic.
2. The client code knows about every concrete processor class.
3. If a new payment mode is added, multiple places may need changes.
4. The payment service becomes harder to maintain.
5. Repeated switch/if-else blocks can spread across the codebase.
6. Unit testing becomes harder because concrete object creation is hardcoded.
```

---

# 5. Problem With Factory Pattern

With Factory Pattern, the client code becomes cleaner:

```java
PaymentProcessor processor =
        PaymentProcessorFactory.getProcessor(request.getPaymentMode());

processor.processPayment(request);
```

Now the selection logic is moved into:

```java
PaymentProcessorFactory
```

The client only depends on:

```java
PaymentProcessor
```

not concrete classes like:

```java
UpiPaymentProcessor
CardPaymentProcessor
NetBankingPaymentProcessor
WalletPaymentProcessor
```

---

# 6. Main Objective

The objective of this module is to build a payment processing system where:

```text
1. All payment processors follow a common interface.
2. Each payment mode has its own processor implementation.
3. A factory class creates the correct processor based on PaymentMode.
4. The client code does not directly instantiate concrete processor classes.
5. A PaymentRequest object carries payment details.
6. The system is easy to extend with more payment modes later.
```

---

# 7. Required Classes

You need to implement the following classes/interfaces/enums:

```text
PaymentMode
PaymentRequest
PaymentProcessor
UpiPaymentProcessor
CardPaymentProcessor
NetBankingPaymentProcessor
WalletPaymentProcessor
PaymentProcessorFactory
Main
```

Optional but recommended:

```text
PaymentRequestValidator
```

---

# 8. Class Design Overview

## High-Level Structure

```text
PaymentProcessor
    ├── UpiPaymentProcessor
    ├── CardPaymentProcessor
    ├── NetBankingPaymentProcessor
    └── WalletPaymentProcessor

PaymentProcessorFactory
    └── Creates correct PaymentProcessor based on PaymentMode

PaymentRequest
    └── Holds payment details

Main
    └── Creates requests, asks factory for processor, processes payments
```

---

# 9. Class 1: `PaymentMode`

## Purpose

`PaymentMode` represents the payment method selected by the user.

Use an enum instead of raw strings.

## Required Code

```java
public enum PaymentMode {
    UPI,
    CARD,
    NET_BANKING,
    WALLET
}
```

## Why enum is better than String

Bad:

```java
PaymentProcessorFactory.getProcessor("UPI");
```

This can lead to typo bugs like:

```text
"upi"
"UPI "
"NetBanking"
"net banking"
```

Better:

```java
PaymentProcessorFactory.getProcessor(PaymentMode.UPI);
```

With enum, only valid values are allowed.

## Responsibility

```text
Define all supported payment modes.
```

---

# 10. Class 2: `PaymentRequest`

## Purpose

`PaymentRequest` represents one payment operation.

Instead of passing many separate values into the processor, you group them into one object.

## Required Fields

```text
paymentId
customerId
amount
currency
paymentMode
description
```

## Required Code

```java
public class PaymentRequest {

    private final String paymentId;
    private final String customerId;
    private final double amount;
    private final String currency;
    private final PaymentMode paymentMode;
    private final String description;

    public PaymentRequest(
            String paymentId,
            String customerId,
            double amount,
            String currency,
            PaymentMode paymentMode,
            String description
    ) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Payment ID cannot be null or blank.");
        }

        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank.");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank.");
        }

        if (paymentMode == null) {
            throw new IllegalArgumentException("Payment mode cannot be null.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank.");
        }

        this.paymentId = paymentId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMode = paymentMode;
        this.description = description;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public String getDescription() {
        return description;
    }
}
```

## Responsibility

```text
Represent a valid payment request.
Hold all data needed by payment processors.
Prevent invalid payment request objects from being created.
```

## Why validation in constructor is useful

A `PaymentRequest` should not exist in an invalid state.

Bad:

```java
new PaymentRequest(null, "", -500.0, "", null, "");
```

Better:

```text
Invalid data should be rejected while creating the request.
```

This makes downstream processor code safer.

---

# 11. Optional Class: `PaymentRequestValidator`

If you do not want to validate inside the constructor, you can create a validator class.

## Recommended Code

```java
public class PaymentRequestValidator {

    private PaymentRequestValidator() {
        // Utility class
    }

    public static void validatePaymentRequest(PaymentRequest paymentRequest) {
        if (paymentRequest == null) {
            throw new IllegalArgumentException("Payment request cannot be null.");
        }

        if (paymentRequest.getPaymentId() == null || paymentRequest.getPaymentId().isBlank()) {
            throw new IllegalArgumentException("Payment ID cannot be null or blank.");
        }

        if (paymentRequest.getCustomerId() == null || paymentRequest.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank.");
        }

        if (paymentRequest.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (paymentRequest.getCurrency() == null || paymentRequest.getCurrency().isBlank()) {
            throw new IllegalArgumentException("Currency cannot be null or blank.");
        }

        if (paymentRequest.getPaymentMode() == null) {
            throw new IllegalArgumentException("Payment mode cannot be null.");
        }

        if (paymentRequest.getDescription() == null || paymentRequest.getDescription().isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank.");
        }
    }
}
```

## Naming Note

Use PascalCase for class names.

Good:

```java
PaymentRequestValidator
```

Bad:

```java
validation
```

---

# 12. Class 3: `PaymentProcessor`

## Purpose

This is the common interface for all payment processors.

Every processor must implement this interface.

## Required Code

```java
public interface PaymentProcessor {
    void processPayment(PaymentRequest request);
}
```

## Why interface is needed

The client code should not care about the exact payment processor class.

It should be able to write:

```java
PaymentProcessor processor =
        PaymentProcessorFactory.getProcessor(request.getPaymentMode());

processor.processPayment(request);
```

The actual object may be:

```text
UpiPaymentProcessor
CardPaymentProcessor
NetBankingPaymentProcessor
WalletPaymentProcessor
```

but the client treats all of them as:

```java
PaymentProcessor
```

## Responsibility

```text
Define a common contract for all payment processors.
```

---

# 13. Class 4: `UpiPaymentProcessor`

## Purpose

This class handles UPI payments.

## Required Code

```java
public class UpiPaymentProcessor implements PaymentProcessor {

    @Override
    public void processPayment(PaymentRequest request) {
        System.out.println();
        System.out.println("=========================================================================");
        System.out.println("Processing UPI payment");
        System.out.println("Payment ID: " + request.getPaymentId());
        System.out.println("Customer ID: " + request.getCustomerId());
        System.out.println("Amount: " + request.getAmount() + " " + request.getCurrency());
        System.out.println("Description: " + request.getDescription());
        System.out.println("=========================================================================");
    }
}
```

## Responsibility

```text
Process UPI-style payments.
Print payment details.
```

## Real-World Equivalent

In a real backend, this class might:

```text
Validate VPA / UPI ID
Generate UPI collect request
Call UPI payment gateway
Handle callback/response
```

---

# 14. Class 5: `CardPaymentProcessor`

## Purpose

This class handles card payments.

## Required Code

```java
public class CardPaymentProcessor implements PaymentProcessor {

    @Override
    public void processPayment(PaymentRequest request) {
        System.out.println();
        System.out.println("=========================================================================");
        System.out.println("Processing Card payment");
        System.out.println("Payment ID: " + request.getPaymentId());
        System.out.println("Customer ID: " + request.getCustomerId());
        System.out.println("Amount: " + request.getAmount() + " " + request.getCurrency());
        System.out.println("Description: " + request.getDescription());
        System.out.println("=========================================================================");
    }
}
```

## Responsibility

```text
Process card-style payments.
Print payment details.
```

## Real-World Equivalent

In a real backend, this class might:

```text
Validate card token
Perform 3DS authentication
Call card gateway
Handle authorization/capture
```

---

# 15. Class 6: `NetBankingPaymentProcessor`

## Purpose

This class handles net banking payments.

## Required Code

```java
public class NetBankingPaymentProcessor implements PaymentProcessor {

    @Override
    public void processPayment(PaymentRequest request) {
        System.out.println();
        System.out.println("=========================================================================");
        System.out.println("Processing Net Banking payment");
        System.out.println("Payment ID: " + request.getPaymentId());
        System.out.println("Customer ID: " + request.getCustomerId());
        System.out.println("Amount: " + request.getAmount() + " " + request.getCurrency());
        System.out.println("Description: " + request.getDescription());
        System.out.println("=========================================================================");
    }
}
```

## Responsibility

```text
Process net banking-style payments.
Print payment details.
```

## Real-World Equivalent

In a real backend, this class might:

```text
Validate bank code
Create bank redirect URL
Handle bank authorization response
Confirm transaction status
```

---

# 16. Class 7: `WalletPaymentProcessor`

## Purpose

This class handles wallet payments.

## Required Code

```java
public class WalletPaymentProcessor implements PaymentProcessor {

    @Override
    public void processPayment(PaymentRequest request) {
        System.out.println();
        System.out.println("=========================================================================");
        System.out.println("Processing Wallet payment");
        System.out.println("Payment ID: " + request.getPaymentId());
        System.out.println("Customer ID: " + request.getCustomerId());
        System.out.println("Amount: " + request.getAmount() + " " + request.getCurrency());
        System.out.println("Description: " + request.getDescription());
        System.out.println("=========================================================================");
    }
}
```

## Responsibility

```text
Process wallet-style payments.
Print payment details.
```

## Real-World Equivalent

In a real backend, this class might:

```text
Check wallet balance
Debit wallet
Create ledger entry
Return transaction response
```

---

# 17. Class 8: `PaymentProcessorFactory`

## Purpose

This is the most important class in Module 2.

The factory creates the correct `PaymentProcessor` based on the given `PaymentMode`.

## Required Code

```java
public class PaymentProcessorFactory {

    private PaymentProcessorFactory() {
        // Utility class
    }

    public static PaymentProcessor getProcessor(PaymentMode paymentMode) {
        if (paymentMode == null) {
            throw new IllegalArgumentException("PaymentMode cannot be null");
        }

        return switch (paymentMode) {
            case UPI -> new UpiPaymentProcessor();
            case CARD -> new CardPaymentProcessor();
            case WALLET -> new WalletPaymentProcessor();
            case NET_BANKING -> new NetBankingPaymentProcessor();
        };
    }
}
```

## Responsibility

```text
Accept PaymentMode.
Create correct PaymentProcessor implementation.
Return it as PaymentProcessor interface.
Centralize processor creation logic.
```

## Mapping

| PaymentMode | Returned Object |
|---|---|
| `UPI` | `UpiPaymentProcessor` |
| `CARD` | `CardPaymentProcessor` |
| `NET_BANKING` | `NetBankingPaymentProcessor` |
| `WALLET` | `WalletPaymentProcessor` |

---

# 18. Why the Factory Method Returns Interface Type

The factory returns:

```java
PaymentProcessor
```

not:

```java
UpiPaymentProcessor
```

This is important.

## Good

```java
public static PaymentProcessor getProcessor(PaymentMode paymentMode)
```

## Bad

```java
public static Object getProcessor(PaymentMode paymentMode)
```

or:

```java
public static UpiPaymentProcessor getProcessor(PaymentMode paymentMode)
```

The caller should only know that it received a payment processor.

The caller should not care which exact processor it received.

---

# 19. Class 9: `Main`

## Purpose

`Main` should test all four payment modes.

## Required Code

```java
public class Main {

    public static void main(String[] args) {
        System.out.println("Factory Module 2 — Payment Processor Factory");

        PaymentRequest upiRequest = new PaymentRequest(
                "PAY-101",
                "CUST-101",
                500.0,
                "INR",
                PaymentMode.UPI,
                "Mobile recharge payment"
        );

        PaymentProcessor upiProcessor =
                PaymentProcessorFactory.getProcessor(upiRequest.getPaymentMode());
        upiProcessor.processPayment(upiRequest);

        PaymentRequest cardRequest = new PaymentRequest(
                "PAY-102",
                "CUST-102",
                2500.0,
                "INR",
                PaymentMode.CARD,
                "Online shopping payment"
        );

        PaymentProcessor cardProcessor =
                PaymentProcessorFactory.getProcessor(cardRequest.getPaymentMode());
        cardProcessor.processPayment(cardRequest);

        PaymentRequest netBankingRequest = new PaymentRequest(
                "PAY-103",
                "CUST-103",
                10000.0,
                "INR",
                PaymentMode.NET_BANKING,
                "Tuition fee payment"
        );

        PaymentProcessor netBankingProcessor =
                PaymentProcessorFactory.getProcessor(netBankingRequest.getPaymentMode());
        netBankingProcessor.processPayment(netBankingRequest);

        PaymentRequest walletRequest = new PaymentRequest(
                "PAY-104",
                "CUST-104",
                999.0,
                "INR",
                PaymentMode.WALLET,
                "Food delivery payment"
        );

        PaymentProcessor walletProcessor =
                PaymentProcessorFactory.getProcessor(walletRequest.getPaymentMode());
        walletProcessor.processPayment(walletRequest);
    }
}
```

## Responsibility

```text
Create payment requests.
Call the factory.
Receive PaymentProcessor.
Call processPayment().
Do not directly instantiate concrete processor classes.
```

---

# 20. Expected Output

Expected output style:

```text
Factory Module 2 — Payment Processor Factory

=========================================================================
Processing UPI payment
Payment ID: PAY-101
Customer ID: CUST-101
Amount: 500.0 INR
Description: Mobile recharge payment
=========================================================================

=========================================================================
Processing Card payment
Payment ID: PAY-102
Customer ID: CUST-102
Amount: 2500.0 INR
Description: Online shopping payment
=========================================================================

=========================================================================
Processing Net Banking payment
Payment ID: PAY-103
Customer ID: CUST-103
Amount: 10000.0 INR
Description: Tuition fee payment
=========================================================================

=========================================================================
Processing Wallet payment
Payment ID: PAY-104
Customer ID: CUST-104
Amount: 999.0 INR
Description: Food delivery payment
=========================================================================
```

---

# 21. Correct Execution Flow

For this line:

```java
PaymentProcessor upiProcessor =
        PaymentProcessorFactory.getProcessor(upiRequest.getPaymentMode());
```

Execution flow:

```text
1. Main creates PaymentRequest with PaymentMode.UPI.
2. Main calls upiRequest.getPaymentMode().
3. Main passes PaymentMode.UPI to PaymentProcessorFactory.
4. Factory receives PaymentMode.UPI.
5. Factory switch matches UPI.
6. Factory creates new UpiPaymentProcessor().
7. Factory returns it as PaymentProcessor.
8. Main calls upiProcessor.processPayment(upiRequest).
9. UpiPaymentProcessor.processPayment(...) executes.
```

The same flow applies for CARD, NET_BANKING, and WALLET.

---

# 22. What Makes This Factory Pattern?

The Factory Pattern exists in this module because object creation is moved into a separate factory class.

## Without Factory

```java
PaymentProcessor processor = new UpiPaymentProcessor();
```

The caller directly creates the concrete class.

## With Factory

```java
PaymentProcessor processor =
        PaymentProcessorFactory.getProcessor(PaymentMode.UPI);
```

The caller asks the factory.

The factory creates the concrete class.

The caller only receives the interface.

This is the main Factory Pattern idea.

---

# 23. What Problem This Module Solves

This module solves:

```text
Concrete payment processor creation spread across client code.
```

Instead of creating processors everywhere, creation is centralized.

## Before Factory

```text
Main knows about UpiPaymentProcessor.
Main knows about CardPaymentProcessor.
Main knows about NetBankingPaymentProcessor.
Main knows about WalletPaymentProcessor.
```

## After Factory

```text
Main knows only PaymentProcessorFactory.
Main knows only PaymentProcessor interface.
Factory knows concrete classes.
```

This is cleaner.

---

# 24. Design Benefits

## 24.1 Cleaner Client Code

Client code becomes:

```java
PaymentProcessor processor =
        PaymentProcessorFactory.getProcessor(request.getPaymentMode());

processor.processPayment(request);
```

It does not need a long switch or if-else chain.

## 24.2 Centralized Object Creation

All creation logic lives in one place:

```java
PaymentProcessorFactory
```

If object creation changes, update the factory.

## 24.3 Easy to Add New Payment Modes

Suppose later you add:

```java
CASH_ON_DELIVERY
```

You would add:

```java
public class CashOnDeliveryPaymentProcessor implements PaymentProcessor {
    @Override
    public void processPayment(PaymentRequest request) {
        System.out.println("Processing Cash On Delivery payment");
    }
}
```

Then update the enum:

```java
public enum PaymentMode {
    UPI,
    CARD,
    NET_BANKING,
    WALLET,
    CASH_ON_DELIVERY
}
```

And update the factory:

```java
case CASH_ON_DELIVERY -> new CashOnDeliveryPaymentProcessor();
```

The rest of the code still works with:

```java
PaymentProcessor
```

---

# 25. Important Rule

The factory should create objects.

The processor classes should process payments.

Do not put payment processing logic inside the factory.

## Wrong

```java
public class PaymentProcessorFactory {

    public static void processPayment(PaymentRequest request) {
        if (request.getPaymentMode() == PaymentMode.UPI) {
            System.out.println("Processing UPI payment");
        }
    }
}
```

## Correct

```java
PaymentProcessor processor =
        PaymentProcessorFactory.getProcessor(request.getPaymentMode());

processor.processPayment(request);
```

Factory creates.

Processor processes.

---

# 26. Common Mistakes

## Mistake 1: Creating Concrete Objects Directly in Main

Wrong:

```java
PaymentProcessor processor = new UpiPaymentProcessor();
```

Correct:

```java
PaymentProcessor processor =
        PaymentProcessorFactory.getProcessor(PaymentMode.UPI);
```

## Mistake 2: Factory Returning Object

Wrong:

```java
public static Object getProcessor(PaymentMode paymentMode)
```

Correct:

```java
public static PaymentProcessor getProcessor(PaymentMode paymentMode)
```

## Mistake 3: Using String Instead of Enum

Wrong:

```java
PaymentProcessorFactory.getProcessor("UPI");
```

Correct:

```java
PaymentProcessorFactory.getProcessor(PaymentMode.UPI);
```

## Mistake 4: Missing Null Handling

Wrong:

```java
public static PaymentProcessor getProcessor(PaymentMode paymentMode) {
    return switch (paymentMode) {
        case UPI -> new UpiPaymentProcessor();
        case CARD -> new CardPaymentProcessor();
        case WALLET -> new WalletPaymentProcessor();
        case NET_BANKING -> new NetBankingPaymentProcessor();
    };
}
```

Better:

```java
if (paymentMode == null) {
    throw new IllegalArgumentException("PaymentMode cannot be null");
}
```

## Mistake 5: Unnecessary Default in Enum Switch

If all enum values are handled, avoid unnecessary `default`.

Better:

```java
return switch (paymentMode) {
    case UPI -> new UpiPaymentProcessor();
    case CARD -> new CardPaymentProcessor();
    case WALLET -> new WalletPaymentProcessor();
    case NET_BANKING -> new NetBankingPaymentProcessor();
};
```

Why?

If a new enum value is added later, the compiler can warn that the switch is incomplete.

## Mistake 6: Typo in Class or Method Names

Avoid:

```java
Proccessor
getProccesor
```

Correct:

```java
Processor
getProcessor
```

Professional naming matters in backend code.

## Mistake 7: Wrong Output for Processor

If the wallet processor prints:

```text
Processing Card payment
```

that is wrong.

Wallet should print:

```text
Processing Wallet payment
```

Each concrete class should clearly represent its own behavior.

## Mistake 8: Throwing Wrong Exceptions

Avoid:

```java
throw new NullPointerException("Payment ID is null");
```

Use:

```java
throw new IllegalArgumentException("Payment ID cannot be null or blank.");
```

Why?

Because invalid input is an illegal argument.

## Mistake 9: Incorrect Main Method Signature

Use:

```java
public static void main(String[] args)
```

not:

```java
public static void main()
```

The standard Java entry point is:

```java
public static void main(String[] args)
```

---

# 27. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `PaymentMode` enum | 1 |
| Correct `PaymentRequest` class | 1.5 |
| Correct `PaymentProcessor` interface | 1 |
| Correct `UpiPaymentProcessor` implementation | 1 |
| Correct `CardPaymentProcessor` implementation | 1 |
| Correct `NetBankingPaymentProcessor` implementation | 1 |
| Correct `WalletPaymentProcessor` implementation | 1 |
| Correct `PaymentProcessorFactory` | 1.5 |
| Proper validation/null handling | 0.7 |
| Correct `Main` tests all processors | 0.8 |
| Code readability and naming | 0.5 |

Total: **10 marks**

---

# 28. Ideal Final Code Structure

Your package/project can look like this:

```text
factory/module2/
    PaymentMode.java
    PaymentRequest.java
    PaymentProcessor.java
    UpiPaymentProcessor.java
    CardPaymentProcessor.java
    NetBankingPaymentProcessor.java
    WalletPaymentProcessor.java
    PaymentProcessorFactory.java
    Main.java
```

Optional:

```text
factory/module2/
    PaymentRequestValidator.java
```

---

# 29. Difference Between Module 1 and Module 2

Module 1:

```text
NotificationType -> NotificationSender
```

Module 2:

```text
PaymentMode -> PaymentProcessor
```

But Module 2 is slightly more advanced because it introduces a request object:

```java
PaymentRequest
```

This is more realistic because backend services usually pass request objects instead of many loose parameters.

---

# 30. Final Learning Goal

By completing Module 2, you should understand:

```text
1. How Factory Pattern works with request objects.
2. How to select an implementation using an enum field inside a request.
3. Why factory should create objects but not perform business processing.
4. Why the factory should return an interface type.
5. How Factory Pattern cleans up service/client code.
6. How to validate request data in backend-style code.
```

The main mental model is:

```text
Request comes in.
Request has paymentMode.
Factory checks paymentMode.
Factory creates the correct PaymentProcessor.
Client calls processPayment(request).
```

For this module:

```text
UPI          -> UpiPaymentProcessor
CARD         -> CardPaymentProcessor
NET_BANKING  -> NetBankingPaymentProcessor
WALLET       -> WalletPaymentProcessor
```

That is Factory Pattern with a backend-style request object.
