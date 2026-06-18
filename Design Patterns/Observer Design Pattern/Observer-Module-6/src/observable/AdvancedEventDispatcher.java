package observable;

import event.OrderEvent;
import event.OrderEventType;
import observer.OrderEventObserver;

import java.util.List;

public interface AdvancedEventDispatcher {
    void subscribe(OrderEventType eventType, ObserverRegistration registration);

    void unsubscribe(OrderEventType eventType, OrderEventObserver observer);

    DispatchResult publish(OrderEvent event);

    List<OrderEvent> getEventHistory();

    List<DeadLetterEvent> getDeadLetterEvents();
}
