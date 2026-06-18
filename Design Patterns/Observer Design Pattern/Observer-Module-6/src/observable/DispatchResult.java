package observable;

import event.OrderEvent;

import java.util.List;
import java.util.ArrayList;

public class DispatchResult {

    private final OrderEvent event;
    private final List<ObserverDispatchResult> observerResults;

    public DispatchResult(OrderEvent event, List<ObserverDispatchResult> observerResults) {
        this.event = event;
        this.observerResults = new ArrayList<>(observerResults);
    }

    public OrderEvent getEvent() {
        return event;
    }

    public List<ObserverDispatchResult> getObserverResults() {
        return new ArrayList<>(observerResults);
    }

    public long getSuccessCount() {
        return observerResults.stream()
                .filter(result -> result.getStatus() == DispatchStatus.SUCCESS)
                .count();
    }

    public long getFailedCount() {
        return observerResults.stream()
                .filter(result -> result.getStatus() == DispatchStatus.FAILED)
                .count();
    }

    public long getSkippedCount() {
        return observerResults.stream()
                .filter(result -> result.getStatus() == DispatchStatus.SKIPPED)
                .count();
    }
}
