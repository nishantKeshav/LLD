package observer;

import event.OrderEvent;
import event.OrderEventType;

public class InventoryObserver implements OrderEventObserver {

    @Override
    public String getObserverName() {
        return "InventoryObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        OrderEventType eventType = event.getEventType();
        switch (eventType) {
            case ORDER_PLACED -> System.out.println("INVENTORY: Stock reserved for order " + event.getOrderId());
            case ORDER_CANCELLED -> System.out.println("INVENTORY: Stock released for cancelled order " + event.getOrderId());
            default -> System.out.println("INVENTORY: Unknown event type " + eventType);
        }
    }
}
