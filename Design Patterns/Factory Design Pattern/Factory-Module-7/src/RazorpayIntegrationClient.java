import java.util.Set;
import java.util.List;
import java.util.UUID;

public class RazorpayIntegrationClient implements IntegrationClient {

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
        return IntegrationProvider.RAZORPAY;
    }

    @Override
    public String getClientName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Set<IntegrationOperation> supportedOperations() {
        return Set.of(IntegrationOperation.PAYMENT, IntegrationOperation.REFUND, IntegrationOperation.PAYOUT);
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
