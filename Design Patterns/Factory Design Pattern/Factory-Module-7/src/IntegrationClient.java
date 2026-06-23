import java.util.Set;
import java.util.List;

public interface IntegrationClient {

    IntegrationResponse execute(IntegrationRequest request, List<IntegrationProvider> attemptedProviders);

    IntegrationProvider getProvider();

    String getClientName();

    Set<IntegrationOperation> supportedOperations();

    Set<Currency> supportedCurrencies();

    Set<Region> supportedRegions();

    boolean supportsOperation(IntegrationOperation operation);

    boolean supportsCurrency(Currency currency);

    boolean supportsRegion(Region region);
}