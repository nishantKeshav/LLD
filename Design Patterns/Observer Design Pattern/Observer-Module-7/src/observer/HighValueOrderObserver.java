package observer;

import event.OrderEvent;

public class HighValueOrderObserver implements OrderEventObserver, EventFilter {

    @Override
    public String getObserverName() {
        return "HighValueOrderObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        System.out.println("HIGH VALUE: Special monitoring for high value order "
                + event.getOrderId()
                + " amount "
                + event.getAmount());
    }

    @Override
    public boolean shouldProcess(OrderEvent event) {
        return event.getAmount() > 10000;
    }
}
