package event;

import observable.EventDispatcher;

import java.util.HashMap;
import java.util.Map;

public class OrderService {

    private final EventDispatcher eventDispatcher;
    private final Map<String, OrderStatus> orderStatusMap;

    public OrderService(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
        this.orderStatusMap = new HashMap<>();
    }

    public void placeOrder(String orderId, String customerId, double amount) {
        if (orderStatusMap.containsKey(orderId)) {
            throw new IllegalArgumentException("Order already exists: " + orderId);
        }

        OrderStatus oldStatus = OrderStatus.CREATED;
        OrderStatus newStatus = OrderStatus.PLACED;

        orderStatusMap.put(orderId, newStatus);

        OrderEvent event = new OrderEvent(
                OrderEventType.ORDER_PLACED,
                orderId,
                amount,
                "ORDER PLACED SUCCESSFULLY",
                customerId,
                oldStatus,
                newStatus
        );

        eventDispatcher.publish(event);
    }

    public void markPaymentSuccess(String orderId, String customerId, double amount) {
        OrderStatus oldStatus = getExistingOrderStatus(orderId);

        if (oldStatus != OrderStatus.PLACED) {
            throw new IllegalStateException(
                    "Payment can only be marked successful for PLACED orders. Current status: " + oldStatus
            );
        }

        OrderStatus newStatus = OrderStatus.PAID;

        orderStatusMap.put(orderId, newStatus);

        OrderEvent event = new OrderEvent(
                OrderEventType.PAYMENT_SUCCESS,
                orderId,
                amount,
                "PAYMENT SUCCESSFUL",
                customerId,
                oldStatus,
                newStatus
        );

        eventDispatcher.publish(event);
    }

    public void shipOrder(String orderId, String customerId, double amount) {
        OrderStatus oldStatus = getExistingOrderStatus(orderId);

        if (oldStatus != OrderStatus.PAID) {
            throw new IllegalStateException(
                    "Order can only be shipped after payment success. Current status: " + oldStatus
            );
        }

        OrderStatus newStatus = OrderStatus.SHIPPED;

        orderStatusMap.put(orderId, newStatus);

        OrderEvent event = new OrderEvent(
                OrderEventType.ORDER_SHIPPED,
                orderId,
                amount,
                "ORDER SHIPPED",
                customerId,
                oldStatus,
                newStatus
        );

        eventDispatcher.publish(event);
    }

    public void cancelOrder(String orderId, String customerId, double amount) {
        OrderStatus oldStatus = getExistingOrderStatus(orderId);

        if (oldStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled: " + orderId);
        }

        if (oldStatus == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Shipped order cannot be cancelled: " + orderId);
        }

        OrderStatus newStatus = OrderStatus.CANCELLED;

        orderStatusMap.put(orderId, newStatus);

        OrderEvent event = new OrderEvent(
                OrderEventType.ORDER_CANCELLED,
                orderId,
                amount,
                "ORDER CANCELLED",
                customerId,
                oldStatus,
                newStatus
        );

        eventDispatcher.publish(event);
    }

    private OrderStatus getExistingOrderStatus(String orderId) {
        OrderStatus status = orderStatusMap.get(orderId);

        if (status == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        return status;
    }
}