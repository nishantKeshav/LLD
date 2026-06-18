public class HttpClientConfig {

    private final int timeoutMs;
    private final int maxRetries;
    private final String authToken;
    private final HttpClientMetrics metrics;

    public HttpClientConfig(String authToken, int timeoutMs, int maxRetries, HttpClientMetrics metrics) {
        if (authToken == null || authToken.isBlank()) {
            throw new IllegalArgumentException("authToken cannot be null or empty.");
        }
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeoutMs cannot be negative.");
        }
        if (maxRetries < 0) {
            throw new IllegalArgumentException("maxRetries cannot be negative.");
        }
        if (metrics == null) {
            throw new IllegalArgumentException("HttpClientMetrics cannot be null.");
        }
        this.authToken = authToken;
        this.timeoutMs = timeoutMs;
        this.maxRetries = maxRetries;
        this.metrics = metrics;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public String getAuthToken() {
        return authToken;
    }

    public HttpClientMetrics getMetrics() {
        return metrics;
    }

}
