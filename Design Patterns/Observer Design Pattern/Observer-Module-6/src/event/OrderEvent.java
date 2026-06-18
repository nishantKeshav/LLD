package event;

import java.time.LocalDateTime;

public class OrderEvent {
    private final OrderEventType eventType;
    private final String orderId;
    private final String customerId;
    private final double amount;
    private final OrderStatus oldStatus;
    private final OrderStatus newStatus;
    private final String message;
    private final LocalDateTime eventTime;

    public OrderEvent(OrderEventType eventType, String orderId, String customerId, double amount, OrderStatus oldStatus, OrderStatus newStatus, String message) {
        this.eventType = eventType;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.message = message;
        this.eventTime = LocalDateTime.now();
    }

    public OrderEventType getEventType() {
        return eventType;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getAmount() {
        return amount;
    }

    public OrderStatus getOldStatus() {
        return oldStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }
    // constructor
    // getters
}
