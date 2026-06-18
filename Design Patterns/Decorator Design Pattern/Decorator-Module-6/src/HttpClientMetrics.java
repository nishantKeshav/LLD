public class HttpClientMetrics {

    private int totalRequests;
    private int successResponses;
    private int failedResponses;
    private int retryableFailures;

    public void incrementTotalRequests() {
        totalRequests++;
    }

    public void incrementSuccessResponses() {
        successResponses++;
    }

    public void incrementFailedResponses() {
        failedResponses++;
    }

    public void incrementRetryableFailures() {
        retryableFailures++;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getSuccessResponses() {
        return successResponses;
    }

    public int getFailedResponses() {
        return failedResponses;
    }

    public int getRetryableFailures() {
        return retryableFailures;
    }
}