package metrics;

public class EventBusMetrics {

    private int publishedEvents;
    private int processedEvents;
    private int successfulObserverCalls;
    private int failedObserverCalls;
    private int skippedObserverCalls;
    private int duplicateSkippedObserverCalls;
    private int deadLetterCount;

    public void incrementPublishedEvents() {
        publishedEvents++;
    }

    public void incrementProcessedEvents() {
        processedEvents++;
    }

    public void incrementSuccessfulObserverCalls() {
        successfulObserverCalls++;
    }

    public void incrementFailedObserverCalls() {
        failedObserverCalls++;
    }

    public void incrementSkippedObserverCalls() {
        skippedObserverCalls++;
    }

    public void incrementDuplicateSkippedObserverCalls() {
        duplicateSkippedObserverCalls++;
    }

    public void incrementDeadLetterCount() {
        deadLetterCount++;
    }

    public int getPublishedEvents() {
        return publishedEvents;
    }

    public int getProcessedEvents() {
        return processedEvents;
    }

    public int getSuccessfulObserverCalls() {
        return successfulObserverCalls;
    }

    public int getFailedObserverCalls() {
        return failedObserverCalls;
    }

    public int getSkippedObserverCalls() {
        return skippedObserverCalls;
    }

    public int getDuplicateSkippedObserverCalls() {
        return duplicateSkippedObserverCalls;
    }

    public int getDeadLetterCount() {
        return deadLetterCount;
    }

    // getters
}
