package observable;

import observer.OrderEventObserver;

public class ObserverRegistration {
    private final OrderEventObserver observer;
    private final int priority;
    private final int maxRetries;
    private final EventFilter eventFilter;

    public  ObserverRegistration(OrderEventObserver observer, int priority, int maxRetries, EventFilter eventFilter) {
        this.observer = observer;
        this.priority = priority;
        this.maxRetries = maxRetries;
        this.eventFilter = eventFilter;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public int getPriority() {
        return priority;
    }

    public EventFilter getEventFilter() {
        return eventFilter;
    }

    public OrderEventObserver getObserver() {
        return observer;
    }

    // constructor
    // getters
}
