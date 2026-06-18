package observer;

import event.OrderEvent;

public class EmailNotificationObserver implements OrderEventObserver {

    @Override
    public String getObserverName() {
        return "EmailNotificationObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        System.out.println("EMAIL: Order placed notification sent to customer "
                + event.getCustomerId()
                + " for order "
                + event.getOrderId());
    }
}