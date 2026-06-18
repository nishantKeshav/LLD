package observable;

import event.OrderEvent;

import java.time.LocalDateTime;

public class DeadLetterEvent {

    private final int attempts;
    private final String reason;
    private final OrderEvent event;
    private final String observerName;
    private final LocalDateTime failedAt;

    DeadLetterEvent(OrderEvent event, String observerName, String reason, int attempts) {
        this.event = event;
        this.reason = reason;
        this.attempts = attempts;
        this.observerName = observerName;
        this.failedAt = LocalDateTime.now();
    }

    public OrderEvent getEvent() {
        return event;
    }

    public String getObserverName() {
        return observerName;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    public int getAttempts() {
        return attempts;
    }

    // constructor
    // getters
}
