package event;

import java.util.UUID;
import java.time.LocalDateTime;

public class OrderEvent {

    private final double amount;
    private final String message;
    private final String eventId;
    private final String orderId;
    private final String customerId;
    private final OrderStatus oldStatus;
    private final OrderStatus newStatus;
    private final LocalDateTime eventTime;
    private final OrderEventType eventType;

    public OrderEvent(OrderEventType eventType, String orderId, String customerId, double amount,
            OrderStatus oldStatus, OrderStatus newStatus, String message) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.message = message;
        this.eventTime = LocalDateTime.now();
    }

    public double getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    public String getEventId() {
        return eventId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public OrderStatus getOldStatus() {
        return oldStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public OrderEventType getEventType() {
        return eventType;
    }
}
