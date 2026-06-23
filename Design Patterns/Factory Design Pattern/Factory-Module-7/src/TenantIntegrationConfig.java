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