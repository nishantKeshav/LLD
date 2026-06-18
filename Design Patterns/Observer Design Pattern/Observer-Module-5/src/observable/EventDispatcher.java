package observable;

import java.util.List;

import event.OrderEvent;
import event.OrderEventType;
import observer.OrderEventObserver;

public interface EventDispatcher {
    void subscribe(OrderEventType eventType, OrderEventObserver observer);
    void unsubscribe(OrderEventType eventType, OrderEventObserver observer);
    void publish(OrderEvent event);
    List<OrderEvent> getEventHistory();
}