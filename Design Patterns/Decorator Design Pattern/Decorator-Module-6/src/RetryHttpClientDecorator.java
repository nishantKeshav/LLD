public class RetryHttpClientDecorator extends HttpClientDecorator {

    private final int maxRetries;
    private final HttpClientMetrics metrics;

    public RetryHttpClientDecorator(HttpClient httpClient, int maxRetries) {
        this(httpClient, maxRetries, null);
    }

    public RetryHttpClientDecorator(HttpClient httpClient, int maxRetries, HttpClientMetrics metrics) {
        super(httpClient);

        if (maxRetries < 0) {
            throw new IllegalArgumentException("Max retries cannot be negative.");
        }

        this.maxRetries = maxRetries;
        this.metrics = metrics;
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        int attempts = 0;
        int totalAttempts = maxRetries + 1;

        HttpResponse lastResponse = null;
        Exception lastException = null;

        while (attempts < totalAttempts) {
            attempts++;

            try {
                System.out.println("RETRY: Attempt "
                        + attempts
                        + " for "
                        + request.getMethod()
                        + " "
                        + request.getUrl());

                HttpResponse response = super.execute(request);

                if (response.isSuccess()) {
                    return response;
                }

                lastResponse = response;

                System.out.println("RETRY: Attempt "
                        + attempts
                        + " returned status "
                        + response.getStatusCode());

                if (attempts < totalAttempts) {
                    incrementRetryableFailure();
                }

            } catch (Exception e) {
                lastException = e;

                System.out.println("RETRY: Attempt "
                        + attempts
                        + " failed: "
                        + e.getMessage());

                if (attempts < totalAttempts) {
                    incrementRetryableFailure();
                }
            }
        }

        if (lastResponse != null) {
            return lastResponse;
        }

        return new HttpResponse(
                500,
                "Request failed after retries: " + lastException.getMessage(),
                false
        );
    }

    private void incrementRetryableFailure() {
        if (metrics != null) {
            metrics.incrementRetryableFailures();
        }
    }
}