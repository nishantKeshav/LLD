# Factory Design Pattern Practice — Module 5

## Logistics Partner Factory

### Difficulty Level

**Advanced**

### Pattern Focus

**Factory Pattern with full-request-based object selection, business rules, validation, service orchestration, cost calculation, and structured assignment result**

### Backend Theme

**E-commerce logistics partner assignment / order shipment routing system**

---

# 1. Module Overview

This is **Module 5** of the Factory Design Pattern practice series.

In earlier modules, the factory mostly selected an implementation using a single enum value:

```text
Module 1: NotificationType -> NotificationSender
Module 2: PaymentMode -> PaymentProcessor
Module 3: FileType -> DocumentParser
Module 4: ReportFormat -> ReportGenerator
```

Module 5 is different and more advanced.

Here, the factory should not select the implementation using only one enum value. Instead, it should inspect a full request object and apply business rules before returning the correct implementation.

The supported delivery types are:

```text
STANDARD
EXPRESS
SAME_DAY
INTERNATIONAL
```

The factory should select among these partner classes:

```text
STANDARD      -> StandardDeliveryPartner
EXPRESS       -> ExpressDeliveryPartner
SAME_DAY      -> SameDayDeliveryPartner
INTERNATIONAL -> InternationalDeliveryPartner
```

However, this selection depends on multiple fields:

```text
deliveryType
destinationCountry
distanceKm
weightKg
cashOnDelivery
```

So Module 5 teaches an important senior-level Factory Pattern idea:

```text
Factory Pattern can use business rules, not just direct enum-to-class mapping.
```

---

# 2. Why This Module Exists

In real e-commerce backend systems, assigning a logistics partner is not a simple enum mapping.

A customer may request a delivery type, but that delivery type may not always be allowed.

Examples:

```text
Same-day delivery cannot be used for long-distance orders.
Express delivery cannot be used for very heavy packages.
International delivery cannot support Cash on Delivery.
Standard delivery may only be available for domestic shipments.
```

So the backend must do two things:

```text
1. Validate whether the requested delivery type is allowed.
2. Select the correct logistics partner only if the business rules pass.
```

The factory is responsible for the selection part.

It should not blindly do this:

```java
case SAME_DAY -> new SameDayDeliveryPartner();
```

It should first check:

```text
Is the shipment domestic?
Is the distance within 50 km?
Is the package weight within 10 kg?
```

Only after these checks pass should it return:

```java
new SameDayDeliveryPartner();
```

---

# 3. Core Factory Pattern Idea in This Module

Factory Pattern is used when:

```text
You have multiple classes implementing the same interface,
and you need to create the correct implementation based on input.
```

In Module 5, the input is not just an enum.

The input is the full request:

```java
DeliveryRequest
```

The correct factory method should look like this:

```java
DeliveryPartner partner = LogisticsPartnerFactory.getPartner(request);
```

not this:

```java
DeliveryPartner partner = LogisticsPartnerFactory.getPartner(request.getDeliveryType());
```

Why?

Because partner selection depends on:

```text
deliveryType
destinationCountry
distanceKm
weightKg
cashOnDelivery
```

This is what makes Module 5 a stronger and more realistic Factory Pattern exercise.

---

# 4. Why Module 5 Is More Difficult Than Module 4

Module 4 was:

```text
ReportRequest -> ReportExportService -> ReportGeneratorFactory -> ReportGenerator -> ReportGenerationResult
```

The factory mostly selected based on:

```text
ReportFormat.PDF
ReportFormat.CSV
ReportFormat.EXCEL
ReportFormat.HTML
```

Module 5 becomes:

```text
DeliveryRequest -> DeliveryAssignmentService -> LogisticsPartnerFactory -> DeliveryPartner -> DeliveryAssignmentResult
```

Module 5 is harder because the factory must apply business rules:

```text
STANDARD must be domestic.
EXPRESS must be domestic and weight <= 30 kg.
SAME_DAY must be domestic, distance <= 50 km, and weight <= 10 kg.
INTERNATIONAL must be non-India and must not allow COD.
```

This makes the factory decision realistic.

---

# 5. Problem Without Factory Pattern

Without Factory Pattern, the service may become too large:

```java
public class DeliveryAssignmentService {

    public DeliveryAssignmentResult assignDeliveryPartner(DeliveryRequest request) {
        DeliveryPartner partner;

        if (request.getDeliveryType() == DeliveryType.STANDARD) {
            if (!"India".equalsIgnoreCase(request.getDestinationCountry())) {
                throw new IllegalArgumentException("Standard delivery is only domestic.");
            }
            partner = new StandardDeliveryPartner();

        } else if (request.getDeliveryType() == DeliveryType.EXPRESS) {
            if (!"India".equalsIgnoreCase(request.getDestinationCountry())) {
                throw new IllegalArgumentException("Express delivery is only domestic.");
            }
            if (request.getWeightKg() > 30) {
                throw new IllegalArgumentException("Express delivery supports maximum 30 kg.");
            }
            partner = new ExpressDeliveryPartner();

        } else if (request.getDeliveryType() == DeliveryType.SAME_DAY) {
            if (!"India".equalsIgnoreCase(request.getDestinationCountry())) {
                throw new IllegalArgumentException("Same-day delivery is only domestic.");
            }
            if (request.getDistanceKm() > 50) {
                throw new IllegalArgumentException("Same-day delivery supports maximum 50 km.");
            }
            if (request.getWeightKg() > 10) {
                throw new IllegalArgumentException("Same-day delivery supports maximum 10 kg.");
            }
            partner = new SameDayDeliveryPartner();

        } else if (request.getDeliveryType() == DeliveryType.INTERNATIONAL) {
            if ("India".equalsIgnoreCase(request.getDestinationCountry())) {
                throw new IllegalArgumentException("International delivery requires non-India destination.");
            }
            if (request.isCashOnDelivery()) {
                throw new IllegalArgumentException("COD is not allowed for international shipments.");
            }
            partner = new InternationalDeliveryPartner();

        } else {
            throw new IllegalArgumentException("Unsupported delivery type.");
        }

        return partner.assignDelivery(request);
    }
}
```

## Problems

```text
1. Service contains too much partner-selection logic.
2. Service knows every concrete partner class.
3. Business-rule checks are mixed with service orchestration.
4. If a new delivery type is added, the service becomes bigger.
5. The code becomes harder to test and maintain.
6. The service violates separation of concerns.
```

---

# 6. Problem With Factory Pattern

With Factory Pattern, the service becomes clean:

```java
public class DeliveryAssignmentService {

    public DeliveryAssignmentResult assignDeliveryPartner(DeliveryRequest request) {
        DeliveryRequestValidator.validate(request);

        DeliveryPartner partner =
                LogisticsPartnerFactory.getPartner(request);

        return partner.assignDelivery(request);
    }
}
```

Now responsibilities are separated:

```text
DeliveryAssignmentService
    -> coordinates the assignment flow

DeliveryRequestValidator
    -> validates basic request fields

LogisticsPartnerFactory
    -> applies business rules and selects the correct partner

DeliveryPartner implementations
    -> create the assignment result

DeliveryCostCalculator
    -> calculates estimated delivery cost
```

---

# 7. Main Objective

Build a logistics partner assignment system using the Factory Design Pattern.

The system should:

```text
1. Accept a DeliveryRequest.
2. Validate request fields.
3. Use LogisticsPartnerFactory to select the correct DeliveryPartner.
4. Apply delivery-type-specific business rules during partner selection.
5. Assign the shipment using the selected partner.
6. Calculate estimated delivery cost.
7. Return a structured DeliveryAssignmentResult.
8. Test all valid delivery types.
9. Test at least one invalid business-rule scenario.
10. Avoid direct partner object creation in Main or service.
```

---

# 8. Required Classes

Implement these:

```text
DeliveryType
DeliveryRequest
DeliveryAssignmentResult
DeliveryPartner
StandardDeliveryPartner
ExpressDeliveryPartner
SameDayDeliveryPartner
InternationalDeliveryPartner
LogisticsPartnerFactory
DeliveryRequestValidator
DeliveryCostCalculator
DeliveryAssignmentService
Main
```

Optional future improvement:

```text
TrackingIdGenerator
```

---

# 9. Class Design Overview

```text
DeliveryPartner
    ├── StandardDeliveryPartner
    ├── ExpressDeliveryPartner
    ├── SameDayDeliveryPartner
    └── InternationalDeliveryPartner

LogisticsPartnerFactory
    └── Applies business rules and creates correct DeliveryPartner

DeliveryRequest
    └── Holds shipment request data

DeliveryAssignmentResult
    └── Holds assignment result

DeliveryRequestValidator
    └── Validates request fields

DeliveryCostCalculator
    └── Calculates estimated delivery cost

DeliveryAssignmentService
    └── Coordinates the assignment flow

Main
    └── Tests valid and invalid scenarios
```

Runtime flow:

```text
Main
  -> DeliveryAssignmentService
      -> DeliveryRequestValidator
      -> LogisticsPartnerFactory
          -> Correct DeliveryPartner
              -> DeliveryCostCalculator
              -> DeliveryAssignmentResult
```

---

# 10. Class 1: DeliveryType

```java
public enum DeliveryType {
    STANDARD,
    EXPRESS,
    SAME_DAY,
    INTERNATIONAL
}
```

Use enum values instead of raw strings such as:

```text
"standard"
"same day"
"international-delivery"
```

Enums prevent typo-based bugs and make switch expressions safer.

---

# 11. Class 2: DeliveryRequest

`DeliveryRequest` represents one delivery assignment request.

Required fields:

```text
requestId
orderId
customerId
sourceCity
destinationCity
destinationCountry
deliveryType
distanceKm
weightKg
packageValue
fragile
cashOnDelivery
createdAt
```

Suggested code:

```java
import java.time.LocalDateTime;

public class DeliveryRequest {

    private final String requestId;
    private final String orderId;
    private final String customerId;
    private final String sourceCity;
    private final String destinationCity;
    private final String destinationCountry;
    private final DeliveryType deliveryType;
    private final double distanceKm;
    private final double weightKg;
    private final double packageValue;
    private final boolean fragile;
    private final boolean cashOnDelivery;
    private final LocalDateTime createdAt;

    public DeliveryRequest(
            String requestId,
            String orderId,
            String customerId,
            String sourceCity,
            String destinationCity,
            String destinationCountry,
            DeliveryType deliveryType,
            double distanceKm,
            double weightKg,
            double packageValue,
            boolean fragile,
            boolean cashOnDelivery
    ) {
        this.requestId = requestId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.destinationCountry = destinationCountry;
        this.deliveryType = deliveryType;
        this.distanceKm = distanceKm;
        this.weightKg = weightKg;
        this.packageValue = packageValue;
        this.fragile = fragile;
        this.cashOnDelivery = cashOnDelivery;
        this.createdAt = LocalDateTime.now();
    }

    public String getRequestId() {
        return requestId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public double getPackageValue() {
        return packageValue;
    }

    public boolean isFragile() {
        return fragile;
    }

    public boolean isCashOnDelivery() {
        return cashOnDelivery;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
```

Design note:

You may validate inside the constructor, but for Module 5, a separate `DeliveryRequestValidator` is preferred because it separates:

```text
data object
validation logic
business-rule factory logic
```

---

# 12. Class 3: DeliveryAssignmentResult

`DeliveryAssignmentResult` represents the output after a logistics partner has been assigned.

Required fields:

```text
requestId
orderId
customerId
deliveryType
partnerName
assigned
message
estimatedDeliveryDays
estimatedCost
trackingId
```

Suggested code:

```java
public class DeliveryAssignmentResult {

    private final String requestId;
    private final String orderId;
    private final String customerId;
    private final DeliveryType deliveryType;
    private final String partnerName;
    private final boolean assigned;
    private final String message;
    private final int estimatedDeliveryDays;
    private final double estimatedCost;
    private final String trackingId;

    public DeliveryAssignmentResult(
            String requestId,
            String orderId,
            String customerId,
            DeliveryType deliveryType,
            String partnerName,
            boolean assigned,
            String message,
            int estimatedDeliveryDays,
            double estimatedCost,
            String trackingId
    ) {
        this.requestId = requestId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.deliveryType = deliveryType;
        this.partnerName = partnerName;
        this.assigned = assigned;
        this.message = message;
        this.estimatedDeliveryDays = estimatedDeliveryDays;
        this.estimatedCost = estimatedCost;
        this.trackingId = trackingId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public String getMessage() {
        return message;
    }

    public int getEstimatedDeliveryDays() {
        return estimatedDeliveryDays;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public String getTrackingId() {
        return trackingId;
    }
}
```

---

# 13. Class 4: DeliveryPartner

Common interface for all logistics partners:

```java
public interface DeliveryPartner {
    DeliveryAssignmentResult assignDelivery(DeliveryRequest request);
    String getPartnerName();
}
```

The service should work with:

```java
DeliveryPartner partner = LogisticsPartnerFactory.getPartner(request);
```

not concrete classes directly.

---

# 14. Class 5: DeliveryRequestValidator

This class validates basic request fields.

It should not contain delivery-type-specific business rules. Business rules belong in the factory.

Required validation:

```text
request cannot be null
requestId cannot be null/blank
orderId cannot be null/blank
customerId cannot be null/blank
sourceCity cannot be null/blank
destinationCity cannot be null/blank
destinationCountry cannot be null/blank
deliveryType cannot be null
distanceKm must be greater than 0
weightKg must be greater than 0
packageValue must be greater than or equal to 0
```

Suggested code:

```java
public class DeliveryRequestValidator {

    private DeliveryRequestValidator() {
        // Utility class
    }

    public static void validate(DeliveryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Delivery request cannot be null.");
        }

        if (request.getRequestId() == null || request.getRequestId().isBlank()) {
            throw new IllegalArgumentException("Request ID cannot be null or blank.");
        }

        if (request.getOrderId() == null || request.getOrderId().isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null or blank.");
        }

        if (request.getCustomerId() == null || request.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank.");
        }

        if (request.getSourceCity() == null || request.getSourceCity().isBlank()) {
            throw new IllegalArgumentException("Source city cannot be null or blank.");
        }

        if (request.getDestinationCity() == null || request.getDestinationCity().isBlank()) {
            throw new IllegalArgumentException("Destination city cannot be null or blank.");
        }

        if (request.getDestinationCountry() == null || request.getDestinationCountry().isBlank()) {
            throw new IllegalArgumentException("Destination country cannot be null or blank.");
        }

        if (request.getDeliveryType() == null) {
            throw new IllegalArgumentException("Delivery type cannot be null.");
        }

        if (request.getDistanceKm() <= 0) {
            throw new IllegalArgumentException("Distance must be greater than zero.");
        }

        if (request.getWeightKg() <= 0) {
            throw new IllegalArgumentException("Weight must be greater than zero.");
        }

        if (request.getPackageValue() < 0) {
            throw new IllegalArgumentException("Package value cannot be negative.");
        }
    }
}
```

---

# 15. Validation vs Business Rules

Module 5 has two different kinds of checks.

## Basic request validation

This checks whether the request fields are structurally valid.

Examples:

```text
requestId is not blank
distanceKm > 0
weightKg > 0
deliveryType is not null
packageValue is not negative
```

This belongs in:

```java
DeliveryRequestValidator
```

## Business-rule validation

This checks whether the requested delivery type is allowed for the shipment.

Examples:

```text
SAME_DAY distance <= 50
SAME_DAY weight <= 10
EXPRESS weight <= 30
INTERNATIONAL COD is not allowed
STANDARD must be domestic
```

This belongs in:

```java
LogisticsPartnerFactory
```

---

# 16. Class 6: DeliveryCostCalculator

This utility calculates estimated delivery cost.

Cost rules:

```text
STANDARD:
Base cost = 50
Per km = 2
Per kg = 10

EXPRESS:
Base cost = 100
Per km = 4
Per kg = 15

SAME_DAY:
Base cost = 150
Per km = 6
Per kg = 20

INTERNATIONAL:
Base cost = 500
Per km = 8
Per kg = 50
```

Surcharges:

```text
Fragile package surcharge = 100
Cash on Delivery surcharge = 40
High-value package surcharge = 1% of packageValue if packageValue > 10000
```

Suggested code:

```java
public class DeliveryCostCalculator {

    private static final double PACKAGE_VALUE_THRESHOLD = 10000;

    private DeliveryCostCalculator() {
        // Utility class
    }

    public static double calculateCost(DeliveryRequest request) {
        double baseCost;
        double perKm;
        double perKg;

        switch (request.getDeliveryType()) {
            case STANDARD -> {
                baseCost = 50;
                perKm = 2;
                perKg = 10;
            }
            case EXPRESS -> {
                baseCost = 100;
                perKm = 4;
                perKg = 15;
            }
            case SAME_DAY -> {
                baseCost = 150;
                perKm = 6;
                perKg = 20;
            }
            case INTERNATIONAL -> {
                baseCost = 500;
                perKm = 8;
                perKg = 50;
            }
            default -> throw new IllegalArgumentException("Unsupported delivery type.");
        }

        double cost = baseCost
                + (request.getDistanceKm() * perKm)
                + (request.getWeightKg() * perKg);

        if (request.isFragile()) {
            cost += 100;
        }

        if (request.isCashOnDelivery()) {
            cost += 40;
        }

        if (request.getPackageValue() > PACKAGE_VALUE_THRESHOLD) {
            cost += request.getPackageValue() * 0.01;
        }

        return cost;
    }
}
```

If your Java version supports exhaustive enum switches clearly, you can remove the `default`.

---

# 17. Partner Classes

Each partner implements `DeliveryPartner`.

Each partner should:

```text
calculate estimated cost
return DeliveryAssignmentResult
include partner name
include tracking ID
include estimated delivery days
```

## StandardDeliveryPartner

Behavior:

```text
Estimated delivery days: 5
Allowed only for domestic deliveries
Can handle COD
Can handle fragile packages
Can handle normal/heavy packages
```

```java
import java.util.UUID;

public class StandardDeliveryPartner implements DeliveryPartner {

    private static final int ESTIMATED_DELIVERY_DAYS = 5;

    @Override
    public DeliveryAssignmentResult assignDelivery(DeliveryRequest request) {
        double estimatedCost = DeliveryCostCalculator.calculateCost(request);

        return new DeliveryAssignmentResult(
                request.getRequestId(),
                request.getOrderId(),
                request.getCustomerId(),
                request.getDeliveryType(),
                getPartnerName(),
                true,
                "Standard delivery partner assigned successfully",
                ESTIMATED_DELIVERY_DAYS,
                estimatedCost,
                "STD-" + request.getOrderId() + "-" + UUID.randomUUID()
        );
    }

    @Override
    public String getPartnerName() {
        return this.getClass().getSimpleName();
    }
}
```

## ExpressDeliveryPartner

Behavior:

```text
Estimated delivery days: 2
Allowed only for domestic deliveries
Maximum weight allowed: 30 kg
Can handle COD
Can handle fragile packages
```

```java
import java.util.UUID;

public class ExpressDeliveryPartner implements DeliveryPartner {

    private static final int ESTIMATED_DELIVERY_DAYS = 2;

    @Override
    public DeliveryAssignmentResult assignDelivery(DeliveryRequest request) {
        double estimatedCost = DeliveryCostCalculator.calculateCost(request);

        return new DeliveryAssignmentResult(
                request.getRequestId(),
                request.getOrderId(),
                request.getCustomerId(),
                request.getDeliveryType(),
                getPartnerName(),
                true,
                "Express delivery partner assigned successfully",
                ESTIMATED_DELIVERY_DAYS,
                estimatedCost,
                "EXP-" + request.getOrderId() + "-" + UUID.randomUUID()
        );
    }

    @Override
    public String getPartnerName() {
        return this.getClass().getSimpleName();
    }
}
```

## SameDayDeliveryPartner

Behavior:

```text
Estimated delivery days: 1
Allowed only for domestic deliveries
Maximum distance allowed: 50 km
Maximum weight allowed: 10 kg
Can handle COD
Can handle fragile packages
```

```java
import java.util.UUID;

public class SameDayDeliveryPartner implements DeliveryPartner {

    private static final int ESTIMATED_DELIVERY_DAYS = 1;

    @Override
    public DeliveryAssignmentResult assignDelivery(DeliveryRequest request) {
        double estimatedCost = DeliveryCostCalculator.calculateCost(request);

        return new DeliveryAssignmentResult(
                request.getRequestId(),
                request.getOrderId(),
                request.getCustomerId(),
                request.getDeliveryType(),
                getPartnerName(),
                true,
                "Same-day delivery partner assigned successfully",
                ESTIMATED_DELIVERY_DAYS,
                estimatedCost,
                "SDD-" + request.getOrderId() + "-" + UUID.randomUUID()
        );
    }

    @Override
    public String getPartnerName() {
        return this.getClass().getSimpleName();
    }
}
```

## InternationalDeliveryPartner

Behavior:

```text
Estimated delivery days: 10
Allowed only for non-India destinations
Cash on Delivery is not allowed
Can handle fragile packages
Can handle heavy packages
```

```java
import java.util.UUID;

public class InternationalDeliveryPartner implements DeliveryPartner {

    private static final int ESTIMATED_DELIVERY_DAYS = 10;

    @Override
    public DeliveryAssignmentResult assignDelivery(DeliveryRequest request) {
        double estimatedCost = DeliveryCostCalculator.calculateCost(request);

        return new DeliveryAssignmentResult(
                request.getRequestId(),
                request.getOrderId(),
                request.getCustomerId(),
                request.getDeliveryType(),
                getPartnerName(),
                true,
                "International delivery partner assigned successfully",
                ESTIMATED_DELIVERY_DAYS,
                estimatedCost,
                "INT-" + request.getOrderId() + "-" + UUID.randomUUID()
        );
    }

    @Override
    public String getPartnerName() {
        return this.getClass().getSimpleName();
    }
}
```

---

# 18. Class 11: LogisticsPartnerFactory

This is the most important class in Module 5.

The factory should accept the full request:

```java
public static DeliveryPartner getPartner(DeliveryRequest request)
```

not only:

```java
public static DeliveryPartner getPartner(DeliveryType deliveryType)
```

## Business Rules

### Standard Delivery

```text
STANDARD is only for domestic deliveries.
```

If destination country is not India, reject.

### Express Delivery

```text
EXPRESS is only for domestic deliveries.
EXPRESS maximum weight = 30 kg.
```

If weight is more than 30 kg, reject.

### Same-Day Delivery

```text
SAME_DAY is only for domestic deliveries.
SAME_DAY maximum distance = 50 km.
SAME_DAY maximum weight = 10 kg.
```

If distance is more than 50 km, reject.

If weight is more than 10 kg, reject.

### International Delivery

```text
INTERNATIONAL is only for non-India destinations.
COD is not allowed for INTERNATIONAL.
```

If destination country is India, reject.

If `cashOnDelivery = true`, reject.

## Suggested Implementation

```java
public class LogisticsPartnerFactory {

    private static final String INDIA = "India";

    private LogisticsPartnerFactory() {
        // Utility class
    }

    public static DeliveryPartner getPartner(DeliveryRequest request) {
        DeliveryRequestValidator.validate(request);

        return switch (request.getDeliveryType()) {
            case STANDARD -> createStandardPartner(request);
            case EXPRESS -> createExpressPartner(request);
            case SAME_DAY -> createSameDayPartner(request);
            case INTERNATIONAL -> createInternationalPartner(request);
        };
    }

    private static DeliveryPartner createStandardPartner(DeliveryRequest request) {
        if (!isDomestic(request)) {
            throw new IllegalArgumentException(
                    "Standard delivery is only available for domestic shipments."
            );
        }

        return new StandardDeliveryPartner();
    }

    private static DeliveryPartner createExpressPartner(DeliveryRequest request) {
        if (!isDomestic(request)) {
            throw new IllegalArgumentException(
                    "Express delivery is only available for domestic shipments."
            );
        }

        if (request.getWeightKg() > 30) {
            throw new IllegalArgumentException(
                    "Express delivery supports maximum weight of 30 kg."
            );
        }

        return new ExpressDeliveryPartner();
    }

    private static DeliveryPartner createSameDayPartner(DeliveryRequest request) {
        if (!isDomestic(request)) {
            throw new IllegalArgumentException(
                    "Same-day delivery is only available for domestic shipments."
            );
        }

        if (request.getDistanceKm() > 50) {
            throw new IllegalArgumentException(
                    "Same-day delivery supports maximum distance of 50 km."
            );
        }

        if (request.getWeightKg() > 10) {
            throw new IllegalArgumentException(
                    "Same-day delivery supports maximum weight of 10 kg."
            );
        }

        return new SameDayDeliveryPartner();
    }

    private static DeliveryPartner createInternationalPartner(DeliveryRequest request) {
        if (isDomestic(request)) {
            throw new IllegalArgumentException(
                    "International delivery requires a non-India destination country."
            );
        }

        if (request.isCashOnDelivery()) {
            throw new IllegalArgumentException(
                    "Cash on Delivery is not available for international shipments."
            );
        }

        return new InternationalDeliveryPartner();
    }

    private static boolean isDomestic(DeliveryRequest request) {
        return INDIA.equalsIgnoreCase(request.getDestinationCountry().trim());
    }
}
```

## Important Factory Rule

The factory should select the partner.

The factory should not create the full assignment result.

Correct:

```java
DeliveryPartner partner = LogisticsPartnerFactory.getPartner(request);
return partner.assignDelivery(request);
```

Wrong:

```java
DeliveryAssignmentResult result = LogisticsPartnerFactory.assignDelivery(request);
```

---

# 19. Class 12: DeliveryAssignmentService

This service coordinates the flow.

```java
public class DeliveryAssignmentService {

    public DeliveryAssignmentResult assignDeliveryPartner(DeliveryRequest request) {
        DeliveryRequestValidator.validate(request);

        DeliveryPartner partner =
                LogisticsPartnerFactory.getPartner(request);

        return partner.assignDelivery(request);
    }
}
```

Responsibilities:

```text
Validate request.
Ask factory for partner.
Call selected partner.
Return DeliveryAssignmentResult.
```

The service should not directly create concrete partners.

---

# 20. Main

`Main` should test:

```text
STANDARD valid request
EXPRESS valid request
SAME_DAY valid request
INTERNATIONAL valid request
At least one invalid request
```

Suggested test setup:

```java
public class Main {

    public static void main(String[] args) {
        System.out.println("Factory Module 5 — Logistics Partner Factory");

        DeliveryAssignmentService service = new DeliveryAssignmentService();

        DeliveryRequest standardRequest = new DeliveryRequest(
                "REQ-101",
                "ORD-101",
                "CUST-101",
                "Mumbai",
                "Pune",
                "India",
                DeliveryType.STANDARD,
                150,
                8,
                5000,
                false,
                true
        );

        printResult(service.assignDeliveryPartner(standardRequest));

        DeliveryRequest expressRequest = new DeliveryRequest(
                "REQ-102",
                "ORD-102",
                "CUST-102",
                "Mumbai",
                "Bangalore",
                "India",
                DeliveryType.EXPRESS,
                980,
                12,
                15000,
                true,
                false
        );

        printResult(service.assignDeliveryPartner(expressRequest));

        DeliveryRequest sameDayRequest = new DeliveryRequest(
                "REQ-103",
                "ORD-103",
                "CUST-103",
                "Mumbai",
                "Thane",
                "India",
                DeliveryType.SAME_DAY,
                25,
                3,
                2000,
                false,
                true
        );

        printResult(service.assignDeliveryPartner(sameDayRequest));

        DeliveryRequest internationalRequest = new DeliveryRequest(
                "REQ-104",
                "ORD-104",
                "CUST-104",
                "Mumbai",
                "New York",
                "USA",
                DeliveryType.INTERNATIONAL,
                12500,
                20,
                50000,
                true,
                false
        );

        printResult(service.assignDeliveryPartner(internationalRequest));

        try {
            DeliveryRequest invalidSameDayRequest = new DeliveryRequest(
                    "REQ-105",
                    "ORD-105",
                    "CUST-105",
                    "Mumbai",
                    "Nashik",
                    "India",
                    DeliveryType.SAME_DAY,
                    180,
                    5,
                    3000,
                    false,
                    true
            );

            printResult(service.assignDeliveryPartner(invalidSameDayRequest));

        } catch (IllegalArgumentException exception) {
            System.out.println();
            System.out.println("Invalid delivery request rejected");
            System.out.println("Reason: " + exception.getMessage());
        }
    }

    private static void printResult(DeliveryAssignmentResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Request ID: " + result.getRequestId());
        System.out.println("Order ID: " + result.getOrderId());
        System.out.println("Customer ID: " + result.getCustomerId());
        System.out.println("Delivery Type: " + result.getDeliveryType());
        System.out.println("Partner Name: " + result.getPartnerName());
        System.out.println("Assigned: " + result.isAssigned());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Estimated Delivery Days: " + result.getEstimatedDeliveryDays());
        System.out.println("Estimated Cost: " + result.getEstimatedCost());
        System.out.println("Tracking ID: " + result.getTrackingId());
        System.out.println("==================================================");
    }
}
```

---

# 21. Expected Output Style

```text
Factory Module 5 — Logistics Partner Factory

==================================================
Request ID: REQ-101
Order ID: ORD-101
Customer ID: CUST-101
Delivery Type: STANDARD
Partner Name: StandardDeliveryPartner
Assigned: true
Message: Standard delivery partner assigned successfully
Estimated Delivery Days: 5
Estimated Cost: 470.0
Tracking ID: STD-ORD-101-<uuid>
==================================================

==================================================
Request ID: REQ-102
Order ID: ORD-102
Customer ID: CUST-102
Delivery Type: EXPRESS
Partner Name: ExpressDeliveryPartner
Assigned: true
Message: Express delivery partner assigned successfully
Estimated Delivery Days: 2
Estimated Cost: 4450.0
Tracking ID: EXP-ORD-102-<uuid>
==================================================

==================================================
Request ID: REQ-103
Order ID: ORD-103
Customer ID: CUST-103
Delivery Type: SAME_DAY
Partner Name: SameDayDeliveryPartner
Assigned: true
Message: Same-day delivery partner assigned successfully
Estimated Delivery Days: 1
Estimated Cost: 400.0
Tracking ID: SDD-ORD-103-<uuid>
==================================================

==================================================
Request ID: REQ-104
Order ID: ORD-104
Customer ID: CUST-104
Delivery Type: INTERNATIONAL
Partner Name: InternationalDeliveryPartner
Assigned: true
Message: International delivery partner assigned successfully
Estimated Delivery Days: 10
Estimated Cost: 102100.0
Tracking ID: INT-ORD-104-<uuid>
==================================================

Invalid delivery request rejected
Reason: Same-day delivery supports maximum distance of 50 km.
```

---

# 22. Cost Calculation Examples

## Standard Delivery

Input:

```text
distanceKm = 150
weightKg = 8
packageValue = 5000
fragile = false
cashOnDelivery = true
```

Calculation:

```text
Base cost = 50
Distance cost = 150 * 2 = 300
Weight cost = 8 * 10 = 80
COD surcharge = 40

Total = 470
```

## Express Delivery

Input:

```text
distanceKm = 980
weightKg = 12
packageValue = 15000
fragile = true
cashOnDelivery = false
```

Calculation:

```text
Base cost = 100
Distance cost = 980 * 4 = 3920
Weight cost = 12 * 15 = 180
Fragile surcharge = 100
High-value surcharge = 15000 * 0.01 = 150

Total = 4450
```

## Same-Day Delivery

Input:

```text
distanceKm = 25
weightKg = 3
packageValue = 2000
fragile = false
cashOnDelivery = true
```

Calculation:

```text
Base cost = 150
Distance cost = 25 * 6 = 150
Weight cost = 3 * 20 = 60
COD surcharge = 40

Total = 400
```

## International Delivery

Input:

```text
distanceKm = 12500
weightKg = 20
packageValue = 50000
fragile = true
cashOnDelivery = false
```

Calculation:

```text
Base cost = 500
Distance cost = 12500 * 8 = 100000
Weight cost = 20 * 50 = 1000
Fragile surcharge = 100
High-value surcharge = 50000 * 0.01 = 500

Total = 102100
```

---

# 23. Correct Execution Flow

For this line:

```java
DeliveryAssignmentResult result =
        service.assignDeliveryPartner(sameDayRequest);
```

Execution flow:

```text
1. Main creates DeliveryRequest.
2. Main calls DeliveryAssignmentService.assignDeliveryPartner(request).
3. DeliveryAssignmentService validates the request.
4. DeliveryAssignmentService calls LogisticsPartnerFactory.getPartner(request).
5. LogisticsPartnerFactory checks delivery type.
6. Factory sees SAME_DAY.
7. Factory checks domestic shipment.
8. Factory checks distance <= 50 km.
9. Factory checks weight <= 10 kg.
10. Factory returns SameDayDeliveryPartner.
11. Service calls partner.assignDelivery(request).
12. SameDayDeliveryPartner calculates cost.
13. SameDayDeliveryPartner creates DeliveryAssignmentResult.
14. Main prints the result.
```

---

# 24. What Makes This Factory Pattern?

The Factory Pattern exists in this module because object creation is moved into a separate factory class.

Without Factory:

```java
DeliveryPartner partner = new SameDayDeliveryPartner();
```

With Factory:

```java
DeliveryPartner partner =
        LogisticsPartnerFactory.getPartner(request);
```

The service asks the factory.

The factory applies rules and creates the correct concrete partner.

The service receives only the interface:

```java
DeliveryPartner
```

---

# 25. Design Benefits

```text
1. Cleaner service code.
2. Centralized partner-selection logic.
3. Business rules are easier to maintain.
4. Adding a new delivery type is easier.
5. Service depends on DeliveryPartner interface, not concrete classes.
6. Cost calculation is centralized in DeliveryCostCalculator.
7. Invalid business scenarios are explicitly testable.
```

---

# 26. Common Mistakes

```text
1. Factory accepting only DeliveryType instead of DeliveryRequest.
2. Blind enum-to-class mapping without business rules.
3. Putting full assignment result creation inside the factory.
4. Missing invalid scenario test.
5. Allowing international COD.
6. Allowing same-day delivery for long distances.
7. Allowing express delivery for overweight packages.
8. Not trimming country input before comparison.
9. Missing private constructor in factory.
10. Non-standard constant names.
11. Creating concrete partners directly in Main or service.
12. Returning Object from factory instead of DeliveryPartner.
```

---

# 27. Scoring Rubric

| Area | Marks |
|---|---:|
| Correct `DeliveryType` enum | 1 |
| Correct `DeliveryRequest` class | 1 |
| Correct `DeliveryAssignmentResult` class | 1 |
| Correct `DeliveryPartner` interface | 1 |
| Correct request validation | 1.2 |
| Correct `DeliveryCostCalculator` | 1.2 |
| Correct `StandardDeliveryPartner` | 1 |
| Correct `ExpressDeliveryPartner` | 1 |
| Correct `SameDayDeliveryPartner` | 1 |
| Correct `InternationalDeliveryPartner` | 1 |
| Correct business-rule-based factory | 2 |
| Correct service layer | 1 |
| Main tests all valid delivery types | 1 |
| Main tests invalid scenario | 1 |
| Code readability and naming | 0.8 |

Total: **15.2 marks normalized to 10**

---

# 28. Ideal Final Code Structure

```text
factory/module5/
    DeliveryType.java
    DeliveryRequest.java
    DeliveryAssignmentResult.java
    DeliveryPartner.java
    StandardDeliveryPartner.java
    ExpressDeliveryPartner.java
    SameDayDeliveryPartner.java
    InternationalDeliveryPartner.java
    LogisticsPartnerFactory.java
    DeliveryRequestValidator.java
    DeliveryCostCalculator.java
    DeliveryAssignmentService.java
    Main.java
```

Optional:

```text
TrackingIdGenerator.java
```

---

# 29. Difference Between Module 4 and Module 5

Module 4:

```text
ReportFormat -> ReportGenerator
```

Factory selection was mostly direct.

Module 5:

```text
DeliveryRequest -> LogisticsPartnerFactory -> DeliveryPartner
```

Factory selection is business-rule-based.

Module 5 is more advanced because it adds:

```text
full request object selection
domestic vs international checks
distance limits
weight limits
COD restrictions
cost calculation
tracking ID generation
invalid scenario testing
business-rule-based factory methods
```

---

# 30. Final Learning Goal

By completing Module 5, you should understand:

```text
1. Factory Pattern can use a full request object, not only an enum.
2. Factory selection can include business rules.
3. Request validation and business-rule validation are different.
4. The factory should select objects, not perform the full operation.
5. Services should coordinate the flow.
6. Concrete implementations should perform assignment behavior.
7. Utility classes can centralize repeated calculations.
8. Invalid scenarios are essential for testing business logic.
```

The key mental model is:

```text
DeliveryRequest comes in.
DeliveryAssignmentService validates it.
LogisticsPartnerFactory checks business rules.
Factory returns the correct DeliveryPartner.
Partner assigns shipment.
Partner returns DeliveryAssignmentResult.
```

For this module:

```text
STANDARD      -> StandardDeliveryPartner
EXPRESS       -> ExpressDeliveryPartner
SAME_DAY      -> SameDayDeliveryPartner
INTERNATIONAL -> InternationalDeliveryPartner
```

That is Factory Pattern with realistic logistics partner routing logic.
