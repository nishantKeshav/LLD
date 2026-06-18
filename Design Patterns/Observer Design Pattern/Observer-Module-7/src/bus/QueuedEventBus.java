package bus;

import event.DeadLetterEvent;
import event.OrderEvent;
import event.OrderEventType;
import metrics.EventBusMetrics;
import observer.ObserverRegistration;
import observer.OrderEventObserver;
import result.DispatchResult;

import java.util.List;

public interface QueuedEventBus {

    void subscribe(OrderEventType eventType, ObserverRegistration registration);

    void unsubscribe(OrderEventType eventType, OrderEventObserver observer);

    void publish(OrderEvent event);

    DispatchResult processNext();

    List<DispatchResult> processAll();

    void replayDeadLetter(String deadLetterId);

    List<OrderEvent> getPendingEvents();

    List<OrderEvent> getEventHistory();

    List<DeadLetterEvent> getDeadLetterEvents();

    EventBusMetrics getMetrics();

}
