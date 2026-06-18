package observable;

import event.OrderEventType;

public class ObserverDispatchResult {
    private final String observerName;
    private final OrderEventType eventType;
    private final DispatchStatus status;
    private final int attempts;
    private final String errorMessage;

    public ObserverDispatchResult(String observerName, OrderEventType eventType, DispatchStatus status, int attempts, String errorMessage) {
        this.observerName = observerName;
        this.eventType = eventType;
        this.status = status;
        this.attempts = attempts;
        this.errorMessage = errorMessage;
    }

    public DispatchStatus getStatus() {
        return status;
    }

    public String getObserverName() {
        return observerName;
    }

    public OrderEventType getEventType() {
        return eventType;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // constructor
    // getters
}
