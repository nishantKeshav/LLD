package result;

import event.OrderEventType;

public class ObserverDispatchResult {

    private final String observerName;
    private final String eventId;
    private final OrderEventType eventType;
    private final DispatchStatus status;
    private final int attempts;
    private final String errorMessage;

    public ObserverDispatchResult(
            String observerName,
            String eventId,
            OrderEventType eventType,
            DispatchStatus status,
            int attempts,
            String errorMessage
    ) {
        this.observerName = observerName;
        this.eventId = eventId;
        this.eventType = eventType;
        this.status = status;
        this.attempts = attempts;
        this.errorMessage = errorMessage;
    }

    public String getObserverName() {
        return observerName;
    }

    public String getEventId() {
        return eventId;
    }

    public OrderEventType getEventType() {
        return eventType;
    }

    public DispatchStatus getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // getters
}
