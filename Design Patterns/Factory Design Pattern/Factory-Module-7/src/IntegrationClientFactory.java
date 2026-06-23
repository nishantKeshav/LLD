public class IntegrationClientFactory {

    private final IntegrationClientRegistry integrationClientRegistry;

    public IntegrationClientFactory(IntegrationClientRegistry integrationClientRegistry) {
        if (integrationClientRegistry == null) {
            throw new IllegalArgumentException("integrationClientRegistry cannot be null");
        }
        this.integrationClientRegistry = integrationClientRegistry;
    }

    public IntegrationClient getClient(IntegrationProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider cannot be null");
        }
        return integrationClientRegistry.getClient(provider);
    }


}
