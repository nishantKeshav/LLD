public class RetryDecision {

    private final boolean shouldRetry;
    private final int maxAttempts;
    private final int delayInSeconds;
    private final String reason;

    public RetryDecision(boolean shouldRetry, int maxAttempts, int delayInSeconds, String reason) {
        this.shouldRetry = shouldRetry;
        this.maxAttempts = maxAttempts;
        this.delayInSeconds = delayInSeconds;
        this.reason = reason;
    }

    public boolean getShouldRetry() {
        return shouldRetry;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public int getDelayInSeconds() {
        return delayInSeconds;
    }

    public String getReason() {
        return reason;
    }
}
