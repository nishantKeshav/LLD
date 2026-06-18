package observer;

import event.OrderEvent;

public class AuditLogObserver implements OrderEventObserver {

    @Override
    public String getObserverName() {
        return "AuditLogObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        System.out.println("AUDIT: Event " + event.getEventType() + " recorded for order " + event.getOrderId());
    }
}
