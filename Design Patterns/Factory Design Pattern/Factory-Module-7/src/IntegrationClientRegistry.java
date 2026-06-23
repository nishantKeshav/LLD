import java.util.Map;
import java.util.EnumMap;

public class IntegrationClientRegistry {

    private final Map<IntegrationProvider,IntegrationClient> clients;

    public IntegrationClientRegistry() {
        clients = new EnumMap<>(IntegrationProvider.class);
        register(new PaypalIntegrationClient());
        register(new StripeIntegrationClient());
        register(new CashfreeIntegrationClient());
        register(new RazorpayIntegrationClient());
    }

    public void register(IntegrationClient client) {
        if (client == null) {
            throw new IllegalArgumentException("Integration client cannot be null.");
        }
        clients.put(client.getProvider(), client);
    }

    public IntegrationClient getClient(IntegrationProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Integration client cannot be null.");
        }
        IntegrationClient client = clients.get(provider);
        if (client == null) {
            throw new IllegalArgumentException("Integration client cannot be null.");
        }
        return client;
    }

    public boolean isClientRegistered(IntegrationProvider provider) {
        return clients.containsKey(provider);
    }

}
