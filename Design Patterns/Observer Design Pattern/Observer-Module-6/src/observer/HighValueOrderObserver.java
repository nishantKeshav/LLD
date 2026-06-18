package observer;

import event.OrderEvent;

public class HighValueOrderObserver implements OrderEventObserver {

    @Override
    public String getObserverName() {
        return "HighValueOrderObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        System.out.println("HIGH VALUE: Special monitoring for high value order " + event.getOrderId() + " amount " + event.getAmount());
    }
}
