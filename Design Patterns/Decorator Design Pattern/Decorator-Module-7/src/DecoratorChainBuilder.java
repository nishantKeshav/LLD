import java.util.List;
import java.util.Comparator;

public class DecoratorChainBuilder {

    private DecoratorChainBuilder() {
        /* This utility class should not be instantiated */
    }

    public static HttpClient build(HttpClient baseClient, List<DecoratorType> decoratorTypes, HttpClientConfig config) {
        if (baseClient == null) {
            throw new IllegalArgumentException("Base HttpClient cannot be null.");
        }
        if (decoratorTypes == null) {
            throw new IllegalArgumentException("Decorator list cannot be null.");
        }
        if (config == null) {
            throw new IllegalArgumentException("HttpClientConfig cannot be null.");
        }
        List<DecoratorType> sortedDecorators = decoratorTypes.stream()
                .distinct()
                .sorted(Comparator.comparingInt(DecoratorType::getPriority))
                .toList();

        HttpClient client = baseClient;
        for (DecoratorType decoratorType : sortedDecorators) {
            client = applyDecorator(client, decoratorType, config);
        }
        return client;
    }

    private static HttpClient applyDecorator(HttpClient client, DecoratorType decoratorType, HttpClientConfig config) {
        return switch (decoratorType) {
            case TIMEOUT -> new TimeoutHttpClientDecorator(client, config.getTimeoutMs());
            case LOGGING -> new LoggingHttpClientDecorator(client);
            case AUTH -> new AuthHeaderHttpClientDecorator(client, config.getAuthToken());
            case REQUEST_ID -> new RequestIdHttpClientDecorator(client);
            case RETRY -> new RetryHttpClientDecorator(client, config.getMaxRetries(), config.getMetrics());
            case METRICS -> new MetricsHttpClientDecorator(client, config.getMetrics());
        };
    }
}