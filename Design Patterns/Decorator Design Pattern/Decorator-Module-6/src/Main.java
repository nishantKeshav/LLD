import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        runNormalRequestExample();
        System.out.println();

        runTimeoutRequestExample();
        System.out.println();

        runRetryRequestExample();
        System.out.println();

        runFullChainRetryRequestExample();
    }

    private static void runNormalRequestExample() {
        System.out.println("Module 6 — HTTP Client Decorator Ex-1: Normal Request");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClient client = buildStandardClient(metrics);

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

    private static void runTimeoutRequestExample() {
        System.out.println("Module 6 — HTTP Client Decorator Ex-2: Timeout Request");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClient client = buildStandardClient(metrics);

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

    private static void runRetryRequestExample() {
        System.out.println("Module 6 — HTTP Client Decorator Ex-3: Retry Request Only");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClient client = new UnstableHttpClient();
        client = new RetryHttpClientDecorator(client, 2, metrics);

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

    private static void runFullChainRetryRequestExample() {
        System.out.println("Module 6 — HTTP Client Decorator Ex-4: Full Chain Retry Request");

        HttpClientMetrics metrics = new HttpClientMetrics();

        HttpClient client = new UnstableHttpClient();

        client = new TimeoutHttpClientDecorator(client, 3000);
        client = new LoggingHttpClientDecorator(client);
        client = new AuthHeaderHttpClientDecorator(client, "Bearer test-token");
        client = new RequestIdHttpClientDecorator(client);
        client = new RetryHttpClientDecorator(client, 2, metrics);
        client = new MetricsHttpClientDecorator(client, metrics);

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

    private static HttpClient buildStandardClient(HttpClientMetrics metrics) {
        HttpClient client = new BasicHttpClient();

        client = new TimeoutHttpClientDecorator(client, 3000);
        client = new LoggingHttpClientDecorator(client);
        client = new AuthHeaderHttpClientDecorator(client, "Bearer test-token");
        client = new RequestIdHttpClientDecorator(client);
        client = new MetricsHttpClientDecorator(client, metrics);

        return client;
    }

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
}