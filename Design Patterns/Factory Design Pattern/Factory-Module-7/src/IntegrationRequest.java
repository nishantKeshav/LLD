import java.util.List;
import java.time.LocalDateTime;
import java.util.Objects;

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

    public IntegrationRequest(String requestId, String tenantId, String customerId, IntegrationOperation operation, Currency currency,
                              Region region, double amount, String idempotencyKey, IntegrationProvider preferredProvider,
                              List<IntegrationProvider> fallbackProviders) {
        if (fallbackProviders == null) {
            throw new IllegalArgumentException("Fallback providers cannot be null.");
        }
        if (fallbackProviders.stream().anyMatch(Objects::isNull)) {
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

    public String getRequestId() {
        return requestId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public IntegrationOperation getOperation() {
        return operation;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Region getRegion() {
        return region;
    }

    public double getAmount() {
        return amount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public IntegrationProvider getPreferredProvider() {
        return preferredProvider;
    }

    public List<IntegrationProvider> getFallbackProviders() {
        return List.copyOf(fallbackProviders);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
