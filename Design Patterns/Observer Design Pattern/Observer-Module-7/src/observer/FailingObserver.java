package observer;

import event.OrderEvent;

public class FailingObserver implements OrderEventObserver {

    @Override
    public String getObserverName() {
        return "FailingObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        throw new RuntimeException("Intentional observer failure");
    }
}
