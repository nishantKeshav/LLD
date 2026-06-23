public class ProviderCredential {

    private final String apiKey;
    private final boolean active;
    private final String secretKey;
    private final IntegrationProvider provider;

    public ProviderCredential(IntegrationProvider provider, String apiKey, String secretKey, boolean active) {
        if (provider == null) {
            throw new IllegalArgumentException("provider cannot be null");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey cannot be blank");
        }
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalArgumentException("secretKey cannot be blank");
        }
        this.apiKey = apiKey;
        this.active = active;
        this.provider = provider;
        this.secretKey = secretKey;
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
