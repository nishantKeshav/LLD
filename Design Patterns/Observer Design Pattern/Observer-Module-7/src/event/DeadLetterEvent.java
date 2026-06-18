package event;

import java.util.UUID;
import java.time.LocalDateTime;

public class DeadLetterEvent {

    private final String deadLetterId;
    private final OrderEvent event;
    private final String observerName;
    private final String reason;
    private final LocalDateTime failedAt;
    private final int attempts;
    private boolean replayed;

    public DeadLetterEvent(
            OrderEvent event,
            String observerName,
            String reason,
            int attempts
    ) {
        this.deadLetterId = UUID.randomUUID().toString();
        this.event = event;
        this.observerName = observerName;
        this.reason = reason;
        this.attempts = attempts;
        this.failedAt = LocalDateTime.now();
        this.replayed = false;
    }

    public void markReplayed() {
        this.replayed = true;
    }

    public String getDeadLetterId() {
        return deadLetterId;
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

    public int getAttempts() {
        return attempts;
    }

    public boolean isReplayed() {
        return replayed;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }

    // getters
}
