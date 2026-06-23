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