package observer;

import event.OrderEvent;

public interface OrderEventObserver {
    String getObserverName();
    void onEvent(OrderEvent event) throws RuntimeException;
}
