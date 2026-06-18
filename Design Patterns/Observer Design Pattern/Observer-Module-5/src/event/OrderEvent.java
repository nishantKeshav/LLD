package event;

import java.time.LocalDateTime;

public class OrderEvent {

    private final OrderEventType eventType;
    private final String orderId;
    private final double amount;
    private final String message;
    private final String customerId;
    private final OrderStatus oldStatus;
    private final OrderStatus newStatus;
    private final LocalDateTime eventTime;

    public OrderEvent(OrderEventType eventType, String orderId, double amount,
                      String message, String customerId, OrderStatus oldStatus, OrderStatus newStatus) {
        this.eventType = eventType;
        this.orderId = orderId;
        this.amount = amount;
        this.message = message;
        this.customerId = customerId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.eventTime = LocalDateTime.now();
    }

    // GETTERS
    public OrderEventType getEventType() {
        return eventType;
    }

    public String getOrderId() {
        return orderId;
    }

    public double getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
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

    @Override
    public String toString() {
        return "OrderEvent{" +
                "eventType=" + eventType +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", message='" + message + '\'' +
                ", customerId='" + customerId + '\'' +
                ", oldStatus=" + oldStatus +
                ", newStatus=" + newStatus +
                ", eventTime=" + eventTime +
                '}';
    }

}
