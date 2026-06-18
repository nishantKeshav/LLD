public class PaymentMetrics {

    private int totalAttempts;
    private int successCount;
    private int failureCount;

    public PaymentMetrics() {
        totalAttempts = 0;
        successCount = 0;
        failureCount = 0;
    }

    public void incrementTotalAttempts() {
        totalAttempts++;
    }

    public void incrementSuccessCount() {
        successCount++;
    }

    public void incrementFailureCount() {
        failureCount++;
    }

    public int getTotalAttempts() {
        return totalAttempts;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }
}
