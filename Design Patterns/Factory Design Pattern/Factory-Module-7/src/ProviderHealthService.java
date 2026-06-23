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