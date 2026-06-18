package observer;

import event.OrderEvent;

public interface EventFilter {
    boolean shouldProcess(OrderEvent event);
}
