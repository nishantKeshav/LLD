import java.util.Set;
import java.util.List;
import java.util.UUID;

public class StripeIntegrationClient implements IntegrationClient{

    @Override
    public IntegrationResponse execute(IntegrationRequest request, List<IntegrationProvider> attemptedProviders) {
        return new IntegrationResponse(
                request.getRequestId(),
                request.getTenantId(),
                getProvider(),
                true,
                getProvider() + " integration executed successfully",
                getProvider() + "-" + UUID.randomUUID(),
                attemptedProviders,
                null
        );
    }

    @Override
    public IntegrationProvider getProvider() {
        return IntegrationProvider.STRIPE;
    }

    @Override
    public String getClientName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Set<IntegrationOperation> supportedOperations() {
        return Set.of(IntegrationOperation.PAYMENT, IntegrationOperation.REFUND, IntegrationOperation.SUBSCRIPTION);
    }

    @Override
    public Set<Currency> supportedCurrencies() {
        return Set.of(Currency.USD, Currency.EUR);
    }

    @Override
    public Set<Region> supportedRegions() {
        return Set.of(Region.US, Region.EUROPE);
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
