# Factory Design Pattern Practice — Module 7

## Configurable Integration Client Factory

### Difficulty Level

**Final Boss / Advanced++**

### Pattern Focus

**Registry-based Factory Pattern + tenant-specific configuration + provider capability matching + provider health checks + fallback routing + structured success/failure response modeling**

### Backend Theme

**External payment/integration provider selection system**

---

# 1. Module Overview

This is **Module 7** of the Factory Design Pattern practice series.

It is intended to be the hardest Factory Pattern module in this sequence.

Earlier modules were simpler:

```text
Module 1: NotificationType -> NotificationSender
Module 2: PaymentMode -> PaymentProcessor
Module 3: FileType -> DocumentParser
Module 4: ReportFormat -> ReportGenerator
Module 5: DeliveryRequest -> LogisticsPartnerFactory -> DeliveryPartner
Module 6: NotificationRequest -> RoutingService -> ChannelFactory -> NotificationChannel
```

Module 7 builds on everything from the previous modules.

In this module, you are building a backend system that chooses the correct external integration provider for a request.

The system supports providers such as:

```text
RAZORPAY
STRIPE
PAYPAL
CASHFREE
```

The system supports operations such as:

```text
PAYMENT
REFUND
PAYOUT
SUBSCRIPTION
```

The system must decide which provider client should be used for each request.

The decision depends on:

```text
tenant configuration
enabled providers
provider credentials
provider health
supported operation
supported currency
supported region
preferred provider
fallback providers
```

This is no longer a simple factory like:

```java
case RAZORPAY -> new RazorpayIntegrationClient();
```

Instead, the design should be:

```text
IntegrationRequest
    -> IntegrationExecutionService
        -> IntegrationClientSelectionService
            -> TenantIntegrationConfigService
            -> ProviderHealthService
            -> IntegrationClientFactory
                -> IntegrationClientRegistry
                    -> IntegrationClient
                        -> IntegrationResponse
```

The key learning goal is to understand how Factory Pattern fits into a real backend orchestration flow.


---

# 2. Why This Module Exists

In real backend systems, you often integrate with multiple external providers.

For example:

```text
Payment providers
SMS providers
Email providers
KYC providers
Banking providers
Webhook providers
Document verification providers
Fraud detection providers
```

For payments, your system may integrate with providers like:

```text
Razorpay
Stripe
PayPal
Cashfree
```

But provider selection is not always simple.

A tenant may have only some providers enabled.

Example:

```text
TENANT-A -> RAZORPAY, CASHFREE
TENANT-B -> STRIPE, PAYPAL
```

A provider may only support some operations.

Example:

```text
RAZORPAY -> PAYMENT, REFUND, PAYOUT
STRIPE   -> PAYMENT, REFUND, SUBSCRIPTION
PAYPAL   -> PAYMENT, REFUND
CASHFREE -> PAYMENT, PAYOUT
```

A provider may only support some currencies.

Example:

```text
RAZORPAY -> INR
CASHFREE -> INR
STRIPE   -> USD, EUR
PAYPAL   -> USD, EUR
```

A provider may only support some regions.

Example:

```text
RAZORPAY -> INDIA
CASHFREE -> INDIA
STRIPE   -> US, EUROPE
PAYPAL   -> US, EUROPE
```

A provider may also be temporarily unhealthy.

Example:

```text
PAYPAL is configured for TENANT-B.
PAYPAL supports USD payments.
PAYPAL supports US region.
But PAYPAL health is false.
So PAYPAL should be skipped.
```

This means the backend must make a smart provider selection decision.

That decision should not be placed directly inside `Main`.

It should not be placed inside the factory.

It should be placed inside a dedicated selection service.


---

# 3. Core Factory Pattern Idea in Module 7

Factory Pattern is used to obtain the correct implementation of a common interface.

In this module, all provider clients implement:

```java
IntegrationClient
```

Concrete implementations include:

```text
RazorpayIntegrationClient
StripeIntegrationClient
PaypalIntegrationClient
CashfreeIntegrationClient
```

The factory is:

```java
IntegrationClientFactory
```

But the factory should not use switch.

Wrong:

```java
public IntegrationClient getClient(IntegrationProvider provider) {
    return switch (provider) {
        case RAZORPAY -> new RazorpayIntegrationClient();
        case STRIPE -> new StripeIntegrationClient();
        case PAYPAL -> new PaypalIntegrationClient();
        case CASHFREE -> new CashfreeIntegrationClient();
    };
}
```

Correct:

```java
public IntegrationClient getClient(IntegrationProvider provider) {
    return registry.get(provider);
}
```

The registry stores clients:

```java
Map<IntegrationProvider, IntegrationClient>
```

So the actual Factory Pattern flow is:

```text
IntegrationProvider
    -> IntegrationClientFactory
        -> IntegrationClientRegistry
            -> IntegrationClient
```

The selection service decides **which provider** should be used.

The factory only fetches the client for that provider.

This separation is very important.


---

# 4. Why Module 7 Is More Difficult Than Module 6

Module 6 was about notification channel selection:

```text
NotificationRequest
    -> DispatchService
    -> RoutingService
    -> ChannelFactory
    -> ChannelRegistry
    -> NotificationChannel
```

Module 7 is harder because provider selection requires more checks.

Module 6 checked:

```text
tenant enabled channel
priority support
recipient details
fallback channels
```

Module 7 checks:

```text
tenant enabled provider
active provider credentials
provider health
operation support
currency support
region support
preferred provider
fallback providers
```

So Module 7 is:

```text
Request + tenant config + credentials + health + provider capabilities
    -> suitable IntegrationClient
```

This is a realistic backend factory-orchestration problem.


---

# 5. Problem Without This Design

Without this design, your execution service may become huge.

Example bad service:

```java
public class IntegrationExecutionService {

    public IntegrationResponse execute(IntegrationRequest request) {
        IntegrationClient client;

        if (request.getPreferredProvider() == IntegrationProvider.RAZORPAY) {
            if (!tenantHasRazorpay(request.getTenantId())) {
                // fallback manually
            }

            if (!razorpaySupports(request.getOperation(), request.getCurrency(), request.getRegion())) {
                // fallback manually
            }

            client = new RazorpayIntegrationClient();

        } else if (request.getPreferredProvider() == IntegrationProvider.STRIPE) {
            client = new StripeIntegrationClient();

        } else if (request.getPreferredProvider() == IntegrationProvider.PAYPAL) {
            client = new PaypalIntegrationClient();

        } else {
            client = new CashfreeIntegrationClient();
        }

        return client.execute(request);
    }
}
```

This design is bad because:

```text
1. The service knows all concrete client classes.
2. The service mixes validation, selection, factory creation, and execution.
3. Fallback logic becomes messy.
4. Tenant configuration logic is hardcoded.
5. Provider capability checks are scattered.
6. Adding a new provider becomes risky.
7. Testing becomes harder.
```

---

# 6. Problem With Factory + Selection Service Design

With the correct design, each class has one responsibility.

```text
IntegrationExecutionService
    -> coordinates execution

IntegrationClientSelectionService
    -> selects the best provider/client

IntegrationClientFactory
    -> fetches client from registry

IntegrationClientRegistry
    -> stores registered clients

TenantIntegrationConfigService
    -> provides tenant config

ProviderHealthService
    -> provides provider health

IntegrationClient implementations
    -> execute provider-specific request
```

Execution service becomes clean:

```java
public IntegrationResponse execute(IntegrationRequest request) {
    IntegrationRequestValidator.validate(request);

    List<IntegrationProvider> attemptedProviders = new ArrayList<>();

    try {
        IntegrationClient client =
                selectionService.selectClient(request, attemptedProviders);

        return client.execute(request, attemptedProviders);

    } catch (IllegalArgumentException exception) {
        return new IntegrationResponse(
                request.getRequestId(),
                request.getTenantId(),
                null,
                false,
                "Integration execution failed",
                null,
                attemptedProviders,
                exception.getMessage()
        );
    }
}
```


---

# 7. Main Objective

Build a configurable external integration client selection system using the **Factory Design Pattern**.

The system should:

```text
1. Accept an IntegrationRequest.
2. Validate the request.
3. Load tenant-specific integration config.
4. Build candidate provider list.
5. Try preferred provider first.
6. Try fallback providers after preferred provider.
7. Skip providers not enabled for the tenant.
8. Skip providers without active credentials.
9. Skip unhealthy providers.
10. Skip providers that do not support the requested operation.
11. Skip providers that do not support the requested currency.
12. Skip providers that do not support the requested region.
13. Use IntegrationClientFactory to fetch the correct client.
14. Execute the request using the selected client.
15. Return IntegrationResponse.
16. Return failure response if no suitable provider is found.
```

---

# 8. Required Classes

You need to implement:

```text
IntegrationProvider
IntegrationOperation
Currency
Region
ProviderCredential
IntegrationRequest
IntegrationResponse
IntegrationClient
RazorpayIntegrationClient
StripeIntegrationClient
PaypalIntegrationClient
CashfreeIntegrationClient
IntegrationClientRegistry
IntegrationClientFactory
TenantIntegrationConfig
TenantIntegrationConfigService
IntegrationRequestValidator
ProviderHealthService
IntegrationClientSelectionService
IntegrationExecutionService
Main
```


---

# 9. Class Responsibility Overview

| Class | Responsibility |
|---|---|
| `IntegrationProvider` | Represents external providers like RAZORPAY, STRIPE, PAYPAL, CASHFREE |
| `IntegrationOperation` | Represents the operation: PAYMENT, REFUND, PAYOUT, SUBSCRIPTION |
| `Currency` | Represents request currency: INR, USD, EUR |
| `Region` | Represents request region: INDIA, US, EUROPE |
| `ProviderCredential` | Stores provider credential details for a tenant |
| `IntegrationRequest` | Represents one integration execution request |
| `IntegrationResponse` | Represents final success/failure response |
| `IntegrationClient` | Common interface for all provider clients |
| `RazorpayIntegrationClient` | Razorpay-specific integration client |
| `StripeIntegrationClient` | Stripe-specific integration client |
| `PaypalIntegrationClient` | PayPal-specific integration client |
| `CashfreeIntegrationClient` | Cashfree-specific integration client |
| `IntegrationClientRegistry` | Stores all provider clients in a map |
| `IntegrationClientFactory` | Fetches client from registry |
| `TenantIntegrationConfig` | Stores tenant-specific enabled providers and credentials |
| `TenantIntegrationConfigService` | Simulates tenant config lookup |
| `ProviderHealthService` | Simulates provider health status |
| `IntegrationRequestValidator` | Validates request fields |
| `IntegrationClientSelectionService` | Applies provider selection rules |
| `IntegrationExecutionService` | Coordinates validation, selection, execution, and failure handling |
| `Main` | Tests direct success, fallback success, and failure scenarios |

---

# 10. High-Level Runtime Flow

```text
Main
  -> IntegrationExecutionService.execute(request)
      -> IntegrationRequestValidator.validate(request)
      -> IntegrationClientSelectionService.selectClient(request, attemptedProviders)
          -> TenantIntegrationConfigService.getConfig(tenantId)
          -> ProviderHealthService.isHealthy(provider)
          -> IntegrationClientFactory.getClient(provider)
              -> IntegrationClientRegistry.get(provider)
          -> client capability checks
      -> IntegrationClient.execute(request, attemptedProviders)
      -> IntegrationResponse
```

For failure:

```text
Main
  -> IntegrationExecutionService.execute(request)
      -> Validator passes
      -> SelectionService tries all providers
      -> No provider is suitable
      -> SelectionService throws IllegalArgumentException
      -> ExecutionService catches it
      -> ExecutionService returns failed IntegrationResponse
```


---

# 11. Enums

## `IntegrationProvider`

```java
public enum IntegrationProvider {
    RAZORPAY,
    STRIPE,
    PAYPAL,
    CASHFREE
}
```

This enum identifies external providers.

Use enum values instead of strings.

Bad:

```java
"razorpay"
"cash-free"
"stripe_provider"
```

Good:

```java
IntegrationProvider.RAZORPAY
IntegrationProvider.CASHFREE
IntegrationProvider.STRIPE
```

## `IntegrationOperation`

```java
public enum IntegrationOperation {
    PAYMENT,
    REFUND,
    PAYOUT,
    SUBSCRIPTION
}
```

Not every provider supports every operation.

The selection service must check:

```java
client.supportsOperation(request.getOperation())
```

## `Currency`

```java
public enum Currency {
    INR,
    USD,
    EUR
}
```

Different providers support different currencies.

## `Region`

```java
public enum Region {
    INDIA,
    US,
    EUROPE
}
```

Different providers operate in different regions.


---

# 12. Provider Capability Matrix

| Provider | Operations | Currencies | Regions |
|---|---|---|---|
| `RAZORPAY` | PAYMENT, REFUND, PAYOUT | INR | INDIA |
| `STRIPE` | PAYMENT, REFUND, SUBSCRIPTION | USD, EUR | US, EUROPE |
| `PAYPAL` | PAYMENT, REFUND | USD, EUR | US, EUROPE |
| `CASHFREE` | PAYMENT, PAYOUT | INR | INDIA |

This matrix drives provider selection.

Example:

```text
Request: SUBSCRIPTION + USD + US
Provider: STRIPE
Result: Allowed
```

Example:

```text
Request: SUBSCRIPTION + INR + INDIA
Provider: RAZORPAY
Result: Not allowed, because Razorpay does not support SUBSCRIPTION.
```

Example:

```text
Request: PAYMENT + INR + INDIA
Provider: STRIPE
Result: Not allowed, because Stripe does not support INR/INDIA.
```


---

# 13. `ProviderCredential`

## Purpose

Stores provider-specific credential details for a tenant.

## Fields

```text
provider
apiKey
secretKey
active
```

## Suggested Code

```java
public class ProviderCredential {

    private final IntegrationProvider provider;
    private final String apiKey;
    private final String secretKey;
    private final boolean active;

    public ProviderCredential(
            IntegrationProvider provider,
            String apiKey,
            String secretKey,
            boolean active
    ) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null.");
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key cannot be null or blank.");
        }

        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("Secret key cannot be null or blank.");
        }

        this.provider = provider;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.active = active;
    }

    public IntegrationProvider getProvider() {
        return provider;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public boolean isActive() {
        return active;
    }
}
```

## Why this class exists

In real systems, every tenant has its own provider credentials.

Example:

```text
TENANT-A Razorpay API key
TENANT-A Cashfree API key
TENANT-B Stripe API key
TENANT-B PayPal API key
```

Provider should be skipped if:

```text
credential missing
credential inactive
```

So selection service must indirectly check:

```java
config.hasActiveCredential(provider)
```


---

# 14. `IntegrationRequest`

## Purpose

Represents one request to execute an external integration operation.

## Fields

```text
requestId
tenantId
customerId
operation
currency
region
amount
idempotencyKey
preferredProvider
fallbackProviders
createdAt
```

## Suggested Code

```java
import java.time.LocalDateTime;
import java.util.List;

public class IntegrationRequest {

    private final String requestId;
    private final String tenantId;
    private final String customerId;
    private final IntegrationOperation operation;
    private final Currency currency;
    private final Region region;
    private final double amount;
    private final String idempotencyKey;
    private final IntegrationProvider preferredProvider;
    private final List<IntegrationProvider> fallbackProviders;
    private final LocalDateTime createdAt;

    public IntegrationRequest(
            String requestId,
            String tenantId,
            String customerId,
            IntegrationOperation operation,
            Currency currency,
            Region region,
            double amount,
            String idempotencyKey,
            IntegrationProvider preferredProvider,
            List<IntegrationProvider> fallbackProviders
    ) {
        if (fallbackProviders == null) {
            throw new IllegalArgumentException("Fallback providers cannot be null.");
        }

        if (fallbackProviders.contains(null)) {
            throw new IllegalArgumentException("Fallback providers cannot contain null.");
        }

        this.requestId = requestId;
        this.tenantId = tenantId;
        this.customerId = customerId;
        this.operation = operation;
        this.currency = currency;
        this.region = region;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.preferredProvider = preferredProvider;
        this.fallbackProviders = List.copyOf(fallbackProviders);
        this.createdAt = LocalDateTime.now();
    }

    public String getRequestId() { return requestId; }
    public String getTenantId() { return tenantId; }
    public String getCustomerId() { return customerId; }
    public IntegrationOperation getOperation() { return operation; }
    public Currency getCurrency() { return currency; }
    public Region getRegion() { return region; }
    public double getAmount() { return amount; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public IntegrationProvider getPreferredProvider() { return preferredProvider; }
    public List<IntegrationProvider> getFallbackProviders() { return fallbackProviders; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
```

The selection service uses:

```text
tenantId
operation
currency
region
preferredProvider
fallbackProviders
```

The validator uses all mandatory fields to ensure request correctness.


---

# 15. `IntegrationResponse`

## Purpose

Represents final result after execution.

## Fields

```text
requestId
tenantId
providerUsed
success
message
externalReferenceId
attemptedProviders
failureReason
```

## Suggested Code

```java
import java.util.List;

public class IntegrationResponse {

    private final String requestId;
    private final String tenantId;
    private final IntegrationProvider providerUsed;
    private final boolean success;
    private final String message;
    private final String externalReferenceId;
    private final List<IntegrationProvider> attemptedProviders;
    private final String failureReason;

    public IntegrationResponse(
            String requestId,
            String tenantId,
            IntegrationProvider providerUsed,
            boolean success,
            String message,
            String externalReferenceId,
            List<IntegrationProvider> attemptedProviders,
            String failureReason
    ) {
        this.requestId = requestId;
        this.tenantId = tenantId;
        this.providerUsed = providerUsed;
        this.success = success;
        this.message = message;
        this.externalReferenceId = externalReferenceId;
        this.attemptedProviders = List.copyOf(attemptedProviders);
        this.failureReason = failureReason;
    }

    public String getRequestId() { return requestId; }
    public String getTenantId() { return tenantId; }
    public IntegrationProvider getProviderUsed() { return providerUsed; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getExternalReferenceId() { return externalReferenceId; }
    public List<IntegrationProvider> getAttemptedProviders() { return attemptedProviders; }
    public String getFailureReason() { return failureReason; }
}
```

## Correct success response semantics

For success:

```text
success = true
providerUsed = selected provider
message = provider execution success message
externalReferenceId = generated provider reference
failureReason = null
```

## Correct failure response semantics

For failure:

```text
success = false
providerUsed = null
message = Integration execution failed
externalReferenceId = null
failureReason = actual failure reason
```

Never generate external reference ID for failed selection.


---

# 16. `IntegrationClient` Interface

## Purpose

Common interface for all provider clients.

## Suggested Code

```java
import java.util.List;
import java.util.Set;

public interface IntegrationClient {

    IntegrationResponse execute(
            IntegrationRequest request,
            List<IntegrationProvider> attemptedProviders
    );

    IntegrationProvider getProvider();

    String getClientName();

    Set<IntegrationOperation> supportedOperations();

    Set<Currency> supportedCurrencies();

    Set<Region> supportedRegions();

    boolean supportsOperation(IntegrationOperation operation);

    boolean supportsCurrency(Currency currency);

    boolean supportsRegion(Region region);
}
```

The execution service and selection service should not care whether the selected client is:

```text
RazorpayIntegrationClient
StripeIntegrationClient
PaypalIntegrationClient
CashfreeIntegrationClient
```

They should depend on:

```java
IntegrationClient
```

This is polymorphism.


---

# 17. Concrete Clients

Each concrete client should:

```text
1. Implement IntegrationClient.
2. Return its provider.
3. Define supported operations.
4. Define supported currencies.
5. Define supported regions.
6. Execute request and return IntegrationResponse.
```

## `RazorpayIntegrationClient`

```java
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RazorpayIntegrationClient implements IntegrationClient {

    @Override
    public IntegrationResponse execute(
            IntegrationRequest request,
            List<IntegrationProvider> attemptedProviders
    ) {
        return new IntegrationResponse(
                request.getRequestId(),
                request.getTenantId(),
                getProvider(),
                true,
                "Razorpay integration executed successfully",
                "RAZORPAY-" + UUID.randomUUID(),
                attemptedProviders,
                null
        );
    }

    @Override
    public IntegrationProvider getProvider() {
        return IntegrationProvider.RAZORPAY;
    }

    @Override
    public String getClientName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Set<IntegrationOperation> supportedOperations() {
        return Set.of(
                IntegrationOperation.PAYMENT,
                IntegrationOperation.REFUND,
                IntegrationOperation.PAYOUT
        );
    }

    @Override
    public Set<Currency> supportedCurrencies() {
        return Set.of(Currency.INR);
    }

    @Override
    public Set<Region> supportedRegions() {
        return Set.of(Region.INDIA);
    }

    @Override
    public boolean supportsOperation(IntegrationOperation operation) {
        return supportedOperations().contains(operation);
    }

    @Override
    public boolean supportsCurrency(Currency currency) {
        return supportedCurrencies().contains(currency);
    }

    @Override
    public boolean supportsRegion(Region region) {
        return supportedRegions().contains(region);
    }
}
```

## `StripeIntegrationClient`

Supports:

```text
Operations: PAYMENT, REFUND, SUBSCRIPTION
Currencies: USD, EUR
Regions: US, EUROPE
```

## `PaypalIntegrationClient`

Supports:

```text
Operations: PAYMENT, REFUND
Currencies: USD, EUR
Regions: US, EUROPE
```

## `CashfreeIntegrationClient`

Supports:

```text
Operations: PAYMENT, PAYOUT
Currencies: INR
Regions: INDIA
```


---

# 18. `IntegrationClientRegistry`

## Purpose

Stores all available integration clients in a map.

## Suggested Code

```java
import java.util.EnumMap;
import java.util.Map;

public class IntegrationClientRegistry {

    private final Map<IntegrationProvider, IntegrationClient> clients =
            new EnumMap<>(IntegrationProvider.class);

    public IntegrationClientRegistry() {
        register(new RazorpayIntegrationClient());
        register(new StripeIntegrationClient());
        register(new PaypalIntegrationClient());
        register(new CashfreeIntegrationClient());
    }

    public void register(IntegrationClient client) {
        if (client == null) {
            throw new IllegalArgumentException("Integration client cannot be null.");
        }

        clients.put(client.getProvider(), client);
    }

    public IntegrationClient get(IntegrationProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Integration provider cannot be null.");
        }

        IntegrationClient client = clients.get(provider);

        if (client == null) {
            throw new IllegalArgumentException(
                    "No integration client registered for provider: " + provider
            );
        }

        return client;
    }

    public boolean isRegistered(IntegrationProvider provider) {
        return clients.containsKey(provider);
    }
}
```

The registry allows the factory to avoid a switch.


---

# 19. `IntegrationClientFactory`

## Purpose

Fetches provider clients from the registry.

## Suggested Code

```java
public class IntegrationClientFactory {

    private final IntegrationClientRegistry registry;

    public IntegrationClientFactory(IntegrationClientRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("Integration client registry cannot be null.");
        }

        this.registry = registry;
    }

    public IntegrationClient getClient(IntegrationProvider provider) {
        return registry.get(provider);
    }
}
```

## What this class should do

```text
Take IntegrationProvider.
Ask registry for matching IntegrationClient.
Return IntegrationClient.
```

## What this class should NOT do

```text
It should not choose best provider.
It should not check fallback providers.
It should not check tenant config.
It should not check provider health.
It should not execute request.
```

Selection belongs to `IntegrationClientSelectionService`.


---

# 20. `TenantIntegrationConfig`

## Purpose

Represents tenant-specific provider configuration.

## Fields

```text
tenantId
enabledProviders
credentials
```

## Suggested Code

```java
import java.util.Map;
import java.util.Set;

public class TenantIntegrationConfig {

    private final String tenantId;
    private final Set<IntegrationProvider> enabledProviders;
    private final Map<IntegrationProvider, ProviderCredential> credentials;

    public TenantIntegrationConfig(
            String tenantId,
            Set<IntegrationProvider> enabledProviders,
            Map<IntegrationProvider, ProviderCredential> credentials
    ) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank.");
        }

        if (enabledProviders == null || enabledProviders.isEmpty()) {
            throw new IllegalArgumentException("Enabled providers cannot be null or empty.");
        }

        if (credentials == null || credentials.isEmpty()) {
            throw new IllegalArgumentException("Credentials cannot be null or empty.");
        }

        this.tenantId = tenantId;
        this.enabledProviders = Set.copyOf(enabledProviders);
        this.credentials = Map.copyOf(credentials);
    }

    public String getTenantId() {
        return tenantId;
    }

    public Set<IntegrationProvider> getEnabledProviders() {
        return enabledProviders;
    }

    public boolean isProviderEnabled(IntegrationProvider provider) {
        return enabledProviders.contains(provider);
    }

    public boolean hasActiveCredential(IntegrationProvider provider) {
        ProviderCredential credential = credentials.get(provider);
        return credential != null && credential.isActive();
    }

    public ProviderCredential getCredential(IntegrationProvider provider) {
        return credentials.get(provider);
    }
}
```

A provider may exist globally but not be enabled for a specific tenant.

Also, a tenant may have credentials, but those credentials may be inactive.

So use:

```java
hasActiveCredential(provider)
```

not only:

```java
hasCredentials(provider)
```


---

# 21. `TenantIntegrationConfigService`

## Purpose

Simulates fetching tenant config from DB/config service.

## Suggested Data

```text
TENANT-A -> RAZORPAY, CASHFREE
TENANT-B -> STRIPE, PAYPAL
```

## Suggested Code

```java
import java.util.Map;
import java.util.Set;

public class TenantIntegrationConfigService {

    private final Map<String, TenantIntegrationConfig> configs =
            Map.of(
                    "TENANT-A",
                    new TenantIntegrationConfig(
                            "TENANT-A",
                            Set.of(
                                    IntegrationProvider.RAZORPAY,
                                    IntegrationProvider.CASHFREE
                            ),
                            Map.of(
                                    IntegrationProvider.RAZORPAY,
                                    new ProviderCredential(
                                            IntegrationProvider.RAZORPAY,
                                            "rzp-key",
                                            "rzp-secret",
                                            true
                                    ),
                                    IntegrationProvider.CASHFREE,
                                    new ProviderCredential(
                                            IntegrationProvider.CASHFREE,
                                            "cf-key",
                                            "cf-secret",
                                            true
                                    )
                            )
                    ),

                    "TENANT-B",
                    new TenantIntegrationConfig(
                            "TENANT-B",
                            Set.of(
                                    IntegrationProvider.STRIPE,
                                    IntegrationProvider.PAYPAL
                            ),
                            Map.of(
                                    IntegrationProvider.STRIPE,
                                    new ProviderCredential(
                                            IntegrationProvider.STRIPE,
                                            "stripe-key",
                                            "stripe-secret",
                                            true
                                    ),
                                    IntegrationProvider.PAYPAL,
                                    new ProviderCredential(
                                            IntegrationProvider.PAYPAL,
                                            "paypal-key",
                                            "paypal-secret",
                                            true
                                    )
                            )
                    )
            );

    public TenantIntegrationConfig getConfig(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank.");
        }

        TenantIntegrationConfig config = configs.get(tenantId);

        if (config == null) {
            throw new IllegalArgumentException("No integration config found for tenant: " + tenantId);
        }

        return config;
    }
}
```


---

# 22. `ProviderHealthService`

## Purpose

Simulates provider health.

## Suggested Code

```java
import java.util.Map;

public class ProviderHealthService {

    private final Map<IntegrationProvider, Boolean> healthStatus =
            Map.of(
                    IntegrationProvider.RAZORPAY, true,
                    IntegrationProvider.STRIPE, true,
                    IntegrationProvider.PAYPAL, false,
                    IntegrationProvider.CASHFREE, true
            );

    public boolean isHealthy(IntegrationProvider provider) {
        return healthStatus.getOrDefault(provider, false);
    }
}
```

A provider may be configured and capable, but temporarily down.

Example:

```text
PAYPAL is enabled for TENANT-B.
PAYPAL supports USD.
PAYPAL supports PAYMENT.
But PAYPAL health = false.
So PAYPAL should be skipped.
```

This gives you a realistic fallback case.


---

# 23. `IntegrationRequestValidator`

## Purpose

Validates basic request correctness.

It should not select providers.

## Suggested Code

```java
public class IntegrationRequestValidator {

    private IntegrationRequestValidator() {
        // Utility class
    }

    public static void validate(IntegrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Integration request cannot be null.");
        }

        if (request.getRequestId() == null || request.getRequestId().isBlank()) {
            throw new IllegalArgumentException("Request ID cannot be null or blank.");
        }

        if (request.getTenantId() == null || request.getTenantId().isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank.");
        }

        if (request.getCustomerId() == null || request.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank.");
        }

        if (request.getOperation() == null) {
            throw new IllegalArgumentException("Operation cannot be null.");
        }

        if (request.getCurrency() == null) {
            throw new IllegalArgumentException("Currency cannot be null.");
        }

        if (request.getRegion() == null) {
            throw new IllegalArgumentException("Region cannot be null.");
        }

        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (request.getIdempotencyKey() == null || request.getIdempotencyKey().isBlank()) {
            throw new IllegalArgumentException("Idempotency key cannot be null or blank.");
        }

        if (request.getPreferredProvider() == null) {
            throw new IllegalArgumentException("Preferred provider cannot be null.");
        }

        if (request.getFallbackProviders() == null) {
            throw new IllegalArgumentException("Fallback providers cannot be null.");
        }

        if (request.getFallbackProviders().contains(null)) {
            throw new IllegalArgumentException("Fallback providers cannot contain null values.");
        }
    }
}
```


---

# 24. `IntegrationClientSelectionService`

## Purpose

This is the most important and hardest class in Module 7.

It decides which provider client should be used.

## Responsibilities

```text
1. Load tenant integration config.
2. Create candidate provider list.
3. Add preferred provider first.
4. Add fallback providers after preferred provider.
5. Track attempted providers.
6. Skip provider if not enabled for tenant.
7. Skip provider if active credential is missing.
8. Skip provider if provider is unhealthy.
9. Fetch client using IntegrationClientFactory.
10. Skip client if operation is not supported.
11. Skip client if currency is not supported.
12. Skip client if region is not supported.
13. Return first suitable client.
14. Throw error if no suitable client exists.
```

## Suggested Code

```java
import java.util.ArrayList;
import java.util.List;

public class IntegrationClientSelectionService {

    private final IntegrationClientFactory clientFactory;
    private final TenantIntegrationConfigService configService;
    private final ProviderHealthService healthService;

    public IntegrationClientSelectionService(
            IntegrationClientFactory clientFactory,
            TenantIntegrationConfigService configService,
            ProviderHealthService healthService
    ) {
        if (clientFactory == null) {
            throw new IllegalArgumentException("IntegrationClientFactory cannot be null.");
        }

        if (configService == null) {
            throw new IllegalArgumentException("TenantIntegrationConfigService cannot be null.");
        }

        if (healthService == null) {
            throw new IllegalArgumentException("ProviderHealthService cannot be null.");
        }

        this.clientFactory = clientFactory;
        this.configService = configService;
        this.healthService = healthService;
    }

    public IntegrationClient selectClient(
            IntegrationRequest request,
            List<IntegrationProvider> attemptedProviders
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Integration request cannot be null.");
        }

        if (attemptedProviders == null) {
            throw new IllegalArgumentException("Attempted providers list cannot be null.");
        }

        TenantIntegrationConfig config =
                configService.getConfig(request.getTenantId());

        List<IntegrationProvider> candidates = new ArrayList<>();
        candidates.add(request.getPreferredProvider());
        candidates.addAll(request.getFallbackProviders());

        for (IntegrationProvider provider : candidates) {
            attemptedProviders.add(provider);

            if (!config.isProviderEnabled(provider)) {
                continue;
            }

            if (!config.hasActiveCredential(provider)) {
                continue;
            }

            if (!healthService.isHealthy(provider)) {
                continue;
            }

            IntegrationClient client =
                    clientFactory.getClient(provider);

            if (!client.supportsOperation(request.getOperation())) {
                continue;
            }

            if (!client.supportsCurrency(request.getCurrency())) {
                continue;
            }

            if (!client.supportsRegion(request.getRegion())) {
                continue;
            }

            return client;
        }

        throw new IllegalArgumentException(
                "No suitable integration provider found for request: "
                        + request.getRequestId()
        );
    }
}
```

## What this class should NOT do

```text
It should not create provider clients using new.
It should not execute the request.
It should not create IntegrationResponse.
It should not validate basic request fields.
```

It only selects the best client.


---

# 25. `IntegrationExecutionService`

## Purpose

Coordinates the full integration execution flow.

## Responsibilities

```text
1. Validate request.
2. Create attempted providers list.
3. Ask selection service to select client.
4. Execute request using selected client.
5. Return success response.
6. Return failure response if no client is suitable.
```

## Suggested Code

```java
import java.util.ArrayList;
import java.util.List;

public class IntegrationExecutionService {

    private final IntegrationClientSelectionService selectionService;

    public IntegrationExecutionService(
            IntegrationClientSelectionService selectionService
    ) {
        if (selectionService == null) {
            throw new IllegalArgumentException("IntegrationClientSelectionService cannot be null.");
        }

        this.selectionService = selectionService;
    }

    public IntegrationResponse execute(IntegrationRequest request) {
        IntegrationRequestValidator.validate(request);

        List<IntegrationProvider> attemptedProviders = new ArrayList<>();

        try {
            IntegrationClient client =
                    selectionService.selectClient(request, attemptedProviders);

            return client.execute(request, attemptedProviders);

        } catch (IllegalArgumentException exception) {
            return new IntegrationResponse(
                    request.getRequestId(),
                    request.getTenantId(),
                    null,
                    false,
                    "Integration execution failed",
                    null,
                    attemptedProviders,
                    exception.getMessage()
            );
        }
    }
}
```

This is the main service class.

It keeps `Main` clean.

It keeps selection logic outside client classes.

It keeps execution orchestration in one place.


---

# 26. Expected Main Test Cases

Your `Main` should test at least these scenarios:

```text
1. Direct success:
   TENANT-A preferred RAZORPAY, INR, INDIA, PAYMENT
   -> RAZORPAY selected.

2. Fallback due to disabled provider:
   TENANT-A preferred STRIPE, fallback CASHFREE, INR, INDIA, PAYMENT
   -> STRIPE disabled for TENANT-A, CASHFREE selected.

3. Fallback due to unhealthy provider:
   TENANT-B preferred PAYPAL, fallback STRIPE, USD, US, PAYMENT
   -> PAYPAL unhealthy, STRIPE selected.

4. Direct success with subscription:
   TENANT-B preferred STRIPE, USD, US, SUBSCRIPTION
   -> STRIPE selected.

5. Failure due to unsupported operation/currency/region:
   TENANT-A preferred RAZORPAY, fallback CASHFREE, EUR, EUROPE, SUBSCRIPTION
   -> no suitable provider found.

6. Failure due to currency/region mismatch:
   TENANT-B preferred STRIPE, fallback PAYPAL, INR, INDIA, PAYMENT
   -> no suitable provider found.
```


---

# 27. Suggested `Main.java`

```java
import java.util.List;

public class Main {

    public static void main(String[] args) {

        IntegrationClientRegistry registry =
                new IntegrationClientRegistry();

        IntegrationClientFactory factory =
                new IntegrationClientFactory(registry);

        TenantIntegrationConfigService configService =
                new TenantIntegrationConfigService();

        ProviderHealthService healthService =
                new ProviderHealthService();

        IntegrationClientSelectionService selectionService =
                new IntegrationClientSelectionService(
                        factory,
                        configService,
                        healthService
                );

        IntegrationExecutionService executionService =
                new IntegrationExecutionService(selectionService);

        System.out.println("Factory Module 7 — Configurable Integration Client Factory");

        IntegrationRequest razorpaySuccessRequest = new IntegrationRequest(
                "REQ-101",
                "TENANT-A",
                "CUST-101",
                IntegrationOperation.PAYMENT,
                Currency.INR,
                Region.INDIA,
                1500.00,
                "IDEMP-REQ-101",
                IntegrationProvider.RAZORPAY,
                List.of(IntegrationProvider.CASHFREE)
        );

        printResponse(executionService.execute(razorpaySuccessRequest));

        IntegrationRequest fallbackToCashfreeRequest = new IntegrationRequest(
                "REQ-102",
                "TENANT-A",
                "CUST-102",
                IntegrationOperation.PAYMENT,
                Currency.INR,
                Region.INDIA,
                2500.00,
                "IDEMP-REQ-102",
                IntegrationProvider.STRIPE,
                List.of(IntegrationProvider.CASHFREE)
        );

        printResponse(executionService.execute(fallbackToCashfreeRequest));

        IntegrationRequest fallbackToStripeDueToPaypalHealthRequest = new IntegrationRequest(
                "REQ-103",
                "TENANT-B",
                "CUST-103",
                IntegrationOperation.PAYMENT,
                Currency.USD,
                Region.US,
                3000.00,
                "IDEMP-REQ-103",
                IntegrationProvider.PAYPAL,
                List.of(IntegrationProvider.STRIPE)
        );

        printResponse(executionService.execute(fallbackToStripeDueToPaypalHealthRequest));

        IntegrationRequest stripeSubscriptionRequest = new IntegrationRequest(
                "REQ-104",
                "TENANT-B",
                "CUST-104",
                IntegrationOperation.SUBSCRIPTION,
                Currency.USD,
                Region.US,
                999.00,
                "IDEMP-REQ-104",
                IntegrationProvider.STRIPE,
                List.of(IntegrationProvider.PAYPAL)
        );

        printResponse(executionService.execute(stripeSubscriptionRequest));

        IntegrationRequest noSuitableProviderRequest = new IntegrationRequest(
                "REQ-105",
                "TENANT-A",
                "CUST-105",
                IntegrationOperation.SUBSCRIPTION,
                Currency.EUR,
                Region.EUROPE,
                5000.00,
                "IDEMP-REQ-105",
                IntegrationProvider.RAZORPAY,
                List.of(IntegrationProvider.CASHFREE)
        );

        printResponse(executionService.execute(noSuitableProviderRequest));

        IntegrationRequest currencyRegionMismatchRequest = new IntegrationRequest(
                "REQ-106",
                "TENANT-B",
                "CUST-106",
                IntegrationOperation.PAYMENT,
                Currency.INR,
                Region.INDIA,
                1200.00,
                "IDEMP-REQ-106",
                IntegrationProvider.STRIPE,
                List.of(IntegrationProvider.PAYPAL)
        );

        printResponse(executionService.execute(currencyRegionMismatchRequest));
    }

    private static void printResponse(IntegrationResponse response) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Request ID: " + response.getRequestId());
        System.out.println("Tenant ID: " + response.getTenantId());
        System.out.println("Success: " + response.isSuccess());
        System.out.println("Provider Used: " + response.getProviderUsed());
        System.out.println("Message: " + response.getMessage());
        System.out.println("External Reference ID: " + response.getExternalReferenceId());
        System.out.println("Attempted Providers: " + response.getAttemptedProviders());
        System.out.println("Failure Reason: " + response.getFailureReason());
        System.out.println("==================================================");
    }
}
```


---

# 28. Expected Output Style

## REQ-101

```text
==================================================
Request ID: REQ-101
Tenant ID: TENANT-A
Success: true
Provider Used: RAZORPAY
Message: Razorpay integration executed successfully
External Reference ID: RAZORPAY-<uuid>
Attempted Providers: [RAZORPAY]
Failure Reason: null
==================================================
```

## REQ-102

```text
==================================================
Request ID: REQ-102
Tenant ID: TENANT-A
Success: true
Provider Used: CASHFREE
Message: Cashfree integration executed successfully
External Reference ID: CASHFREE-<uuid>
Attempted Providers: [STRIPE, CASHFREE]
Failure Reason: null
==================================================
```

## REQ-103

```text
==================================================
Request ID: REQ-103
Tenant ID: TENANT-B
Success: true
Provider Used: STRIPE
Message: Stripe integration executed successfully
External Reference ID: STRIPE-<uuid>
Attempted Providers: [PAYPAL, STRIPE]
Failure Reason: null
==================================================
```

## REQ-104

```text
==================================================
Request ID: REQ-104
Tenant ID: TENANT-B
Success: true
Provider Used: STRIPE
Message: Stripe integration executed successfully
External Reference ID: STRIPE-<uuid>
Attempted Providers: [STRIPE]
Failure Reason: null
==================================================
```

## REQ-105

```text
==================================================
Request ID: REQ-105
Tenant ID: TENANT-A
Success: false
Provider Used: null
Message: Integration execution failed
External Reference ID: null
Attempted Providers: [RAZORPAY, CASHFREE]
Failure Reason: No suitable integration provider found for request: REQ-105
==================================================
```

## REQ-106

```text
==================================================
Request ID: REQ-106
Tenant ID: TENANT-B
Success: false
Provider Used: null
Message: Integration execution failed
External Reference ID: null
Attempted Providers: [STRIPE, PAYPAL]
Failure Reason: No suitable integration provider found for request: REQ-106
==================================================
```


---

# 29. Correct Execution Flow Example

For this request:

```text
TENANT-B
preferred provider = PAYPAL
fallback provider = STRIPE
operation = PAYMENT
currency = USD
region = US
```

Flow:

```text
1. Main calls IntegrationExecutionService.execute(request).
2. ExecutionService validates the request.
3. ExecutionService creates attemptedProviders list.
4. ExecutionService calls SelectionService.selectClient().
5. SelectionService loads TENANT-B config.
6. Candidate list becomes [PAYPAL, STRIPE].
7. SelectionService attempts PAYPAL.
8. PAYPAL is enabled.
9. PAYPAL has active credential.
10. ProviderHealthService says PAYPAL is unhealthy.
11. PAYPAL is skipped.
12. SelectionService attempts STRIPE.
13. STRIPE is enabled.
14. STRIPE has active credential.
15. STRIPE is healthy.
16. Factory fetches StripeIntegrationClient from registry.
17. STRIPE supports PAYMENT.
18. STRIPE supports USD.
19. STRIPE supports US.
20. SelectionService returns StripeIntegrationClient.
21. ExecutionService calls client.execute().
22. StripeIntegrationClient returns successful IntegrationResponse.
```

---

# 30. What Makes This Factory Pattern?

The Factory Pattern exists here:

```java
IntegrationClient client =
        integrationClientFactory.getClient(provider);
```

The caller does not directly do:

```java
new RazorpayIntegrationClient()
new StripeIntegrationClient()
```

The caller receives:

```java
IntegrationClient
```

The concrete type is hidden behind the factory and registry.

The factory itself uses:

```java
IntegrationClientRegistry
```

to fetch the correct client.

This is a registry-based Factory Pattern.


---

# 31. Responsibility Separation

## `IntegrationClientRegistry`

```text
Stores available integration clients.
```

## `IntegrationClientFactory`

```text
Fetches a client by provider.
```

## `TenantIntegrationConfigService`

```text
Provides tenant-specific enabled providers and credentials.
```

## `ProviderHealthService`

```text
Reports whether a provider is healthy.
```

## `IntegrationClientSelectionService`

```text
Applies provider selection rules.
```

## `IntegrationExecutionService`

```text
Coordinates validation, selection, execution, and failure response.
```

## `IntegrationClient` implementations

```text
Execute provider-specific request and return response.
```


---

# 32. Important Rules

## Rule 1: Factory should not use switch

Wrong:

```java
return switch (provider) {
    case RAZORPAY -> new RazorpayIntegrationClient();
    case STRIPE -> new StripeIntegrationClient();
};
```

Correct:

```java
return registry.get(provider);
```

## Rule 2: Factory should not select the best provider

Wrong:

```java
factory.selectBestClient(request);
```

Correct:

```java
selectionService.selectClient(request, attemptedProviders);
```

## Rule 3: Selection service should not create clients directly

Wrong:

```java
IntegrationClient client = new RazorpayIntegrationClient();
```

Correct:

```java
IntegrationClient client = clientFactory.getClient(provider);
```

## Rule 4: Failed response should not have provider reference ID

For failure:

```text
providerUsed = null
externalReferenceId = null
success = false
failureReason = actual reason
```

## Rule 5: Attempted providers must be tracked

If preferred provider fails and fallback is used:

```text
Attempted Providers: [STRIPE, CASHFREE]
```

This proves fallback routing worked.

## Rule 6: Check active credential, not just credential existence

Wrong:

```java
credentials.containsKey(provider)
```

Correct:

```java
ProviderCredential credential = credentials.get(provider);
return credential != null && credential.isActive();
```


---

# 33. Common Mistakes

```text
1. Using switch inside IntegrationClientFactory.
2. Creating clients directly in selection service.
3. Putting fallback logic inside factory.
4. Not checking tenant enabled providers.
5. Not checking active credentials.
6. Checking only credential existence, not credential active status.
7. Not checking provider health.
8. Not checking operation support.
9. Not checking currency support.
10. Not checking region support.
11. Not tracking attempted providers.
12. Returning success response for failed selection.
13. Generating externalReferenceId for failed response.
14. Not using List.copyOf() for fallback providers.
15. Catching broad Exception instead of IllegalArgumentException.
16. Making IntegrationExecutionService too large.
17. Naming execute method as getIntegrationClient even though it returns response.
18. Using misleading failure messages.
```


---

# 34. Scoring Rubric

| Area | Marks |
|---|---:|
| `IntegrationProvider` enum | 0.7 |
| `IntegrationOperation` enum | 0.7 |
| `Currency` enum | 0.5 |
| `Region` enum | 0.5 |
| `ProviderCredential` | 1 |
| `IntegrationRequest` | 1 |
| `IntegrationResponse` | 1 |
| `IntegrationClient` interface | 1.2 |
| All 4 concrete clients | 2 |
| Client capability methods | 1.5 |
| `IntegrationClientRegistry` | 1.5 |
| Registry-based `IntegrationClientFactory` | 1.5 |
| `TenantIntegrationConfig` | 1.2 |
| `TenantIntegrationConfigService` | 1 |
| `ProviderHealthService` | 1 |
| `IntegrationRequestValidator` | 1 |
| `IntegrationClientSelectionService` | 2.5 |
| `IntegrationExecutionService` | 1.2 |
| Main success scenarios | 1 |
| Main fallback scenarios | 1 |
| Main failure scenario | 1 |
| Correct response semantics | 1 |
| Code readability and naming | 1 |

Total: **26.0 marks normalized to 10**

---

# 35. Ideal Code Structure

```text
factory/module7/
    IntegrationProvider.java
    IntegrationOperation.java
    Currency.java
    Region.java
    ProviderCredential.java
    IntegrationRequest.java
    IntegrationResponse.java
    IntegrationClient.java
    RazorpayIntegrationClient.java
    StripeIntegrationClient.java
    PaypalIntegrationClient.java
    CashfreeIntegrationClient.java
    IntegrationClientRegistry.java
    IntegrationClientFactory.java
    TenantIntegrationConfig.java
    TenantIntegrationConfigService.java
    ProviderHealthService.java
    IntegrationRequestValidator.java
    IntegrationClientSelectionService.java
    IntegrationExecutionService.java
    Main.java
```


---

# 36. Difference Between Module 6 and Module 7

Module 6:

```text
NotificationRequest -> RoutingService -> ChannelFactory -> ChannelRegistry -> NotificationChannel
```

Module 7:

```text
IntegrationRequest -> SelectionService -> ClientFactory -> ClientRegistry -> IntegrationClient
```

Module 7 is harder because it adds:

```text
provider operation capability
provider currency capability
provider region capability
tenant credentials
active credential check
provider health check
provider execution response
idempotency key validation
provider-specific external reference ID
```

---

# 37. Final Learning Goal

By completing Module 7, you should understand:

```text
1. Registry-based Factory Pattern at a final-boss level.
2. How factories fit inside larger backend systems.
3. Why a factory should not contain routing/fallback logic.
4. How tenant config affects client selection.
5. How provider credentials affect selection.
6. How provider capabilities affect selection.
7. How provider health affects selection.
8. How fallback providers should be attempted.
9. How to model success and failure responses cleanly.
10. How to separate validation, selection, factory lookup, and execution.
```

The core mental model is:

```text
Request comes in.
ExecutionService validates it.
SelectionService chooses the best provider.
Factory fetches the client from registry.
Client executes request.
Response is returned.
```

This is the hardest Factory Pattern module in the series.

If you can implement this cleanly, you understand how Factory Pattern is used in a realistic backend integration workflow.
