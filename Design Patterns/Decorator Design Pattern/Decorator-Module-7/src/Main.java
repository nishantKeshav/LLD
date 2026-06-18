import java.util.HashMap;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        runNormalRequestExample();
        printSeparator();

        runTimeoutRequestExample();
        printSeparator();

        runRetryOnlyExample();
        printSeparator();

        runFullChainRetryExample();
        printSeparator();

        runRandomOrderExample();
        printSeparator();

        runDuplicateDecoratorExample();
    }

    // ============================================================
    // Example 1: Normal Request
    // ============================================================

    private static void runNormalRequestExample() {
        System.out.println("Module 7 — Ex-1: Normal Request");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClientConfig config = new HttpClientConfig(
                "Bearer test-token",
                3000,
                2,
                metrics
        );

        List<DecoratorType> decorators = List.of(
                DecoratorType.TIMEOUT,
                DecoratorType.LOGGING,
                DecoratorType.AUTH,
                DecoratorType.REQUEST_ID,
                DecoratorType.METRICS
        );

        HttpClient client = DecoratorChainBuilder.build(
                new BasicHttpClient(),
                decorators,
                config
        );

        HttpRequest request = new HttpRequest(
                "https://api.example.com/orders",
                "GET",
                new HashMap<>(),
                null,
                1000
        );

        HttpResponse response = client.execute(request);

        printResponse(response);
        printMetrics(metrics);
    }

    // ============================================================
    // Example 2: Timeout Request
    // ============================================================

    private static void runTimeoutRequestExample() {
        System.out.println("Module 7 — Ex-2: Timeout Request");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClientConfig config = new HttpClientConfig(
                "Bearer test-token",
                3000,
                2,
                metrics
        );

        List<DecoratorType> decorators = List.of(
                DecoratorType.TIMEOUT,
                DecoratorType.LOGGING,
                DecoratorType.AUTH,
                DecoratorType.REQUEST_ID,
                DecoratorType.METRICS
        );

        HttpClient client = DecoratorChainBuilder.build(
                new BasicHttpClient(),
                decorators,
                config
        );

        HttpRequest timeoutRequest = new HttpRequest(
                "https://api.example.com/slow",
                "GET",
                new HashMap<>(),
                null,
                5000
        );

        HttpResponse response = client.execute(timeoutRequest);

        printResponse(response);
        printMetrics(metrics);
    }

    // ============================================================
    // Example 3: Retry Only
    // ============================================================

    private static void runRetryOnlyExample() {
        System.out.println("Module 7 — Ex-3: Retry Only Request");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClientConfig config = new HttpClientConfig(
                "Bearer test-token",
                3000,
                2,
                metrics
        );

        List<DecoratorType> decorators = List.of(
                DecoratorType.RETRY
        );

        HttpClient client = DecoratorChainBuilder.build(
                new UnstableHttpClient(),
                decorators,
                config
        );

        HttpRequest retryRequest = new HttpRequest(
                "https://api.example.com/retry",
                "POST",
                new HashMap<>(),
                "{\"orderId\":\"ORD-101\"}",
                1000
        );

        HttpResponse response = client.execute(retryRequest);

        printResponse(response);
        printMetrics(metrics);
    }

    // ============================================================
    // Example 4: Full Chain Retry
    // ============================================================

    private static void runFullChainRetryExample() {
        System.out.println("Module 7 — Ex-4: Full Chain Retry Request");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClientConfig config = new HttpClientConfig(
                "Bearer test-token",
                3000,
                2,
                metrics
        );

        List<DecoratorType> decorators = List.of(
                DecoratorType.TIMEOUT,
                DecoratorType.LOGGING,
                DecoratorType.AUTH,
                DecoratorType.REQUEST_ID,
                DecoratorType.RETRY,
                DecoratorType.METRICS
        );

        HttpClient client = DecoratorChainBuilder.build(
                new UnstableHttpClient(),
                decorators,
                config
        );

        HttpRequest retryRequest = new HttpRequest(
                "https://api.example.com/full-chain-retry",
                "POST",
                new HashMap<>(),
                "{\"orderId\":\"ORD-202\"}",
                1000
        );

        HttpResponse response = client.execute(retryRequest);

        printResponse(response);
        printMetrics(metrics);
    }

    // ============================================================
    // Example 5: Random Decorator Order
    // ============================================================

    private static void runRandomOrderExample() {
        System.out.println("Module 7 — Ex-5: Random Decorator Order Test");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClientConfig config = new HttpClientConfig(
                "Bearer test-token",
                3000,
                2,
                metrics
        );

        /*
         * Intentionally random order.
         *
         * If your DecoratorChainBuilder sorts by priority,
         * output should still behave like the safe order:
         *
         * TIMEOUT -> LOGGING -> AUTH -> REQUEST_ID -> METRICS
         */
        List<DecoratorType> decorators = List.of(
                DecoratorType.METRICS,
                DecoratorType.REQUEST_ID,
                DecoratorType.AUTH,
                DecoratorType.TIMEOUT,
                DecoratorType.LOGGING
        );

        HttpClient client = DecoratorChainBuilder.build(
                new BasicHttpClient(),
                decorators,
                config
        );

        HttpRequest request = new HttpRequest(
                "https://api.example.com/random-order",
                "GET",
                new HashMap<>(),
                null,
                1000
        );

        HttpResponse response = client.execute(request);

        printResponse(response);
        printMetrics(metrics);
    }

    // ============================================================
    // Example 6: Duplicate Decorator Test
    // ============================================================

    private static void runDuplicateDecoratorExample() {
        System.out.println("Module 7 — Ex-6: Duplicate Decorator Test");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClientConfig config = new HttpClientConfig(
                "Bearer test-token",
                3000,
                2,
                metrics
        );

        /*
         * This intentionally contains duplicates.
         *
         * If your builder uses .distinct(),
         * duplicate decorators should be applied only once.
         */
        List<DecoratorType> decorators = List.of(
                DecoratorType.TIMEOUT,
                DecoratorType.LOGGING,
                DecoratorType.AUTH,
                DecoratorType.AUTH,
                DecoratorType.REQUEST_ID,
                DecoratorType.REQUEST_ID,
                DecoratorType.METRICS
        );

        HttpClient client = DecoratorChainBuilder.build(
                new BasicHttpClient(),
                decorators,
                config
        );

        HttpRequest request = new HttpRequest(
                "https://api.example.com/duplicate-test",
                "GET",
                new HashMap<>(),
                null,
                1000
        );

        HttpResponse response = client.execute(request);

        printResponse(response);
        printMetrics(metrics);
    }

    // ============================================================
    // Utility Methods
    // ============================================================

    private static void printResponse(HttpResponse response) {
        System.out.println("Final Response: " + response.getBody());
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Success: " + response.isSuccess());
    }

    private static void printMetrics(HttpClientMetrics metrics) {
        System.out.println("Total Requests: " + metrics.getTotalRequests());
        System.out.println("Success Responses: " + metrics.getSuccessResponses());
        System.out.println("Failed Responses: " + metrics.getFailedResponses());
        System.out.println("Retryable Failures: " + metrics.getRetryableFailures());
    }

    private static void printSeparator() {
        System.out.println();
        System.out.println("--------------------------------------------------");
        System.out.println();
    }
}