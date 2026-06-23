import java.util.List;
import java.util.ArrayList;

public class IntegrationClientSelectionService {

    private final IntegrationClientFactory clientFactory;
    private final ProviderHealthService providerHealthService;
    private final TenantIntegrationConfigService tenantIntegrationConfigService;

    public IntegrationClientSelectionService(IntegrationClientFactory clientFactory, ProviderHealthService providerHealthService,
                                             TenantIntegrationConfigService tenantIntegrationConfigService) {
        if (clientFactory == null) {
            throw new IllegalArgumentException("clientFactory cannot be null");
        }
        if (providerHealthService == null) {
            throw new IllegalArgumentException("providerHealthService cannot be null");
        }
        if (tenantIntegrationConfigService == null) {
            throw new IllegalArgumentException("tenantIntegrationConfigService cannot be null");
        }
        this.clientFactory = clientFactory;
        this.providerHealthService = providerHealthService;
        this.tenantIntegrationConfigService = tenantIntegrationConfigService;
    }

    public IntegrationClient getIntegrationClient(IntegrationRequest request, List<IntegrationProvider> attemptedProviders) {
        if (request == null) {
            throw new IllegalArgumentException("integrationProvider cannot be null");
        }
        if (attemptedProviders == null) {
            throw new IllegalArgumentException("attemptedProviders cannot be null");
        }
        String tenantId = request.getTenantId();
        TenantIntegrationConfig configService = tenantIntegrationConfigService.getConfig(tenantId);

        List<IntegrationProvider> integrationProvidersList = new ArrayList<>();
        integrationProvidersList.add(request.getPreferredProvider());
        integrationProvidersList.addAll(request.getFallbackProviders());

        for (IntegrationProvider provider : integrationProvidersList) {
            attemptedProviders.add(provider);

            if (!configService.isProviderEnabled(provider)) {
                continue;
            }

            if (!configService.hasActiveCredential(provider)) {
                continue;
            }

            if (!providerHealthService.isHealthy(provider)) {
                continue;
            }

            IntegrationClient integrationClient = clientFactory.getClient(provider);

            if (!integrationClient.supportsOperation(request.getOperation())) {
                continue;
            }

            if (!integrationClient.supportsCurrency(request.getCurrency())) {
                continue;
            }

            if (!integrationClient.supportsRegion(request.getRegion())) {
                continue;
            }
            return integrationClient;
        }
        throw new IllegalArgumentException("No provider found for the provider " + request.getPreferredProvider());
    }
}
